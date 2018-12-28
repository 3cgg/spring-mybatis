package me.libme.module.spring.mybatis;

import java.io.Serializable;

/**
 * Created by J on 2018/8/15.
 */
public interface IMybatisSingleEntityAccess<T extends IEntityModel, ID extends Serializable> {

    @SingleAccess(listener = EntityOnSaveListener.class)
    void saveOnly(T model);

    @SingleAccess(listener = EntityOnUpdateListener.class)
    void updateOnly(T model);

    @SingleAccess(listener = EntityOnDeleteListener.class)
    void delete(ID id);

    void deletePermanently(ID id);

    T getById(ID id);

}
