package me.libme.module.spring.mybatis;

/**
 * Created by J on 2018/5/7.
 */
public interface EntityOnUpdateListener {


    void onUpdate(IEntityModel entityModel, SessionUser sessionUser);


}
