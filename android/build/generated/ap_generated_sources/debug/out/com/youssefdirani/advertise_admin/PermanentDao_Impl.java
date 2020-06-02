package com.youssefdirani.advertise_admin;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class PermanentDao_Impl implements PermanentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NavHeaderEntity> __insertionAdapterOfNavHeaderEntity;

  private final EntityInsertionAdapter<NavEntity> __insertionAdapterOfNavEntity;

  private final EntityInsertionAdapter<DatabaseInfo> __insertionAdapterOfDatabaseInfo;

  private final EntityDeletionOrUpdateAdapter<NavEntity> __deletionAdapterOfNavEntity;

  private final EntityDeletionOrUpdateAdapter<NavHeaderEntity> __updateAdapterOfNavHeaderEntity;

  private final EntityDeletionOrUpdateAdapter<NavEntity> __updateAdapterOfNavEntity;

  private final EntityDeletionOrUpdateAdapter<DatabaseInfo> __updateAdapterOfDatabaseInfo;

  public PermanentDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNavHeaderEntity = new EntityInsertionAdapter<NavHeaderEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `navheader` (`uid`,`imagepath`,`title`,`subtitle`,`background_color_tag`) VALUES (?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, NavHeaderEntity value) {
        stmt.bindLong(1, value.uid);
        if (value.imagePath == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.imagePath);
        }
        if (value.title == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.title);
        }
        if (value.subtitle == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.subtitle);
        }
        if (value.backgroundColor == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.backgroundColor);
        }
      }
    };
    this.__insertionAdapterOfNavEntity = new EntityInsertionAdapter<NavEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `nav` (`uid`,`index1`,`title`,`icon_tag`,`statusbar_backgroundcolortag`,`statusbar_dark`,`topbar_backgroundcolortag`,`topbar_hamburgercolortag`,`topbar_titlecolortag`,`topbar_3dotscolortag`,`bottombar_backgroundcolortag`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, NavEntity value) {
        stmt.bindLong(1, value.uid);
        stmt.bindLong(2, value.index);
        if (value.title == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.title);
        }
        if (value.iconTag == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.iconTag);
        }
        if (value.statusBar_backgroundColorTag == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.statusBar_backgroundColorTag);
        }
        final int _tmp;
        _tmp = value.statusBar_dark ? 1 : 0;
        stmt.bindLong(6, _tmp);
        if (value.topBar_backgroundColorTag == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.topBar_backgroundColorTag);
        }
        if (value.topBar_hamburgerColorTag == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.topBar_hamburgerColorTag);
        }
        if (value.topBar_titleColorTag == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.topBar_titleColorTag);
        }
        if (value.topBar_3dotsColorTag == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.topBar_3dotsColorTag);
        }
        if (value.bottombar_backgroundColorTag == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.bottombar_backgroundColorTag);
        }
      }
    };
    this.__insertionAdapterOfDatabaseInfo = new EntityInsertionAdapter<DatabaseInfo>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `dbinfo` (`uid`,`last_update`,`owner_name`,`owner_phonenumber`) VALUES (?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DatabaseInfo value) {
        stmt.bindLong(1, value.uid);
        stmt.bindLong(2, value.lastUpdate);
        if (value.ownerName == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.ownerName);
        }
        if (value.ownerPhoneNumber == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.ownerPhoneNumber);
        }
      }
    };
    this.__deletionAdapterOfNavEntity = new EntityDeletionOrUpdateAdapter<NavEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `nav` WHERE `uid` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, NavEntity value) {
        stmt.bindLong(1, value.uid);
      }
    };
    this.__updateAdapterOfNavHeaderEntity = new EntityDeletionOrUpdateAdapter<NavHeaderEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `navheader` SET `uid` = ?,`imagepath` = ?,`title` = ?,`subtitle` = ?,`background_color_tag` = ? WHERE `uid` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, NavHeaderEntity value) {
        stmt.bindLong(1, value.uid);
        if (value.imagePath == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.imagePath);
        }
        if (value.title == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.title);
        }
        if (value.subtitle == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.subtitle);
        }
        if (value.backgroundColor == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.backgroundColor);
        }
        stmt.bindLong(6, value.uid);
      }
    };
    this.__updateAdapterOfNavEntity = new EntityDeletionOrUpdateAdapter<NavEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `nav` SET `uid` = ?,`index1` = ?,`title` = ?,`icon_tag` = ?,`statusbar_backgroundcolortag` = ?,`statusbar_dark` = ?,`topbar_backgroundcolortag` = ?,`topbar_hamburgercolortag` = ?,`topbar_titlecolortag` = ?,`topbar_3dotscolortag` = ?,`bottombar_backgroundcolortag` = ? WHERE `uid` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, NavEntity value) {
        stmt.bindLong(1, value.uid);
        stmt.bindLong(2, value.index);
        if (value.title == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.title);
        }
        if (value.iconTag == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.iconTag);
        }
        if (value.statusBar_backgroundColorTag == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.statusBar_backgroundColorTag);
        }
        final int _tmp;
        _tmp = value.statusBar_dark ? 1 : 0;
        stmt.bindLong(6, _tmp);
        if (value.topBar_backgroundColorTag == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.topBar_backgroundColorTag);
        }
        if (value.topBar_hamburgerColorTag == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.topBar_hamburgerColorTag);
        }
        if (value.topBar_titleColorTag == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.topBar_titleColorTag);
        }
        if (value.topBar_3dotsColorTag == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.topBar_3dotsColorTag);
        }
        if (value.bottombar_backgroundColorTag == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.bottombar_backgroundColorTag);
        }
        stmt.bindLong(12, value.uid);
      }
    };
    this.__updateAdapterOfDatabaseInfo = new EntityDeletionOrUpdateAdapter<DatabaseInfo>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `dbinfo` SET `uid` = ?,`last_update` = ?,`owner_name` = ?,`owner_phonenumber` = ? WHERE `uid` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DatabaseInfo value) {
        stmt.bindLong(1, value.uid);
        stmt.bindLong(2, value.lastUpdate);
        if (value.ownerName == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.ownerName);
        }
        if (value.ownerPhoneNumber == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.ownerPhoneNumber);
        }
        stmt.bindLong(5, value.uid);
      }
    };
  }

  @Override
  public void insertNavHeader(final NavHeaderEntity navHeaderEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfNavHeaderEntity.insert(navHeaderEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertNav(final NavEntity navEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfNavEntity.insert(navEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertDbInfoRecord(final DatabaseInfo databaseInfo) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfDatabaseInfo.insert(databaseInfo);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteNavEntity(final NavEntity navEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfNavEntity.handle(navEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateNavHeader(final NavHeaderEntity navHeaderEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfNavHeaderEntity.handle(navHeaderEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateNav(final NavEntity navEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfNavEntity.handle(navEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateDatabaseInfoRecord(final DatabaseInfo databaseInfo) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfDatabaseInfo.handle(databaseInfo);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public NavHeaderEntity getNavHeader() {
    final String _sql = "SELECT * FROM navheader LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
      final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagepath");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfSubtitle = CursorUtil.getColumnIndexOrThrow(_cursor, "subtitle");
      final int _cursorIndexOfBackgroundColor = CursorUtil.getColumnIndexOrThrow(_cursor, "background_color_tag");
      final NavHeaderEntity _result;
      if(_cursor.moveToFirst()) {
        _result = new NavHeaderEntity();
        _result.uid = _cursor.getInt(_cursorIndexOfUid);
        _result.imagePath = _cursor.getString(_cursorIndexOfImagePath);
        _result.title = _cursor.getString(_cursorIndexOfTitle);
        _result.subtitle = _cursor.getString(_cursorIndexOfSubtitle);
        _result.backgroundColor = _cursor.getString(_cursorIndexOfBackgroundColor);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public NavEntity getNav(final int index) {
    final String _sql = "SELECT * FROM nav WHERE index1 LIKE ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, index);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
      final int _cursorIndexOfIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "index1");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfIconTag = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_tag");
      final int _cursorIndexOfStatusBarBackgroundColorTag = CursorUtil.getColumnIndexOrThrow(_cursor, "statusbar_backgroundcolortag");
      final int _cursorIndexOfStatusBarDark = CursorUtil.getColumnIndexOrThrow(_cursor, "statusbar_dark");
      final int _cursorIndexOfTopBarBackgroundColorTag = CursorUtil.getColumnIndexOrThrow(_cursor, "topbar_backgroundcolortag");
      final int _cursorIndexOfTopBarHamburgerColorTag = CursorUtil.getColumnIndexOrThrow(_cursor, "topbar_hamburgercolortag");
      final int _cursorIndexOfTopBarTitleColorTag = CursorUtil.getColumnIndexOrThrow(_cursor, "topbar_titlecolortag");
      final int _cursorIndexOfTopBar3dotsColorTag = CursorUtil.getColumnIndexOrThrow(_cursor, "topbar_3dotscolortag");
      final int _cursorIndexOfBottombarBackgroundColorTag = CursorUtil.getColumnIndexOrThrow(_cursor, "bottombar_backgroundcolortag");
      final NavEntity _result;
      if(_cursor.moveToFirst()) {
        _result = new NavEntity();
        _result.uid = _cursor.getInt(_cursorIndexOfUid);
        _result.index = _cursor.getInt(_cursorIndexOfIndex);
        _result.title = _cursor.getString(_cursorIndexOfTitle);
        _result.iconTag = _cursor.getString(_cursorIndexOfIconTag);
        _result.statusBar_backgroundColorTag = _cursor.getString(_cursorIndexOfStatusBarBackgroundColorTag);
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfStatusBarDark);
        _result.statusBar_dark = _tmp != 0;
        _result.topBar_backgroundColorTag = _cursor.getString(_cursorIndexOfTopBarBackgroundColorTag);
        _result.topBar_hamburgerColorTag = _cursor.getString(_cursorIndexOfTopBarHamburgerColorTag);
        _result.topBar_titleColorTag = _cursor.getString(_cursorIndexOfTopBarTitleColorTag);
        _result.topBar_3dotsColorTag = _cursor.getString(_cursorIndexOfTopBar3dotsColorTag);
        _result.bottombar_backgroundColorTag = _cursor.getString(_cursorIndexOfBottombarBackgroundColorTag);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<NavEntity> getAllNav() {
    final String _sql = "SELECT * FROM nav";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
      final int _cursorIndexOfIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "index1");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfIconTag = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_tag");
      final int _cursorIndexOfStatusBarBackgroundColorTag = CursorUtil.getColumnIndexOrThrow(_cursor, "statusbar_backgroundcolortag");
      final int _cursorIndexOfStatusBarDark = CursorUtil.getColumnIndexOrThrow(_cursor, "statusbar_dark");
      final int _cursorIndexOfTopBarBackgroundColorTag = CursorUtil.getColumnIndexOrThrow(_cursor, "topbar_backgroundcolortag");
      final int _cursorIndexOfTopBarHamburgerColorTag = CursorUtil.getColumnIndexOrThrow(_cursor, "topbar_hamburgercolortag");
      final int _cursorIndexOfTopBarTitleColorTag = CursorUtil.getColumnIndexOrThrow(_cursor, "topbar_titlecolortag");
      final int _cursorIndexOfTopBar3dotsColorTag = CursorUtil.getColumnIndexOrThrow(_cursor, "topbar_3dotscolortag");
      final int _cursorIndexOfBottombarBackgroundColorTag = CursorUtil.getColumnIndexOrThrow(_cursor, "bottombar_backgroundcolortag");
      final List<NavEntity> _result = new ArrayList<NavEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final NavEntity _item;
        _item = new NavEntity();
        _item.uid = _cursor.getInt(_cursorIndexOfUid);
        _item.index = _cursor.getInt(_cursorIndexOfIndex);
        _item.title = _cursor.getString(_cursorIndexOfTitle);
        _item.iconTag = _cursor.getString(_cursorIndexOfIconTag);
        _item.statusBar_backgroundColorTag = _cursor.getString(_cursorIndexOfStatusBarBackgroundColorTag);
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfStatusBarDark);
        _item.statusBar_dark = _tmp != 0;
        _item.topBar_backgroundColorTag = _cursor.getString(_cursorIndexOfTopBarBackgroundColorTag);
        _item.topBar_hamburgerColorTag = _cursor.getString(_cursorIndexOfTopBarHamburgerColorTag);
        _item.topBar_titleColorTag = _cursor.getString(_cursorIndexOfTopBarTitleColorTag);
        _item.topBar_3dotsColorTag = _cursor.getString(_cursorIndexOfTopBar3dotsColorTag);
        _item.bottombar_backgroundColorTag = _cursor.getString(_cursorIndexOfBottombarBackgroundColorTag);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public DatabaseInfo getDatabaseInfo() {
    final String _sql = "SELECT * FROM dbinfo LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
      final int _cursorIndexOfLastUpdate = CursorUtil.getColumnIndexOrThrow(_cursor, "last_update");
      final int _cursorIndexOfOwnerName = CursorUtil.getColumnIndexOrThrow(_cursor, "owner_name");
      final int _cursorIndexOfOwnerPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "owner_phonenumber");
      final DatabaseInfo _result;
      if(_cursor.moveToFirst()) {
        _result = new DatabaseInfo();
        _result.uid = _cursor.getInt(_cursorIndexOfUid);
        _result.lastUpdate = _cursor.getLong(_cursorIndexOfLastUpdate);
        _result.ownerName = _cursor.getString(_cursorIndexOfOwnerName);
        _result.ownerPhoneNumber = _cursor.getString(_cursorIndexOfOwnerPhoneNumber);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
