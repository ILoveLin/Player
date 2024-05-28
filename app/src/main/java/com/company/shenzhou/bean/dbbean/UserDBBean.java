package com.company.shenzhou.bean.dbbean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/5/28 8:45
 * desc：
 */
@Entity
public class UserDBBean {
    @Id(autoincrement = true)
    public  Long id;

    @Unique
    public String username;
    public  String password;
    public  String tag;
    public  Boolean rememberPassword;//是否记住勾选了记住密码   true=勾选
    public Boolean rememberPrivacy; //是否记住勾选了用户协议和隐私政策 true=勾选
    public int userRole = 0;      //用户权限：0普通用户、1权限用户、2超级管理员  默认为0-普通用户
    @Keep
    public UserDBBean(Long id, String username, String password, String tag,
            String remember, int userType) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.tag = tag;
    }
    @Keep
    public UserDBBean() {
    }
    @Generated(hash = 1689614671)
    public UserDBBean(Long id, String username, String password, String tag,
            Boolean rememberPassword, Boolean rememberPrivacy, int userRole) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.tag = tag;
        this.rememberPassword = rememberPassword;
        this.rememberPrivacy = rememberPrivacy;
        this.userRole = userRole;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getTag() {
        return this.tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    public Boolean getRememberPassword() {
        return this.rememberPassword;
    }
    public void setRememberPassword(Boolean rememberPassword) {
        this.rememberPassword = rememberPassword;
    }
    public Boolean getRememberPrivacy() {
        return this.rememberPrivacy;
    }
    public void setRememberPrivacy(Boolean rememberPrivacy) {
        this.rememberPrivacy = rememberPrivacy;
    }
    public int getUserRole() {
        return this.userRole;
    }
    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "UserDBBean{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", tag='" + tag + '\'' +
                ", rememberPassword=" + rememberPassword +
                ", rememberPrivacy=" + rememberPrivacy +
                ", userRole=" + userRole +
                '}';
    }
}
