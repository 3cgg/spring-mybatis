package me.libme.module.spring.mybatis;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by J on 2018/5/7.
 */
@Component
public class _DefaultEntityOnSave implements EntityOnSaveListener {

    @Override
    public void onSave(IEntityModel entityModel, SessionUser authorizer) {

        entityModel.set_version(0);
        entityModel.set_create_id(authorizer.getId());
        entityModel.set_create_time(new Date());
        entityModel.set_update_id(authorizer.getId());
        entityModel.set_update_time(new Date());
        entityModel.set_deleted("N");
        
    }

    
}
