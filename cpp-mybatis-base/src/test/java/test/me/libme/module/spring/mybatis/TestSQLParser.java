package test.me.libme.module.spring.mybatis;

import me.libme.module.spring.mybatis.fn.vc.SimpleUpdateSqlValidator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;

import java.util.List;

/**
 * Created by J on 2018/12/27.
 */
public class TestSQLParser {


    private static void testSql() throws Exception{
        //String sql="select * from user where id=:id and version=:version and name=:name";

        //String sql="update user set name=:name,version=:version+1 where id=:id and version=:version and name=:name";

        String sql="update user set name=:name where id=:id and name=:name";


        Statement statement = CCJSqlParserUtil.parse(sql);

        Update update=(Update)statement;

        List<Column>  columns=update.getColumns();

        Column column=new Column("version");
        columns.add(column);
        update.setColumns(columns);

        List<Expression> updateValues = update.getExpressions();
        Expression expression=CCJSqlParserUtil.parseExpression(":version+1");
        updateValues.add(expression);
        update.setExpressions(updateValues);

        Expression whereExpression=update.getWhere();

        String whereSql=whereExpression.toString();
        whereSql=whereSql+" and version=:version";

        Expression finalWhereExpression=CCJSqlParserUtil.parseCondExpression(whereSql);

        update.setWhere(finalWhereExpression);

        System.out.println(statement);

    }


    private static void testSqlValidate() throws Exception{
        String sql="update user set name=:name,version=3 where id=:id and name=:name";

        boolean pass=new SimpleUpdateSqlValidator().validate(sql);

        System.out.println(pass);

    }

    public static void main(String[] args) throws Exception {


        testSqlValidate();

    }



}
