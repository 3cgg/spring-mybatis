package me.libme.module.spring.mybatis.fn.vc;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;

import java.util.List;

/**
 * Created by J on 2018/12/28.
 */
public class SimpleUpdateSqlValidator implements UpdateSqlValidator {

    @Override
    public boolean validate(String sql) {

        try{
            Statement statement = CCJSqlParserUtil.parse(sql);
            Update update=(Update)statement;
            List<Column> columns=update.getColumns();
            columns.forEach(column -> {
                String name=column.getColumnName();
                if("version".equalsIgnoreCase(name)){
                    throw new IllegalStateException("["+sql+"] cannot contain version column.");
                }
            });
        }catch (RuntimeException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return true;


    }

}
