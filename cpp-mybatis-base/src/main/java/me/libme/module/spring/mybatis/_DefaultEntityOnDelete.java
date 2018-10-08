package me.libme.module.spring.mybatis;

/**
 * Created by J on 2018/5/7.
 */
public class _DefaultEntityOnDelete implements EntityOnDeleteListener{

    @Override
    public void onDelete(IEntityModel entityModel, SessionUser sessionUser) {
        entityModel.set_deleted("Y");
    }
}
