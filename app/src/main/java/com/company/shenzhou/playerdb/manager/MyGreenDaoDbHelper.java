package com.company.shenzhou.playerdb.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.company.shenzhou.playerdb.DaoMaster;
import com.company.shenzhou.playerdb.DeviceDBBeanDao;
import com.company.shenzhou.playerdb.UserDBRememberBeanDao;
import com.company.shenzhou.utlis.LogUtils;

import org.greenrobot.greendao.database.Database;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2023/11/17 9:11
 * desc：
 * ①新增的字段或修改的字段，其变量类型应使用基础数据类型的包装类，如使用Integer而不是int，避免升级过程中报错。
 * ①根据MigrationHelper中的代码，升级后，新增的字段和修改的字段，都会默认被赋予null值。
 *
 * 作者：Anwfly
 * 链接：https://www.jianshu.com/p/4151187947f4
 * 来源：简书
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
public class MyGreenDaoDbHelper extends DaoMaster.DevOpenHelper {
    private static final String TAG = "GreenDao升级工具,==";

    public MyGreenDaoDbHelper(Context context, String name) {
        super(context, name);
    }

    public MyGreenDaoDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }


    @Override
    @SuppressWarnings("all")
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        LogUtils.e(TAG + "----" + oldVersion + "---先前和更新之后的版本---" + newVersion + "----");
        if (oldVersion < newVersion) {
            LogUtils.e(TAG + "进行数据库升级");
            new GreenDaoCompatibleUpdateHelper()
                    .setCallBack(
                            new GreenDaoCompatibleUpdateHelper.GreenDaoCompatibleUpdateCallBack() {

                                @Override
                                public void onFinalSuccess() {
                                    LogUtils.e(TAG + "进行数据库升级 ===> 成功");
                                }

                                @Override
                                public void onFailedLog(String errorMsg) {
                                    LogUtils.e(TAG + "升级失败日志 ===> " + errorMsg);
                                }
                            }
                    )
                    .compatibleUpdate(
                            db,
                            DeviceDBBeanDao.class, UserDBRememberBeanDao.class);
            LogUtils.e(TAG + "进行数据库升级--完成");
        }
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        // 不要调用父类的，它默认是先删除全部表再创建
        // super.onUpgrade(db, oldVersion, newVersion);
    }
}
