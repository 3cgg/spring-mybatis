package me.libme.module.spring.mybatis;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.List;

/**
 * Created by J on 2018/12/28.
 */
public abstract class BaseMybatisDialect implements MybatisDialect {

    private Object[] versionInfo(BoundSql boundSql,Object parameterObject){
        IEntityModel entityModel=(IEntityModel)parameterObject;
        return new Object[]{"version",entityModel.get_version()};
    }

    @Override
    public String versionSql(MappedStatement ms, BoundSql boundSql, Object parameterObject) {
        Object[] version=versionInfo(boundSql,parameterObject);
        return versionSql0(boundSql.getSql(),(String) version[0],(Integer) version[1]);
    }

    protected String versionSql0(String sql, String versionColumn, int version){

//        String sql="update user set name=:name where id=:id and name=:name";

        try{
            Statement statement = CCJSqlParserUtil.parse(sql);

            Update update=(Update)statement;

            List<Column> columns=update.getColumns();

            Column column=new Column(versionColumn);
            columns.add(column);
            update.setColumns(columns);

            List<Expression> updateValues = update.getExpressions();
            Expression expression=CCJSqlParserUtil.parseExpression(""+(version+1));
            updateValues.add(expression);
            update.setExpressions(updateValues);

            Expression whereExpression=update.getWhere();

            String whereSql=whereExpression.toString();
            whereSql=whereSql+" and "+versionColumn+"="+version;

            Expression finalWhereExpression=CCJSqlParserUtil.parseCondExpression(whereSql);

            update.setWhere(finalWhereExpression);

            return update.toString();
        }catch (Exception e){
            throw new RuntimeException(e);
        }



    }





}
