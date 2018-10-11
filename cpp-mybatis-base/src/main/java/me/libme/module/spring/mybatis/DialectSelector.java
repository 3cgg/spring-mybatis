package me.libme.module.spring.mybatis;

import me.libme.kernel._c._i.JFinder;

/**
 * Created by J on 2018/10/11.
 */
public class DialectSelector implements JFinder<MybatisDialect> {


    @Override
    public MybatisDialect find() {

        String mysqlClass="com.mysql.jdbc.Driver";

        String oracleClass="oracle.jdbc.driver.OracleDriver";

        boolean mysql=false;
        boolean oracle=false;

        ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
        try {
            classLoader.loadClass(mysqlClass);
            mysql=true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            classLoader.loadClass(oracleClass);
            oracle=true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
