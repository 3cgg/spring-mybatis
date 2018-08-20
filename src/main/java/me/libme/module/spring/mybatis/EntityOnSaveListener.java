package me.libme.module.spring.mybatis;

/**
 * Created by J on 2018/5/7.
 */
public interface EntityOnSaveListener {


    void onSave(IEntityModel entityModel, SessionUser sessionUser);


}
