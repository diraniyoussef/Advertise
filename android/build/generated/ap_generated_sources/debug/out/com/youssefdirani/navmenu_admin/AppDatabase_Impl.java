package com.youssefdirani.navmenu_admin;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PermanentDao _permanentDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `navheader` (`uid` INTEGER NOT NULL, `imagepath` TEXT, `title` TEXT, `subtitle` TEXT, `background_color_tag` TEXT, PRIMARY KEY(`uid`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `nav` (`uid` INTEGER NOT NULL, `index` INTEGER NOT NULL, `title` TEXT, `icon_tag` TEXT, `statusbar_backgroundcolortag` TEXT, `statusbar_dark` INTEGER NOT NULL, `topbar_backgroundcolortag` TEXT, `topbar_hamburgercolortag` TEXT, `topbar_titlecolortag` TEXT, `topbar_3dotscolortag` TEXT, `bottombar_exists` INTEGER NOT NULL, `bottombar_backgroundcolortag` TEXT, PRIMARY KEY(`uid`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '82e99c786e7d314604ca18e8c952ebec')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `navheader`");
        _db.execSQL("DROP TABLE IF EXISTS `nav`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsNavheader = new HashMap<String, TableInfo.Column>(5);
        _columnsNavheader.put("uid", new TableInfo.Column("uid", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNavheader.put("imagepath", new TableInfo.Column("imagepath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNavheader.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNavheader.put("subtitle", new TableInfo.Column("subtitle", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNavheader.put("background_color_tag", new TableInfo.Column("background_color_tag", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNavheader = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNavheader = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNavheader = new TableInfo("navheader", _columnsNavheader, _foreignKeysNavheader, _indicesNavheader);
        final TableInfo _existingNavheader = TableInfo.read(_db, "navheader");
        if (! _infoNavheader.equals(_existingNavheader)) {
          return new RoomOpenHelper.ValidationResult(false, "navheader(com.youssefdirani.navmenu_admin.NavHeaderEntity).\n"
                  + " Expected:\n" + _infoNavheader + "\n"
                  + " Found:\n" + _existingNavheader);
        }
        final HashMap<String, TableInfo.Column> _columnsNav = new HashMap<String, TableInfo.Column>(12);
        _columnsNav.put("uid", new TableInfo.Column("uid", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNav.put("index", new TableInfo.Column("index", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNav.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNav.put("icon_tag", new TableInfo.Column("icon_tag", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNav.put("statusbar_backgroundcolortag", new TableInfo.Column("statusbar_backgroundcolortag", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNav.put("statusbar_dark", new TableInfo.Column("statusbar_dark", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNav.put("topbar_backgroundcolortag", new TableInfo.Column("topbar_backgroundcolortag", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNav.put("topbar_hamburgercolortag", new TableInfo.Column("topbar_hamburgercolortag", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNav.put("topbar_titlecolortag", new TableInfo.Column("topbar_titlecolortag", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNav.put("topbar_3dotscolortag", new TableInfo.Column("topbar_3dotscolortag", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNav.put("bottombar_exists", new TableInfo.Column("bottombar_exists", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNav.put("bottombar_backgroundcolortag", new TableInfo.Column("bottombar_backgroundcolortag", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNav = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNav = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNav = new TableInfo("nav", _columnsNav, _foreignKeysNav, _indicesNav);
        final TableInfo _existingNav = TableInfo.read(_db, "nav");
        if (! _infoNav.equals(_existingNav)) {
          return new RoomOpenHelper.ValidationResult(false, "nav(com.youssefdirani.navmenu_admin.NavEntity).\n"
                  + " Expected:\n" + _infoNav + "\n"
                  + " Found:\n" + _existingNav);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "82e99c786e7d314604ca18e8c952ebec", "793afc492932fcb733b5dfee2d5af659");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "navheader","nav");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `navheader`");
      _db.execSQL("DELETE FROM `nav`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public PermanentDao permanentDao() {
    if (_permanentDao != null) {
      return _permanentDao;
    } else {
      synchronized(this) {
        if(_permanentDao == null) {
          _permanentDao = new PermanentDao_Impl(this);
        }
        return _permanentDao;
      }
    }
  }
}
