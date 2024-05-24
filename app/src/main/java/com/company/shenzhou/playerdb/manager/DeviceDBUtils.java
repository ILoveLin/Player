package com.company.shenzhou.playerdb.manager;

import android.content.Context;

import com.company.shenzhou.app.AppApplication;
import com.company.shenzhou.bean.dbbean.DeviceDBBean;
import com.company.shenzhou.bean.dbbean.DownBindNameListBean;
import com.company.shenzhou.playerdb.DeviceDBBeanDao;
import com.company.shenzhou.utlis.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * LoveLin
 * <p>
 * Describe 设备视频链接记录表--的CURD操作工具类
 */
public class DeviceDBUtils {
    /**
     * 增  --插入数据
     * insert： 会进行去重，保存第一次的数据，也就是不会进行更新。至于是 由于主键去重，还是有重复的元素就去我还会在看看
     * insertOrReplace：  会去重，保存最新的数据，也就是会进行更新
     * save：  不会去重，保存所有数据
     *
     * @param bean
     */
    public static void insertData(DeviceDBBean bean) {
        AppApplication.getDaoSession().insert(bean);
    }

    /**
     * 增  --数据存在则替换，数据不存在则插入
     *
     * @param bean
     */
    public static void insertOrReplaceData(Context context, DeviceDBBean bean) {
        AppApplication.getDaoSession().getDeviceDBBeanDao().insertOrReplace(bean);
    }

    //插入或者替换,如果没有,插入,如果有,替换  --传入的对象主键如果存在于数据库中，有则更新，否则插入
    public static void insertOrReplaceInTx(Context context, DeviceDBBean bean) {
        AppApplication.getDaoSession().getDeviceDBBeanDao().insertOrReplaceInTx(bean);
    }

    //插入或者替换,如果没有,插入,如果有,替换
    public static void insertOrReplace(Context context, DeviceDBBean bean) {
        AppApplication.getDaoSession().getDeviceDBBeanDao().insertOrReplace(bean);

    }

    /**
     * 删--delete()和deleteAll()；分别表示删除单个和删除所有。
     */
    //更新
    public static void update(Context context, DeviceDBBean bean) {
        AppApplication.getDaoSession().getDeviceDBBeanDao().update(bean);
    }

    //删除
    public static void deleteData(Context context, DeviceDBBean bean) {
        AppApplication.getDaoSession().getDeviceDBBeanDao().delete(bean);
    }

    public static void deleteAllData(Class clazz) {
        AppApplication.getInstance().getDaoSession().deleteAll(clazz);

    }


    /**
     * 改--通过update来进行修改：
     * update之前必须先设置他本身之前的id不然crash
     */


    public static void updateData(Context context, DeviceDBBean bean) {
        AppApplication.getDaoSession().getDeviceDBBeanDao().update(bean);

    }


    //条件查询--其他设备
    //精确查询  获取到bean
    //存入数据库的标识;devicecode(16位设备码)+devicetype(中文说明)
    //备注:因为存入数据库的是16位设备码 所以需要把socket协议的32位转换成16位
    public static DeviceDBBean getQueryBeanByAcceptAndInsertDB(Context context, String tag) {
        DeviceDBBeanDao deviceDBBeanDao = AppApplication.getDaoSession().getDeviceDBBeanDao();
        if (null!=DeviceDBBeanDao.Properties.AcceptAndInsertDB){
            List<DeviceDBBean> list = deviceDBBeanDao.queryBuilder().where(DeviceDBBeanDao.Properties.AcceptAndInsertDB.eq(tag)).list();
            if (null == list) {
                return null;
            } else {
                for (int i = 0; i < list.size(); i++) {
                    DeviceDBBean deviceDBBean = list.get(i);
                    boolean deviceCode = (tag).equals(deviceDBBean.getAcceptAndInsertDB());     // code 存在并且,type存在--返回bean对象,说明数据库有该条数据
                    if (deviceCode) {
                        return deviceDBBean;
                    } else {
                        return null;
                    }
                }


            }
        }

        return null;

    }

    //条件查询  --RC200 设备
    //精确查询  获取到bean
    //依次存入deviceCode,deviceType,因为是RC200没有设备id,只有设备类型,所以这里我们用当前可用ip=deviceType,比如:192.168.71.159=RC200
    public static DeviceDBBean getQueryRC200BeanByAcceptAndInsertDB(Context context, String tag) {
        DeviceDBBeanDao deviceDBBeanDao = AppApplication.getDaoSession().getDeviceDBBeanDao();
        if (null!=DeviceDBBeanDao.Properties.AcceptAndInsertDB){
            List<DeviceDBBean> list = deviceDBBeanDao.queryBuilder().where(DeviceDBBeanDao.Properties.AcceptAndInsertDB.eq(tag)).list();
            if (null == list) {
                return null;
            } else {
                for (int i = 0; i < list.size(); i++) {
                    DeviceDBBean deviceDBBean = list.get(i);
                    boolean deviceCode = (tag).equals(deviceDBBean.getAcceptAndInsertDB());     // code 存在并且,type存在--返回bean对象,说明数据库有该条数据
                    if (deviceCode) {
                        return deviceDBBean;
                    } else {
                        return null;
                    }
                }


            }
        }
        return null;

    }


    /**
     * 查询
     * loadAll()：查询所有数据。
     * queryRaw()：根据条件查询
     * queryBuilder() : 方便查询的创建，后面详细讲解。
     */


    //查询全部
    public static List<DeviceDBBean> queryAll(Context context) {
        //查询所有数据
        try {
            List<DeviceDBBean> list = AppApplication.getDaoSession().getDeviceDBBeanDao().queryBuilder().list();
            return list;
        } catch (Exception e) {
            LogUtils.e("数据库设备列表数据===全部===queryAll=Exception:" + e);

            return null;

        }
    }

    public static List queryRaw(Long id) {

        List<DeviceDBBean> beanLis = (List<DeviceDBBean>)
                AppApplication.getInstance().getDaoSession().queryRaw(DeviceDBBean.class, " where id = ?", id + "");
        return beanLis;
    }

    public static List queryRawTag(String tag) {

        List<DeviceDBBean> beanLis = (List<DeviceDBBean>)
                AppApplication.getInstance().getDaoSession().queryRaw(DeviceDBBean.class, " where tag = ?", tag + "");
        return beanLis;
    }

    //条件查询
    //精确查询  获取到bean
    public static List<DeviceDBBean> getQueryBeanByTag(Context context, String tag) {
        DeviceDBBeanDao deviceDBBeanDao = AppApplication.getDaoSession().getDeviceDBBeanDao();
        if (null!=DeviceDBBeanDao.Properties.AcceptAndInsertDB){
            List<DeviceDBBean> list = deviceDBBeanDao.queryBuilder().where(DeviceDBBeanDao.Properties.AcceptAndInsertDB.eq(tag)).list();
            return list;

        }else {
            return null;
        }

    }


    //条件查询

    /**
     * 根据当前登入用户的用户名的indexBean
     * 返还当前登入用户下所有绑定的设备
     *
     * @param context
     * @param indexBean DownBindNameListBean 用户名bean
     * @return 返还当前登入用户下所有绑定的设备
     */
    public static List<DeviceDBBean> getQueryBeanByNameBean(Context context, DownBindNameListBean indexBean) {
        //查询所有数据
        List<DeviceDBBean> list = AppApplication.getDaoSession().getDeviceDBBeanDao().queryBuilder().list();
        ArrayList<DeviceDBBean> deviceDBBeansList = new ArrayList<>();
        if (list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                DeviceDBBean deviceDBBean = list.get(i);
                List<DownBindNameListBean> downBingNameList = deviceDBBean.getDownBingNameList();
                if (downBingNameList != null) {
                    boolean contains = downBingNameList.contains(indexBean);
                    if (contains) {    //存在
                        deviceDBBeansList.add(deviceDBBean);
                    }
                } else {
                    //使用 MySQLiteOpenHelper 数据库迁移,存在更改字段之后,之前版本AppApplication设备表字段为null的bug
                    //解决字段更改之后的bug
                    LogUtils.e("总长度====数据库更新之后字段==null");
                    //数据库更新这个字段就会变成null,此时默认tag的名字就是DownBindNameListBean 字段数据
                    ArrayList<DownBindNameListBean> downNameList = new ArrayList<>();
                    DownBindNameListBean nameBean = new DownBindNameListBean();
                    //绑定谁添加的设备--用户名
                    String currentUsername = deviceDBBean.getTag();
                    nameBean.setDownBindName(currentUsername);
                    downNameList.add(nameBean);
                    deviceDBBean.setDownBingNameList(downNameList);
                    //更新数据
                    insertOrReplaceData(context, deviceDBBean);
                    //添加数据
                    deviceDBBeansList.add(deviceDBBean);
                }
            }
            LogUtils.e("总长度======deviceDBBeansList=" + deviceDBBeansList.size());
            return deviceDBBeansList;
        }
        return deviceDBBeansList;


    }


    /**
     * 根据当前登入用户的用户名的indexBean
     * 返还当前登入用户下所有绑定的设备
     *
     * @param context
     * @param indexBean DownBindNameListBean 用户名bean
     * @return 返还当前登入用户下所有绑定的设备
     */
    public static List<DeviceDBBean> getQueryBeanByNameBean01(Context context, DownBindNameListBean indexBean) {
        //查询所有数据
        List<DeviceDBBean> list = AppApplication.getDaoSession().getDeviceDBBeanDao().queryBuilder().list();
        ArrayList<DeviceDBBean> deviceDBBeans = new ArrayList<>();
        if (list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                DeviceDBBean deviceDBBean = list.get(i);
                List<DownBindNameListBean> downBingNameList = deviceDBBean.getDownBingNameList();
                if (downBingNameList != null) {
                    boolean contains = downBingNameList.contains(indexBean);
                    if (contains) {
                        //存在
                        deviceDBBeans.add(deviceDBBean);
                    }
                } else {
                    //解决字段更改之后的bug
                    LogUtils.e("总长度====数据库更新之后字段==null");
                    //数据库更新这个字段就会变成null,此时默认tag的名字就是DownBindNameListBean 字段数据
                    ArrayList<DownBindNameListBean> downNameList = new ArrayList<>();
                    DownBindNameListBean nameBean = new DownBindNameListBean();
                    //绑定谁添加的设备--用户名
                    String currentUsername = deviceDBBean.getTag();
                    nameBean.setDownBindName(currentUsername);
                    downNameList.add(nameBean);
                    deviceDBBean.setDownBingNameList(downNameList);
                    insertOrReplaceData(context, deviceDBBean);

                }
            }
            return deviceDBBeans;
        }
        return deviceDBBeans;


    }

}
