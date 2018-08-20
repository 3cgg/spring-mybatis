package me.libme.module.spring.mybatis;

/**
 * Created by J on 2018/5/7.
 */
public interface EntityOnDeleteListener {


    void onDelete(IEntityModel entityModel, SessionUser sessionUser);


}
