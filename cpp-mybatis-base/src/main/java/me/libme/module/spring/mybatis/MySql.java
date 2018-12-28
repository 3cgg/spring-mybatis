package me.libme.module.spring.mybatis;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;

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

}
