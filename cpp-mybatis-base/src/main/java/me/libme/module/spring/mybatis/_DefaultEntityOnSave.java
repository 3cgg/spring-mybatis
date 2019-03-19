package me.libme.module.spring.mybatis;

import me.libme.kernel._c.util.JStringUtils;
import me.libme.kernel._c.util.JUniqueUtils;

import java.util.Date;

/**
 * Created by J on 2018/5/7.
 */
public class _DefaultEntityOnSave implements EntityOnSaveListener {

    @Override
    public void onSave(IEntityModel entityModel, SessionUser authorizer) {

        entityModel.set_version(0);
        entityModel.set_create_id(authorizer.getId());
        entityModel.set_create_time(new Date());
        entityModel.set_update_id(authorizer.getId());
        entityModel.set_update_time(new Date());
        entityModel.set_deleted("N");

        if(JStringUtils.isNullOrEmpty(entityModel.get_id())){
            entityModel.set_id(JUniqueUtils.unique());
        }
        
    }

    
}
