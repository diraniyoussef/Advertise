package com.youssefdirani.advertise_admin;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.List;

import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

class DbOperations {
    private MainActivity activity;

    DbOperations(MainActivity activity ) {
        this.activity = activity;
        db_room = Room.databaseBuilder( activity, AppDatabase.class, "my_db" ).build();
        permanentDao = db_room.permanentDao();
    }

    void addNavRecord( final String title ) {
        new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                //Log.i("Youssef", "inside DbOperations : adding a nav entity.");
                NavEntity navEntity = new NavEntity();
                navEntity.title = title;
                navEntity.bottombar_backgroundColorTag = "colorWhite"; //background color to white. This is my default
                List<NavEntity> navEntityList = permanentDao.getAllNav();
                navEntity.index = navEntityList.size(); //it's my convention to preserve throughout the app the order of indexes
                permanentDao.insertNav( navEntity );
                setBottomBarTableAndFirstBottomNavContentTable( activity.navOperations.getCheckedItemOrder() );


            }
        }.start();
    }
    void removeNavRecord( final int index ) {
        new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                //Log.i("Youssef", "inside DbOperations : adding a nav entity.");
                List<NavEntity> navEntityList = permanentDao.getAllNav();
                NavEntity navEntity = new NavEntity();
                for( NavEntity navEntity1 : navEntityList ) {
                    if( navEntity1.index == index ) {
                        navEntity = navEntity1;
                        break;
                    }
                }
                concatenateNavTableIndices( index ); //fixing the index of the records in nav table
                Log.i("Youssef", "before deleting from database");
                permanentDao.deleteNavEntity( navEntity );
                Log.i("Youssef", "after deleting from database");
                deleteBbTable();
                deleteBottomNavContentTablesButKeepUpTo(-1);
            }
            void concatenateNavTableIndices( int startingIndex ) {//should be called whenever an entity is deleted (except for the last entity).
                //finally it's ok to have e.g. 4 elements like the following indices 0 3 1 2
                List<NavEntity> navEntityList = permanentDao.getAllNav();
                if( startingIndex < navEntityList.size() ) { //startingIndex is passed such that it corresponds for the just deleted element
                    Log.i("Youssef", "concatenateNavTableIndices. size of navEntityList = " + navEntityList.size());
                    for( int i = startingIndex ; i < navEntityList.size() - 1 ; i++ ) {
                        Log.i("Youssef", "concatenateNavTableIndices. i = " + i);
                        NavEntity navEntity = permanentDao.getNav(i + 1 );
                        navEntity.index = i;
                        permanentDao.updateNav( navEntity );
                    }
                }
            }
        }.start();
    }

    void onStop() {
        db_room.close();
    }

    private AppDatabase db_room;
    private PermanentDao permanentDao;
    private SupportSQLiteDatabase db; //https://developer.android.com/reference/androidx/sqlite/db/SupportSQLiteDatabase

    private String generateBbTableName( int navIndex ) {
        return "bb_" + navIndex;
    }

    void setBottomBarTable() {
        db.beginTransaction(); //if you're thinking in using transaction : https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
        final String bottomBarTableName = generateBbTableName( activity.navOperations.getCheckedItemOrder() );
        try {
            createBbTable( bottomBarTableName );
            db.setTransactionSuccessful(); //to commit
        } catch(Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.toast("Unable to save data internally. Data integrity is not guaranteed.", Toast.LENGTH_LONG);
                }
            });
        } finally {
            db.endTransaction();
        }
    }

    private String generateBottomNavContentTableName( int navIndex, int bottomNavIndex ) {
        return "table" + navIndex + "_" + bottomNavIndex;
    }

    private void setBottomBarTableAndFirstBottomNavContentTable( final int navIndex ) {
        db.beginTransaction(); //if you're thinking in using transaction : https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
        final String bottomBarTableName = generateBbTableName( navIndex );
        final String firstBottomNavContentTableName = generateBottomNavContentTableName( navIndex, 0 );
        try {
            createBbTable( bottomBarTableName );
            //normally we have to add 4 rows (if bb_0 table is made for the first time.) But I won't. I will keep everything to default as the first time the user (not the admin) opens the app, it will be similar to the admin's app
            db.execSQL("CREATE TABLE IF NOT EXISTS " + firstBottomNavContentTableName + " ( " +
                    "uid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "type TEXT, " +
                    "content TEXT);");
            Log.i("fatal","after firstBottomNavContentTableName");
            //I can't insert anything here because it's up to the user to insert either images or texts or whatever.
            db.setTransactionSuccessful(); //to commit
        } catch(Exception e) {
            Log.e("fatal", "I got an error", e);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.toast("Unable to save data internally. Data integrity is not guaranteed.", Toast.LENGTH_LONG);
                }
            });
        } finally {
            db.endTransaction();
        }
    }

    private void createBbTable( String bottomBarTableName ) {
        //here we must create a table called bb_0 (which is actually bottombar of the first nav) if not yet created, and it will contain (now we will fetch) all the existing bottom bar tabs info
        db.execSQL("CREATE TABLE IF NOT EXISTS '" + bottomBarTableName + "' ( " +
                "uid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "index1 INTEGER, " +//we cannot name "index" or 'index' they're reserved for some reason.
                "icon TEXT);");
        //adding just one item (if the table has been just created - this check is needed), which corresponds to the navIndex.
        Cursor cursor_bb = db.query("SELECT * FROM '" + bottomBarTableName + "'");
        if( cursor_bb.getCount() == 0 ) {
            ContentValues contentValues = new ContentValues();
            //contentValues.put( "uid", 0);
            contentValues.put( "title", "Option 1");
            contentValues.put( "index1", 0 );
            contentValues.put( "icon", "ic_no_icon" );
            //I guess "icon" will be null, and it's fine to be null.
            db.insert( bottomBarTableName, SQLiteDatabase.CONFLICT_NONE, contentValues ); //returns -1 if failure. https://developer.android.com/reference/androidx/sqlite/db/SupportSQLiteDatabase
        }
    }

    void onCreate() {
        new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                //This is for the first time the admin uses the app
                insertNavHeader();
                insertNavEntity();
                SupportSQLiteOpenHelper supportSQLiteOpenHelper = db_room.getOpenHelper(); //referring to the opened connection to the database. //very good explanation : https://stackoverflow.com/questions/17348480/how-do-i-prevent-sqlite-database-locks - related https://stackoverflow.com/questions/8104832/sqlite-simultaneous-reading-and-writing. And this is related as well https://www.sqlite.org/lockingv3.html
                db = supportSQLiteOpenHelper.getWritableDatabase(); //enableWriteAheadLogging() When write-ahead logging is not enabled (the default), it is not possible for reads and writes to occur on the database at the same time. https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase#create(android.database.sqlite.SQLiteDatabase.CursorFactory)
                //It's better to make a mechanism that corrects itself in case of an error. I.e. make database tables coherent. But I will leave that for now. In case of an error, I believe the app won't crash and it will show something anyway, and for now it's up to the admin to correct whatever he wishes.
                setBottomBarTableAndFirstBottomNavContentTable(0);
                //https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase#execSQL(java.lang.String,%20java.lang.Object[])
                loadNavEntities();
                loadBb(0, true );
            }

            private void insertNavEntity() {
                List<NavEntity> navEntityList = permanentDao.getAllNav();
                if ( navEntityList.size() == 0 ) { //I believe it won't be null the first time we enter. It's just an empty list.
                    //Log.i("Youssef", "inside MainActivity : onStart. No nav entity exists.");
                    //create a record. We must have 1 record. We fetch its name from nav menu
                    NavEntity navEntity = new NavEntity();
                    navEntity.title = activity.navMenu.getItem(0).getTitle().toString();
                    navEntity.index = 0;
                    activity.setBottomBarBackgroundColor( "colorWhite" );
                    navEntity.bottombar_backgroundColorTag = "colorWhite";
                    Log.i("Youssef", "before inserting the first navEntity");
                    permanentDao.insertNav( navEntity );
                    Log.i("Youssef", "after inserting the first navEntity");
                } else {
                    Log.i("Youssef", "We already have an entity ??");
                    //I can't assign the UI here, not until all is inflated and so on.
                    //Now to get whether the user has a bottombar or not (for each navEntity)
                }
            }

            private void insertNavHeader() {
                NavHeaderEntity navHeaderEntity = permanentDao.getNavHeader();
                if( navHeaderEntity == null ) { //it was actually null the first time we entered.
                    //Log.i("Youssef", "inside MainActivity : onStart. No nav header entity exists.");
                    //create a record. We only need 1.
                    navHeaderEntity = new NavHeaderEntity();
                    permanentDao.insertNavHeader( navHeaderEntity ); //it worked even without specifying anything in the just-created navHeaderEntity
                } else {
                    //Log.i("Youssef", "inside MainActivity : onStart. A nav header entity already exists.");
                    //I can't assign the UI here, not until all is inflated and so on.
                }
            }

        }.start(); //I'm not sequencing threads because I'm assuming that this thread is almost intantaneous

    }

    void saveNavHeaderBackgroundColor( final String tag ) {
        ( new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                NavHeaderEntity navHeaderEntity = permanentDao.getNavHeader();
                //Log.i("Youssef", "Saving nav host background onto the database");
                navHeaderEntity.backgroundColor = tag;
                permanentDao.updateNavHeader(navHeaderEntity);
            }
        } ).start();

    }

    void loadNavHeaderBackgroundColor() {
        ( new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                NavHeaderEntity navHeaderEntity = permanentDao.getNavHeader();
                //Log.i("Youssef", "Loading nav host background color from the database");
                final String backgroundColorTag = navHeaderEntity.backgroundColor;
                if( backgroundColorTag != null && !backgroundColorTag.equals("") ) {
                    final LinearLayout linearLayout = activity.findViewById(R.id.linearlayout_navheader);
                    final int color_id = activity.getResources().getIdentifier( backgroundColorTag,
                            "color", activity.getPackageName() );
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearLayout.setBackgroundColor( activity.getResources().getColor( color_id ) );
                        }
                    });
                }
            }
        } ).start();
    }

    void saveHeaderTitles( final EditText editText, final EditText editText_navHeaderTitle,
                                  final EditText editText_navHeaderSubtitle) {
        ( new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                NavHeaderEntity navHeaderEntity = permanentDao.getNavHeader();
                if( editText.equals( editText_navHeaderTitle )) {
                    ////Log.i("Youssef", "edittext header title has lost focus");
                    navHeaderEntity.title = editText.getText().toString();
                    permanentDao.updateNavHeader( navHeaderEntity );
                }
                if( editText.equals( editText_navHeaderSubtitle )) {
                    ////Log.i("Youssef", "edittext header subtitle has lost focus");
                    navHeaderEntity.subtitle = editText.getText().toString();
                    permanentDao.updateNavHeader( navHeaderEntity );
                }
            }
        }).start();
    }

    void loadHeaderTitles( final EditText editText_navHeaderTitle, final EditText editText_navHeaderSubtitle ) {
        ( new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                final String navHeaderTitle = permanentDao.getNavHeader().title; //interesting how the compiler does not complain for an NPE
                if( navHeaderTitle != null && !navHeaderTitle.equals("") ) {
                    //Log.i("Youssef", "navHeaderTitle is " + navHeaderTitle);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editText_navHeaderTitle.setText( navHeaderTitle );
                        }
                    });
                }
                final String navHeaderSubTitle = permanentDao.getNavHeader().subtitle; //interesting how the compiler does not complain for an NPE
                if( navHeaderSubTitle != null && !navHeaderSubTitle.equals("") ) {
                    //Log.i("Youssef", "navHeaderSubTitle is " + navHeaderSubTitle);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editText_navHeaderSubtitle.setText( navHeaderSubTitle );
                        }
                    });
                }
            }
        } ).start();
    }

    void saveNavHeaderImg( final String imagePath ) {
        ( new Thread() {
            public void run() {
                NavHeaderEntity navHeaderEntity = permanentDao.getNavHeader();
                navHeaderEntity.imagePath = imagePath;
                permanentDao.updateNavHeader(navHeaderEntity);
            }
        }).start();
    }

    void loadNavHeaderImage( final ImageButton imageButton_navheadermain ) {
        ( new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                String imagePath = permanentDao.getNavHeader().imagePath; //interesting how the compiler does not complain for an NPE
                if( imagePath != null && !imagePath.equals("") ) {
                    //Log.i("Youssef", "imagePath is " + imagePath);
                    final Uri imageUri = Uri.fromFile(new File(imagePath));
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageButton_navheadermain.setImageURI( imageUri ); //It works even if the path contains spaces.
                        }
                    });
                }
            }
        } ).start();
    }

    private void loadNavEntities() {
        final List<NavEntity> navEntityList = permanentDao.getAllNav();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.navOperations.updateNavItem(0, navEntityList.get(0).title, navEntityList.get(0).iconTag );
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        activity.updateToolbarTitle(0);
                    }
                }, 100); //unfortunately needed.

                //activity.navOperations.addAnItem();
                for( int i = 1 ; i < navEntityList.size() ; i++ ) {
                    activity.navOperations.addAnItem( navEntityList.get(i) );
                }
                activity.navOperations.setupNavigation();
            }
        });
    }

    void loadBb( final int indexOfNavMenuItem, final boolean setColor ) {
        new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                try {
                    final List<NavEntity> navEntityList = permanentDao.getAllNav();
                    activity.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    final String bottombar_backgroundColorTag = navEntityList.get( indexOfNavMenuItem ).bottombar_backgroundColorTag;
                                    if( bottombar_backgroundColorTag == null ||
                                            !bottombar_backgroundColorTag.equalsIgnoreCase("none") ) { //my convention
                                        activity.bottomNavigationView.setVisibility(BottomNavigationView.VISIBLE);
                                        Log.i("Youssef", "showing bb of " + indexOfNavMenuItem);
                                        if( bottombar_backgroundColorTag != null && setColor ) {
                                            activity.setBottomBarBackgroundColor( bottombar_backgroundColorTag );
                                        }
                                    } else if( bottombar_backgroundColorTag.equalsIgnoreCase("none") ) {
                                        activity.bottomNavigationView.setVisibility(BottomNavigationView.INVISIBLE);
                                        Log.i("Youssef", "hiding bb of " + indexOfNavMenuItem);
                                    }

                                }
                            }, 100); //unfortunately needed.

                        }
                    });
                } catch ( Exception e) {
                    Log.e("fatal", "I got an error", e);
                }
            }
        }.start();
    }

    void setNameOfNavItem( final int indexOfNavMenuItem, final String name ) {
        new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                final List<NavEntity> navEntityList = permanentDao.getAllNav();
                NavEntity navEntity = navEntityList.get( indexOfNavMenuItem );
                navEntity.title = name;
                permanentDao.updateNav( navEntity );
            }
        }.start();
    }

    void setBbBackgroundColorTag( final int indexOfNavMenuItem, final String bottombar_backgroundColorTag ) {
        Log.i("Youssef", "in setBbBackgroundColorTag, of " + indexOfNavMenuItem + " to tag "
                + bottombar_backgroundColorTag);
        final List<NavEntity> navEntityList = permanentDao.getAllNav();
        Log.i("Youssef", "in setBbBackgroundColorTag. position 1");
        NavEntity navEntity = navEntityList.get( indexOfNavMenuItem );
        Log.i("Youssef", "in setBbBackgroundColorTag. position 2");
        navEntity.bottombar_backgroundColorTag = bottombar_backgroundColorTag;
        Log.i("Youssef", "in setBbBackgroundColorTag. position 3");
        permanentDao.updateNav( navEntity ); //this might cause sometimes a silent error, such that the statements after it don't work
        Log.i("Youssef", "in setBbBackgroundColorTag, just to make sure : " +
                permanentDao.getAllNav().get(indexOfNavMenuItem).bottombar_backgroundColorTag );
    }

    void deleteBbTable() {
        Log.i("Youssef", "in deleteBbTable");
        final String bottomBarTableName = generateBbTableName( activity.navOperations.getCheckedItemOrder() );
        Log.i("Youssef", "in deleteBbTable");
        deleteTable( bottomBarTableName );
    }

    void deleteBottomNavContentTablesButKeepUpTo( final int startIndex ) { //e.g. 0 to keep only 0 and -1 to remove all.
        int navIndex = activity.navOperations.getCheckedItemOrder();
        int size = activity.bottomMenu.size();
        for( int i = size - 1 ; i > startIndex ; i-- ) {
            deleteTable( generateBottomNavContentTableName( navIndex, i ) );
        }
    }

    private void deleteTable( final String tableName ) {
        db.beginTransaction(); //if you're thinking in using transaction : https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
        try {
            Log.i("Youssef", "before deleting table " + tableName );
            db.execSQL("DROP TABLE IF EXISTS " + tableName + ";");
            Log.i("Youssef", "after deleting table " + tableName );
            db.setTransactionSuccessful(); //to commit
        } catch(Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.toast("Unable to save data internally. Data integrity is not guaranteed.", Toast.LENGTH_LONG);
                }
            });
        } finally {
            db.endTransaction();
        }
    }
}
