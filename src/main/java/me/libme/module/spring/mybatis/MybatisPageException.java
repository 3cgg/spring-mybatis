package me.libme.module.spring.mybatis;

/**
 * Created by J on 2018/8/16.
 */
public class MybatisPageException extends RuntimeException {
    public MybatisPageException() {
        super();
    }

    public MybatisPageException(String message) {
        super(message);
    }

    public MybatisPageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MybatisPageException(Throwable cause) {
        super(cause);
    }
}
