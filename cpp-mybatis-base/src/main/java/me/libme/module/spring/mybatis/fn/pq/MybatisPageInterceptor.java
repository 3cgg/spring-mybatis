/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.libme.module.spring.mybatis.fn.pq;

import me.libme.kernel._c._m.JPage;
import me.libme.kernel._c._m.JPageUtil;
import me.libme.kernel._c._m.JPageable;
import me.libme.module.spring.mybatis.MybatisDialect;
import me.libme.module.spring.mybatis.sql.CountSqlParser;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author J
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Intercepts(
    {
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
    }
)
public class MybatisPageInterceptor implements Interceptor {

    private static final Logger LOGGER= LoggerFactory.getLogger(MybatisPageInterceptor.class);

    private CountSqlParser countSqlParser=new CountSqlParser();

    private String countSuffix = "_COUNT";

    private final MybatisDialect mybatisDialect;

    public MybatisPageInterceptor(MybatisDialect mybatisDialect) {
        this.mybatisDialect = mybatisDialect;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Object parameter = args[1];
            RowBounds rowBounds = (RowBounds) args[2];
            ResultHandler resultHandler = (ResultHandler) args[3];
            Executor executor = (Executor) invocation.getTarget();
            CacheKey cacheKey;
            BoundSql boundSql;
            if(args.length == 4){
                //4个参数时
                boundSql = ms.getBoundSql(parameter);
                cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
            } else {
                //6个参数时
                cacheKey = (CacheKey) args[4];
                boundSql = (BoundSql) args[5];
            }
            List resultList=new ArrayList();
            String msId = ms.getId();
            Configuration configuration = ms.getConfiguration();
            JPageable pageable;
            if((pageable=pageable(parameter))!=null){
                String countMsId = msId + countSuffix;
                MappedStatement countMs = getExistedMappedStatement(configuration, countMsId);
                Long count;
                if(countMs != null){
                    count = executeManualCount(executor, countMs, parameter, boundSql, resultHandler);
                }else {
                    countMs = newCountMappedStatement(ms, countMsId);
                    count = executeAutoCount(executor, countMs, parameter, boundSql, rowBounds, resultHandler);
                }

                String pageSql = mybatisDialect.pageSql(ms, boundSql, parameter, rowBounds, cacheKey);

                // set new pageable parameters
                Map<String,Object> pageParam=mybatisDialect.pageSqlParameter(ms, boundSql,parameter,rowBounds,cacheKey,count,pageable);
                BoundSql pageBoundSql = new BoundSql(configuration, pageSql, boundSql.getParameterMappings(), pageParam);
                pageParam.entrySet().stream().forEach(
                        (entry)-> pageBoundSql.setAdditionalParameter(entry.getKey(),entry.getValue()));
                List list = executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, pageBoundSql);


                JPage page=JPageUtil.wrap(list,count.intValue(),pageable);
                resultList.add(page);

            }else {
                resultList = executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);

            }
            return resultList;
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }

    }



    private JPageable pageable(Object parameter){

        if(JPageable.class.isInstance(parameter)){
            return (JPageable) parameter;
        }

        if(Map.class.isInstance(parameter)){
            JPageable pageable=(JPageable) ((Map)parameter).values().stream().filter(
                    object-> JPageable.class.isInstance(object)).findFirst()
                    .orElse(null);
            return pageable;
        }

        return null;
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 执行自动生成的 count 查询
     *
     * @param executor
     * @param countMs
     * @param parameter
     * @param boundSql
     * @param rowBounds
     * @param resultHandler
     * @return
     * @throws IllegalAccessException
     * @throws SQLException
     */
    private Long executeAutoCount(Executor executor, MappedStatement countMs,
                                  Object parameter, BoundSql boundSql,
                                  RowBounds rowBounds, ResultHandler resultHandler) throws IllegalAccessException, SQLException {
         //调用方言获取 count sql
        String countSql = countSqlParser.getSmartCountSql(boundSql.getSql());
        //创建 count 查询的缓存 key
        CacheKey countKey = executor.createCacheKey(countMs, parameter, RowBounds.DEFAULT, boundSql);

        //countKey.update(countSql);
        BoundSql countBoundSql = new BoundSql(countMs.getConfiguration(), countSql, boundSql.getParameterMappings(), parameter);

        //执行 count 查询
        Object countResultList = executor.query(countMs, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql);
        Long count = (Long) ((List) countResultList).get(0);
        return count;
    }

    private Long executeManualCount(Executor executor, MappedStatement countMs,
                                    Object parameter, BoundSql boundSql,
                                    ResultHandler resultHandler) throws IllegalAccessException, SQLException {
        CacheKey countKey = executor.createCacheKey(countMs, parameter, RowBounds.DEFAULT, boundSql);
        BoundSql countBoundSql = countMs.getBoundSql(parameter);
        Object countResultList = executor.query(countMs, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql);
        Long count = ((Number) ((List) countResultList).get(0)).longValue();
        return count;
    }

    /**
     * 尝试获取已经存在的在 MS，提供对手写count和page的支持
     *
     * @param configuration
     * @param msId
     * @return
     */
    private MappedStatement getExistedMappedStatement(Configuration configuration, String msId){
        MappedStatement mappedStatement = null;
        try {
            mappedStatement = configuration.getMappedStatement(msId, false);
        } catch (Throwable t){
            //ignore
        }
        return mappedStatement;
    }

    private MappedStatement newCountMappedStatement(MappedStatement ms, String newMsId) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), newMsId, ms.getSqlSource(), ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        //count查询返回值int
        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), Long.class, new ArrayList()).build();
        resultMaps.add(resultMap);
        builder.resultMaps(resultMaps);
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

}
