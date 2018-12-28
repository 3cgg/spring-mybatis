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

package me.libme.module.spring.mybatis.fn.vc;

import me.libme.module.spring.mybatis.IEntityModel;
import me.libme.module.spring.mybatis.MybatisDialect;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author J
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Intercepts(
    {
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
    }
)
public class OptimisticLockerInterceptor implements Interceptor {

    private static final Logger LOGGER= LoggerFactory.getLogger(OptimisticLockerInterceptor.class);

    private final MybatisDialect mybatisDialect;

    private UpdateSqlValidator updateSqlValidator;

    public OptimisticLockerInterceptor(MybatisDialect mybatisDialect,UpdateSqlValidator updateSqlValidator) {
        this.mybatisDialect = mybatisDialect;
        this.updateSqlValidator=updateSqlValidator;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            String sqlId=ms.getId();
            if(sqlId.endsWith(".updateOnly")
//                    ||sqlId.endsWith(".delete")
                    ){ // update single record , do  checking version
                Object parameter = args[1];

                BoundSql boundSql = ms.getBoundSql(parameter);

                updateSqlValidator.validate(boundSql.getSql());

                String versionSql=mybatisDialect.versionSql(ms,boundSql,parameter);
                List<ParameterMapping> parameterMappings=boundSql.getParameterMappings();
                Object parameterObject=boundSql.getParameterObject();

                BoundSql versionBoundSql=new BoundSql(ms.getConfiguration(),versionSql,parameterMappings,parameterObject);

                MappedStatement newMappedStatement=newMappedStatement(ms,new BoundSqlSqlSource(versionBoundSql));

                args[0]=newMappedStatement;
                Integer result= (Integer)invocation.proceed();
                if(result==0) {
                    IEntityModel entityModel=(IEntityModel)parameter;
                    throw new ConcurrentModificationException("record ["+entityModel.getClass().getSimpleName()+":"+entityModel.get_id()+"] may be modified by others.");
                }
                return result;
            }else{
                return invocation.proceed();
            }

        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }

    }


    private MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder =
                new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
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
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }


    class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;
        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }
        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }



    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
