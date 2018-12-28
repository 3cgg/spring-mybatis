package me.libme.module.spring.mybatis;

import me.libme.kernel._c._i.JFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by J on 2018/10/11.
 */
public class DialectSelector implements JFinder<MybatisDialect> {

    private static final Logger LOGGER= LoggerFactory.getLogger(DialectSelector.class);


    @Override
    public MybatisDialect find() {

        boolean mysql=false;
        boolean oracle=false;

        ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
        try {
            String mysqlClass="com.mysql.jdbc.Driver";
            classLoader.loadClass(mysqlClass);
            mysql=true;
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage(),e);
        }

        try {
            String oracleClass="oracle.jdbc.driver.OracleDriver";
            classLoader.loadClass(oracleClass);
            oracle=true;
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage(),e);
        }

        try {
            String oracleClass="oracle.jdbc.OracleDriver";
            classLoader.loadClass(oracleClass);
            oracle=true;
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage(),e);
        }

        if(mysql){
            return new MySql();
        }

        if(oracle){
            return new Oracle();
        }

        return null;

    }


}
