package me.libme.module.spring.mybatis;

import me.libme.kernel._c._m.JPageable;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by J on 2018/8/16.
 */
public class MySql extends BaseMybatisDialect {

    @Override
    public String pageSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey) {
        String sql=boundSql.getSql();
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
        sqlBuilder.append(sql);
        sqlBuilder.append(" LIMIT ?, ? ");
        return sqlBuilder.toString();

    }

    @Override
    public Map<String,Object> pageSqlParameter(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey cacheKey, Long count, JPageable pageable) {
        Map<String,Object> param=new HashMap<>();

        int pageNumber=pageable.getPageNumber();
        int pageSize=pageable.getPageSize();

        int offset=(pageNumber)*pageSize;

        List<ParameterMapping> newParameterMappings = new ArrayList<>();

        newParameterMappings.addAll(boundSql.getParameterMappings());

        newParameterMappings.add(
                new ParameterMapping.Builder(ms.getConfiguration(), "sccOffset", Integer.class).build()
        );
        newParameterMappings.add(
                new ParameterMapping.Builder(ms.getConfiguration(), "sccPageSize", Integer.class).build()
        );

        MetaObject metaObject = SystemMetaObject.forObject(boundSql);
        metaObject.setValue("parameterMappings", newParameterMappings);

        param.put("sccOffset",offset);
        param.put("sccPageSize",pageSize);

        return param;

    }

}
