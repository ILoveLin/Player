package com.company.shenzhou.bean.dbbean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/27 8:55
 * desc：   //存入数据库的的时候,这个字段为空,之后登入成功,需要绑定当前登入的用户名,这样在切换用户搜一搜的时候根据用户,来显示是否已经添加过
 *     //每个用户搜一搜之后只能显示当前知己已添加过的设备
 *     //list记录每个下载者的名字,绑定被下载者的名字
 *
 *
 *
 *      * contains判断的时候,必须是本身创建一个
 *  * DownloadedNameListBean  tagBean = new DownloadedNameListBean();
 *  *         tagBean.setDownloadedByName(mLoginUserName);
 *  *
 *  *         然后在downloadedNameList.contains(tagBean)来判断,直接传入string值是没有用的
 */
public class DownBindNameListBean {


    private String downBindName;

    @Override
    public String toString() {
        return "DownloadedNameListBean{" +
                "downloadedByName='" + downBindName + '\'' +
                '}';
    }

    /**
     * 依据对象属性值是否相同来判断ArrayList是否包含某一对象，则需要重写Object的equals()和hashCode()，并在equals()中一一比较对象的每个属性值。
     *
     * 为什么要重写？因为contains方法里面用的是equals方法，equals方法里面是==，判断的是两个对象的内存地址是否相等。
     * ————————————————
     * 版权声明：本文为CSDN博主「Darren Gong」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/axin1240101543/article/details/113619331
     * @param obj
     * @return
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        //如果省市区都相等则相等
        if (obj instanceof DownBindNameListBean) {
            DownBindNameListBean district = (DownBindNameListBean) obj;
            return this.downBindName.equals(district.downBindName);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getDownBindName() {
        return downBindName;
    }

    public void setDownBindName(String downBindName) {
        this.downBindName = downBindName;
    }
}
