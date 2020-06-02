package com.youssefdirani.navmenu_admin;

import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.File;
import java.util.List;

import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;

class DbOperations {
    private MainActivity activity;

    DbOperations(MainActivity activity ) {
        this.activity = activity;

    }

    void onStop() {
        db.close();
    }

    private AppDatabase db;
    private PermanentDao permanentDao;
    private SupportSQLiteDatabase s;

    void mustHaveEntity() {
        ( new Thread() { //opening the database needs to be on a separate thread.
            public void run() {
                db = Room.databaseBuilder( activity,
                        AppDatabase.class, "my_db" ).build();
                permanentDao = db.permanentDao();
                NavHeaderEntity navHeaderEntity = permanentDao.getNavHeader();
                if ( navHeaderEntity == null ) { //it was actually null the first time we entered.
                    //Log.i("Youssef", "inside MainActivity : onStart. No nav header entity exists.");
                    //create a record. We only need 1.
                    navHeaderEntity = new NavHeaderEntity();
                    permanentDao.insertNavHeader( navHeaderEntity ); //it worked even without specifying anything in the just-created navHeaderEntity
                } else {
                    //Log.i("Youssef", "inside MainActivity : onStart. A nav header entity already exists.");
                    //I can't assign the UI here, not until all is inflated and so on.
                }
                List<NavEntity> navEntityList = permanentDao.getAllNav();
                //This is made the first time the admin uses the app
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
        }).start(); //I'm not sequencing threads because I'm assuming that this thread is almost intantaneous

    }

    void concatenateNavTableIndices( int startingIndex ) {//should be called whenever an entity is deleted (except for the last entity).
        //finally it's ok to have e.g. 4 elements like the following indices 0 3 1 2
        List<NavEntity> navEntityList = permanentDao.getAllNav();
        if( startingIndex < navEntityList.size() ) { //startingIndex is passed such that it corresponds for the just deleted element
            for( int i = startingIndex; i < navEntityList.size(); i++ ) {
                //Log.i("Youssef", "inside MainActivity : concatenateNavTableIndices");
                NavEntity navEntity = permanentDao.getNav(i + 1 );
                navEntity.index = i;
                permanentDao.updateNav( navEntity );
            }
        }
    }

    void updateNavHeader() {

        s = db.getOpenHelper().getWritableDatabase();

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
}
