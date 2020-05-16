package com.youssefdirani.advertise_admin;

import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

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

/*
    List<NavEntity> navEntityList = permanentDao.getAllNav();
    Cursor cursor_bb_0 = db.query("SELECT * FROM bb_0");
                    if( cursor_bb_0.getCount() == 0 && navEntityList.size() >= 1 ) {
        ContentValues contentValues = new ContentValues();
        contentValues.put( "title", navEntityList.get(0).title );
        contentValues.put( "index", 0 );
        //I feel that "icon" should be null, and it's better to be like null.
        db.insert("bb_0", SQLiteDatabase.CONFLICT_NONE, contentValues ); //returns -1 if failure. https://developer.android.com/reference/androidx/sqlite/db/SupportSQLiteDatabase
    }
*/

    void addNavRecord( final String title ) {
        new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                //Log.i("Youssef", "inside DbOperations : adding a nav entity.");
                NavEntity navEntity = new NavEntity();
                navEntity.title = title;
                List<NavEntity> navEntityList = permanentDao.getAllNav();
                navEntity.index = navEntityList.size(); //it's my convention to preserve throughout the app the order of indexes
                permanentDao.insertNav(navEntity);
            }
        }.start();
    }

    void onStop() {
        db_room.close();
    }

    private AppDatabase db_room;
    private PermanentDao permanentDao;
    private SupportSQLiteDatabase db; //https://developer.android.com/reference/androidx/sqlite/db/SupportSQLiteDatabase

    void setInitials() {
        new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                //This is for the first time the admin uses the app
                insertNavHeader();
                insertNavEntity();
                SupportSQLiteOpenHelper supportSQLiteOpenHelper = db_room.getOpenHelper(); //referring to the opened connection to the database. //very good explanation : https://stackoverflow.com/questions/17348480/how-do-i-prevent-sqlite-database-locks - related https://stackoverflow.com/questions/8104832/sqlite-simultaneous-reading-and-writing. And this is related as well https://www.sqlite.org/lockingv3.html
                db = supportSQLiteOpenHelper.getWritableDatabase(); //enableWriteAheadLogging() When write-ahead logging is not enabled (the default), it is not possible for reads and writes to occur on the database at the same time. https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase#create(android.database.sqlite.SQLiteDatabase.CursorFactory)
                //It's better to make a mechanism that corrects itself in case of an error. I.e. make database tables coherent. But I will leave that for now. In case of an error, I believe the app won't crash and it will show something anyway, and for now it's up to the admin to correct whatever he wishes.
                setBottomBar0RecordAndTable0_0();
                //https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase#execSQL(java.lang.String,%20java.lang.Object[])
            }

            private void setBottomBar0RecordAndTable0_0() {
                db.beginTransaction(); //if you're thinking in using transaction : https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
                try {
                    //here we must create a table called bb_0 (which is actually bottombar of the first nav) if not yet created, and it will contain (now we will fetch) all the existing bottom bar tabs info
                    db.execSQL("CREATE TABLE IF NOT EXISTS bb_0 ( " +
                            "uid INTEGER PRIMARY KEY, " +
                            "title TEXT, " +
                            "'index' INTEGER, " +
                            "icon TEXT);");
                    //normally we have to add 4 rows (if bb_0 table is made for the first time.) But I won't. I will keep everything to default as the first time the user (not the admin) opens the app, it will be similar to the admin's app
                    db.execSQL("CREATE TABLE IF NOT EXISTS table0_0 ( " +
                            "uid INTEGER PRIMARY KEY, " +
                            "type TEXT, " +
                            "content TEXT);");
                    //we don't have an entity in table0_0 yet.
                    db.setTransactionSuccessful(); //to commit
                } catch(Exception e) {
                    activity.toast("Unable to save data internally. Data integrity is not guaranteed.", Toast.LENGTH_LONG);
                } finally {
                    db.endTransaction();
                }
            }

            private void insertNavEntity() {
                List<NavEntity> navEntityList = permanentDao.getAllNav();
                if ( navEntityList.size() == 0 ) { //I believe it won't be null the first time we enter. It's just an empty list.
                    //Log.i("Youssef", "inside MainActivity : onStart. No nav entity exists.");
                    //create a record. We must have 1 record. We fetch its name from nav menu
                    NavEntity navEntity = new NavEntity();
                    navEntity.title = activity.navMenu.getItem(0).getTitle().toString();
                    navEntity.index = 0;
                    permanentDao.insertNav( navEntity );
                } else {
                    //Log.i("Youssef", "inside MainActivity : onStart. At least a nav entity already exists.");
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

    void concatenateNavTableIndices( int startingIndex ) {//should be called whenever an entity is deleted (except for the last entity).
        //finally it's ok to have e.g. 4 elements like the following indices 0 3 1 2
        List<NavEntity> navEntityList = permanentDao.getAllNav();
        if( startingIndex < navEntityList.size() ) { //startingIndex is passed such that it corresponds for the just deleted element
            for( int i = startingIndex ; i < navEntityList.size() ; i++ ) {
                //Log.i("Youssef", "inside MainActivity : concatenateNavTableIndices");
                NavEntity navEntity = permanentDao.getNav(i + 1 );
                navEntity.index = i;
                permanentDao.updateNav( navEntity );
            }
        }
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
                                /*
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                                 */
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

    public void loadNavEntities() {
        new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                List<NavEntity> navEntityList = permanentDao.getAllNav();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.navOperations.addAnItem();
                    }
                });
            }
        }.start();
    }
}
