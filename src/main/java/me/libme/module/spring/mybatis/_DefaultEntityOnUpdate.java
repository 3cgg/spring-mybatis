package me.libme.module.spring.mybatis;

import java.util.Date;

/**
 * Created by J on 2018/5/7.
 */
public class _DefaultEntityOnUpdate implements EntityOnUpdateListener{

    @Override
    public void onUpdate(IEntityModel entityModel, SessionUser authorizer) {
        entityModel.set_update_id(authorizer.getId());
        entityModel.set_update_time(new Date());
    }

    
}
