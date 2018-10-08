package me.libme.module.spring.mybatis;

import me.libme.kernel._c._m.JPageable;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;

import java.util.Map;

/**
 * Created by J on 2018/8/16.
 */
public interface MybatisDialect {


    String pageSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey);


    Map<String,Object> pageSqlParameter(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey cacheKey, Long count, JPageable pageable);


}
