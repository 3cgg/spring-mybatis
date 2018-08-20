package me.libme.module.spring.mybatis;

import me.libme.kernel._c._m.JModel;

import java.util.Date;


/**
 *
 */
public interface IEntityModel extends JModel {

    default String get_id(){return null;}

    default void set_id(String id){}

    default String get_create_id(){return null;}

    default void set_create_id(String createId){}

    default String get_update_id(){return null;}

    default void set_update_id(String updateId){}

    default Date get_create_time(){return null;}

    default void set_create_time(Date createTime){}

    default Date get_update_time(){return null;}

    default void set_update_time(Date updateTime){}

    default String get_deleted(){return null;}

    default void set_deleted(String deleted){}

    default int get_version(){return -1;}

    default void set_version(Integer version){}

}
