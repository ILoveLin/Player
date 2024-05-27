package com.company.shenzhou.playerdb.manager;

import android.util.Log;

import com.company.shenzhou.app.AppApplication;
import com.company.shenzhou.bean.dbbean.UserDBRememberBean;
import com.company.shenzhou.playerdb.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * LoveLin
 * <p>
 * Describe 用户表的CURD操作工具类
 */
public class UserDBBeanUtils {
    /**
     * 增  --插入数据
     * insert： 会进行去重，保存第一次的数据，也就是不会进行更新。至于是 由于主键去重，还是有重复的元素就去我还会在看看
     * insertOrReplace：  会去重，保存最新的数据，也就是会进行更新
     * save：  不会去重，保存所有数据
     *
     * @param bean
     */
    public static void insertData(UserDBRememberBean bean) {
        AppApplication.getDaoSession().insert(bean);
    }

    /**
     * 增  --数据存在则替换，数据不存在则插入
     *
     * @param bean
     */
    public static void insertOrReplaceData(UserDBRememberBean bean) {
        AppApplication.getDaoSession().insertOrReplace(bean);
    }


    /**
     * 删--delete()和deleteAll()；分别表示删除单个和删除所有。
     */

    public static void deleteData(UserDBRememberBean bean) {
        AppApplication.getDaoSession().delete(bean);

    }

    public static void deleteAllData(Class clazz) {
        AppApplication.getDaoSession().deleteAll(clazz);

    }


    /**
     * 改--通过update来进行修改：
     */


    public static void updateData(UserDBRememberBean bean) {
        AppApplication.getDaoSession().insertOrReplace(bean);
    }


    /**
     * 查询
     * loadAll()：查询所有数据。
     * queryRaw()：根据条件查询
     * queryBuilder() : 方便查询的创建，后面详细讲解。
     */


    public static List queryAll(Class clazz) {
        List list = AppApplication.getDaoSession().loadAll(clazz);
        return list;
    }

    public static List queryRaw(Long id) {

        List<UserDBRememberBean> beanLis = (List<UserDBRememberBean>)//" where username = ?", name
//                 AppApplication.getDaoSession().queryRaw(UserDBRememberBean.class, " where id = ?", id );
                AppApplication.getDaoSession().queryRaw(UserDBRememberBean.class, " where id = ?", id.toString());
        return beanLis;
    }

    /**
     * 根据用户名条件查询,返回password
     *
     * @param name
     * @return
     */
    public static UserDBRememberBean queryListByMessageToGetPassword(String name) {
        boolean isExist = queryListIsExist(name);
        if (isExist) {
            List<UserDBRememberBean> UserDBRememberBeanList = queryListByMessage(name);
            Log.e("path=====:=====", UserDBRememberBeanList.size() + ""); //   /storage/emulated/0/1604026573438.mp4
            for (int i = 0; i < UserDBRememberBeanList.size(); i++) {

                return UserDBRememberBeanList.get(0);

            }
        } else {
            return new UserDBRememberBean();
        }
        return new UserDBRememberBean();

    }

    /**
     * 根据单个条件查询
     *
     * @param name
     * @return
     */
    public static List<UserDBRememberBean> queryListByMessage(String name) {
        DaoSession daoSession = AppApplication.getDaoSession();
        QueryBuilder<UserDBRememberBean> qb = daoSession.queryBuilder(UserDBRememberBean.class);
        List<UserDBRememberBean> students = daoSession.queryRaw(UserDBRememberBean.class, " where username = ?", name);
        return students;
    }
    //tag其实就是id，但是greendao查找id会报错，只能通过tag标识

    public static List<UserDBRememberBean> queryListByBeanIDTag(String tag) {
        DaoSession daoSession = AppApplication.getDaoSession();
        QueryBuilder<UserDBRememberBean> qb = daoSession.queryBuilder(UserDBRememberBean.class);
        List<UserDBRememberBean> students = daoSession.queryRaw(UserDBRememberBean.class, " where tag = ?", tag);
        return students;
    }

    public static UserDBRememberBean queryListByName(String name) {
        DaoSession daoSession = AppApplication.getDaoSession();
        QueryBuilder<UserDBRememberBean> qb = daoSession.queryBuilder(UserDBRememberBean.class);
        List<UserDBRememberBean> students = daoSession.queryRaw(UserDBRememberBean.class, " where username = ?", name);
        UserDBRememberBean userDBRememberBean = students.get(0);
        return userDBRememberBean;
    }

    //查询 超级管理员admin，用户是否存在，true=存在
    public static boolean queryAdminIsExist() {
        //查询所有数据
        try {
            List<UserDBRememberBean> list = AppApplication.getDaoSession().getUserDBRememberBeanDao().queryBuilder().list();

            if (null == list || list.isEmpty()) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                UserDBRememberBean bean = list.get(i);
                if (1 == bean.getId() && "admin".equals(bean.getUsername()) ) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;

        }
    }

    /**
     * @param name
     * @return 查询是否存在
     */
    public static boolean queryListIsExist(String name) {
        DaoSession daoSession = AppApplication.getDaoSession();
        QueryBuilder<UserDBRememberBean> qb = daoSession.queryBuilder(UserDBRememberBean.class);
        List<UserDBRememberBean> students = daoSession.queryRaw(UserDBRememberBean.class, " where username = ?", name);
//        List<Student> students = daoSession.loadAll(Student.class);
//        return students;
        Log.e("path=====Start:=====", students.size() + ""); //   /storage/emulated/0/1604026573438.mp4

        if (students.size() != 0) {  //存在
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param id
     * @return 查询ID是否存在
     */
    public static boolean queryListIsIDExist(String id) {
        DaoSession daoSession = AppApplication.getDaoSession();
        QueryBuilder<UserDBRememberBean> qb = daoSession.queryBuilder(UserDBRememberBean.class);
        List<UserDBRememberBean> students = daoSession.queryRaw(UserDBRememberBean.class, " where id = ?", id);
//        List<Student> students = daoSession.loadAll(Student.class);
//        return students;
        Log.e("path=====Start:=====", students.size() + ""); //   /storage/emulated/0/1604026573438.mp4

        if (students.size() != 0) {  //存在
            return true;
        } else {
            return false;
        }
    }
}
