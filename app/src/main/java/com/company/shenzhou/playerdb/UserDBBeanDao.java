package com.company.shenzhou.playerdb;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.company.shenzhou.bean.dbbean.UserDBBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER_DBBEAN".
*/
public class UserDBBeanDao extends AbstractDao<UserDBBean, Long> {

    public static final String TABLENAME = "USER_DBBEAN";

    /**
     * Properties of entity UserDBBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Username = new Property(1, String.class, "username", false, "USERNAME");
        public final static Property Password = new Property(2, String.class, "password", false, "PASSWORD");
        public final static Property Tag = new Property(3, String.class, "tag", false, "TAG");
        public final static Property RememberPassword = new Property(4, Boolean.class, "rememberPassword", false, "REMEMBER_PASSWORD");
        public final static Property RememberPrivacy = new Property(5, Boolean.class, "rememberPrivacy", false, "REMEMBER_PRIVACY");
        public final static Property UserRole = new Property(6, int.class, "userRole", false, "USER_ROLE");
    }


    public UserDBBeanDao(DaoConfig config) {
        super(config);
    }
    
    public UserDBBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_DBBEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"USERNAME\" TEXT UNIQUE ," + // 1: username
                "\"PASSWORD\" TEXT," + // 2: password
                "\"TAG\" TEXT," + // 3: tag
                "\"REMEMBER_PASSWORD\" INTEGER," + // 4: rememberPassword
                "\"REMEMBER_PRIVACY\" INTEGER," + // 5: rememberPrivacy
                "\"USER_ROLE\" INTEGER NOT NULL );"); // 6: userRole
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER_DBBEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UserDBBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String username = entity.getUsername();
        if (username != null) {
            stmt.bindString(2, username);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(3, password);
        }
 
        String tag = entity.getTag();
        if (tag != null) {
            stmt.bindString(4, tag);
        }
 
        Boolean rememberPassword = entity.getRememberPassword();
        if (rememberPassword != null) {
            stmt.bindLong(5, rememberPassword ? 1L: 0L);
        }
 
        Boolean rememberPrivacy = entity.getRememberPrivacy();
        if (rememberPrivacy != null) {
            stmt.bindLong(6, rememberPrivacy ? 1L: 0L);
        }
        stmt.bindLong(7, entity.getUserRole());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UserDBBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String username = entity.getUsername();
        if (username != null) {
            stmt.bindString(2, username);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(3, password);
        }
 
        String tag = entity.getTag();
        if (tag != null) {
            stmt.bindString(4, tag);
        }
 
        Boolean rememberPassword = entity.getRememberPassword();
        if (rememberPassword != null) {
            stmt.bindLong(5, rememberPassword ? 1L: 0L);
        }
 
        Boolean rememberPrivacy = entity.getRememberPrivacy();
        if (rememberPrivacy != null) {
            stmt.bindLong(6, rememberPrivacy ? 1L: 0L);
        }
        stmt.bindLong(7, entity.getUserRole());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public UserDBBean readEntity(Cursor cursor, int offset) {
        UserDBBean entity = new UserDBBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // username
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // password
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // tag
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0, // rememberPassword
            cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0, // rememberPrivacy
            cursor.getInt(offset + 6) // userRole
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UserDBBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUsername(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPassword(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTag(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRememberPassword(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
        entity.setRememberPrivacy(cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0);
        entity.setUserRole(cursor.getInt(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UserDBBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UserDBBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UserDBBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
