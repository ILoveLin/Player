package com.company.shenzhou.playerdb;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.company.shenzhou.bean.dbbean.DeviceDBBean;
import com.company.shenzhou.bean.dbbean.UserDBBean;
import com.company.shenzhou.bean.dbbean.UserDBRememberBean;

import com.company.shenzhou.playerdb.DeviceDBBeanDao;
import com.company.shenzhou.playerdb.UserDBBeanDao;
import com.company.shenzhou.playerdb.UserDBRememberBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig deviceDBBeanDaoConfig;
    private final DaoConfig userDBBeanDaoConfig;
    private final DaoConfig userDBRememberBeanDaoConfig;

    private final DeviceDBBeanDao deviceDBBeanDao;
    private final UserDBBeanDao userDBBeanDao;
    private final UserDBRememberBeanDao userDBRememberBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        deviceDBBeanDaoConfig = daoConfigMap.get(DeviceDBBeanDao.class).clone();
        deviceDBBeanDaoConfig.initIdentityScope(type);

        userDBBeanDaoConfig = daoConfigMap.get(UserDBBeanDao.class).clone();
        userDBBeanDaoConfig.initIdentityScope(type);

        userDBRememberBeanDaoConfig = daoConfigMap.get(UserDBRememberBeanDao.class).clone();
        userDBRememberBeanDaoConfig.initIdentityScope(type);

        deviceDBBeanDao = new DeviceDBBeanDao(deviceDBBeanDaoConfig, this);
        userDBBeanDao = new UserDBBeanDao(userDBBeanDaoConfig, this);
        userDBRememberBeanDao = new UserDBRememberBeanDao(userDBRememberBeanDaoConfig, this);

        registerDao(DeviceDBBean.class, deviceDBBeanDao);
        registerDao(UserDBBean.class, userDBBeanDao);
        registerDao(UserDBRememberBean.class, userDBRememberBeanDao);
    }
    
    public void clear() {
        deviceDBBeanDaoConfig.clearIdentityScope();
        userDBBeanDaoConfig.clearIdentityScope();
        userDBRememberBeanDaoConfig.clearIdentityScope();
    }

    public DeviceDBBeanDao getDeviceDBBeanDao() {
        return deviceDBBeanDao;
    }

    public UserDBBeanDao getUserDBBeanDao() {
        return userDBBeanDao;
    }

    public UserDBRememberBeanDao getUserDBRememberBeanDao() {
        return userDBRememberBeanDao;
    }

}
