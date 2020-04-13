package com.youssefdirani.navmenu_admin;

import android.content.ComponentCallbacks2;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferences client_app_data;

    public boolean isMemoryLow= false; //this can be checked before we enter or enlarge the database
    @Override
    public void onTrimMemory(int level) { //implemented by ComponentCallbacks2 automatically in AppCompatActivity
        // Determine which lifecycle or system event was raised.
        switch (level) {
            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */
                break;
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */
                //break;
            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */
                //break;
            default:
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                isMemoryLow = true;
                System.gc();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        //mAppBarConfiguration.getDrawerLayout().addView( ?? needed ??
        //to add a menu programmatically https://stackoverflow.com/questions/36333641/add-items-to-menu-group-programatically-in-navigation-view
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        //the piece of code findViewById(R.id.imagebutton_navheadermain) should normally be in
        NavigationUI.setupWithNavController(navigationView, navController);

        client_app_data = getApplicationContext()
                .getSharedPreferences("client_app_data", MODE_PRIVATE);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private final int REQUEST_CODE_LOAD_IMG = 1;
    private ImageButton imageButton_navheadermain;
    private final String imagePath_key = "imageUri";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //this is for the 3 vertical dots
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//this should be before adding menus maybe. findItem https://stackoverflow.com/questions/16500415/findviewbyid-for-menuitem-returns-null (and using menu.findItem may be better than findViewById ?, not sure)
        //Log.i("Youssef", "inside onCreateOptionsMenu");
        return true;
    }

    @Override
    protected void onActivityResult( int reqCode, int resultCode, Intent data ) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == REQUEST_CODE_LOAD_IMG && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();

                if( imageUri == null ) {//should never happen
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                    return;
                }
                /* //maybe unsuccesful tries were they
                //String imageUriPath = imageUri.getPath(); //it returned somthing like this content://media/external/images/media/20753/ORIGINAL/NONE/image/jpeg/1085094180 with some more defects
                //String imageUriPath = imageUri.getEncodedPath();
                //String imageUriPath = imageUri.getLastPathSegment();
                */
                //String imageUriPath = getRealPathFromURI(imageUri); //returned the following /storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20200326-WA0007.jpeg
                /* //this might be an alternative to getRealPathFromURI
                try {
                    String imageUriPath = new File(new URI(imageUri.getPath())).getAbsolutePath();
                } catch( Exception e ) {

                }
                */
                /*
                if( imageUriPath == null ) {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                    return;
                }
                 */
                final SharedPreferences.Editor prefs_editor = client_app_data.edit();
                //prefs_editor.putString(imagePath_key, imageUriPath).apply();
                /*              //fortunately no need to in my case
                int indexOfContent = imageUriPath.indexOf("content");
                if( indexOfContent == -1 ) {
                    prefs_editor.putString(imagePath_key, imageUriPath).apply();
                } else {
                    prefs_editor.putString(imagePath_key, imageUriPath.substring( indexOfContent )).apply();
                }
*/
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = Bitmap.createScaledBitmap(selectedImage, 220, 220, false);
                //saving the image in the scaled size
                String imagePath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        + File.separator + "navmenu.jpg";
                File f = new File( imagePath ); //https://stackoverflow.com/questions/57116335/environment-getexternalstoragedirectory-deprecated-in-api-level-29-java and https://developer.android.com/reference/android/content/Context#getExternalFilesDirs(java.lang.String)
                OutputStream fOut = new FileOutputStream(f);
                selectedImage.compress( Bitmap.CompressFormat.JPEG, 100, fOut );
                fOut.flush();
                fOut.close();
                /*//not needed ??!! https://stackoverflow.com/questions/36472028/using-mediastore-images-media-to-save-bitmap-in-particular-folder and https://stackoverflow.com/questions/4263375/android-saving-created-bitmap-to-directory-on-sd-card/6262219#6262219
                MediaStore.Images.Media.insertImage( getContentResolver(), f.getAbsolutePath(), //https://developer.android.com/reference/android/provider/MediaStore.Images.Media#insertImage(android.content.ContentResolver,%20java.lang.String,%20java.lang.String,%20java.lang.String)
                        f.getName(), f.getName() ); //deprecated and replaced by the following
                */
                /*//this is an alternative to MediaStore.Images.Media.insertImage
                //it seems like not being needed ?
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis());
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //this one
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,  f.getPath());
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1); //https://developer.android.com/reference/android/provider/MediaStore.MediaColumns#IS_PENDING
                }
                 */
                prefs_editor.putString(imagePath_key, imagePath).apply();
                imageButton_navheadermain.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            } catch (IOException e) { //for the sake of fOut.flush and .close
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch( item.getItemId() ) {
            case R.id.action_settings:
                //we want to rename the item

                return true;
/*
            case R.id.configuration:
                final Intent intent = new Intent();
                final String other_panel_index = "-1"; //I can make it any value but -1 is preferred
                intent.putExtra("panelName", panel_name );
                intent.putExtra("panelIndex", panel_index ); //panel index will be used to set the static IP.
                //if the panel index corresponds to this "other panel" we won't assign then any static IP.
                intent.putExtra("panelType", panel_type );
                intent.putExtra("otherPanelIndex", other_panel_index ); //used to compare the panel index with this extra panel index. This is not relevant anymore. I'm only keeping it for compatibility but not used.
                intent.setClass(getApplicationContext(), ConfigPanel.class);
                startActivity(intent);

                return true;
*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
        private String getRealPathFromURI(Uri contentUri) {
            String res = null;
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
            if( cursor != null ) {
                if (cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    res = cursor.getString(column_index);
                }
                cursor.close();
            }
            return res;
        }
    */
    private void loadNavMenuImage() {
        imageButton_navheadermain = findViewById(R.id.imagebutton_navheadermain);
        if( imageButton_navheadermain == null ) { //won't be null I believe
            Log.i("Youssef", "imageButton_navheadermain is null");
        } else {
            //for the client app, it's best to store the image to sd-card and the path to shared preferences https://stackoverflow.com/questions/8586242/how-to-store-images-using-sharedpreference-in-android
            imageButton_navheadermain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, REQUEST_CODE_LOAD_IMG);
                }
            });
            //loading the image from the last known URI (if selected by user)
            String imagePath = client_app_data.getString(imagePath_key, "");
            if (!imagePath.equals("")) {
                Log.i("Youssef", "imagePath is " + imagePath);

                Uri imageUri = Uri.fromFile(new File(imagePath));
            /*
            try {
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = Bitmap.createScaledBitmap(selectedImage, 220, 220, false);//https://stackoverflow.com/questions/18614255/how-to-resize-my-bitmap-as-1616-size-or-3232
                imageButton_navheadermain.setImageBitmap(selectedImage);
            } catch( FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Couldn't get image from memory", Toast.LENGTH_LONG).show();
            }
             */
                imageButton_navheadermain.setImageURI( imageUri ); //This is an alternative to the above try-catch block. It works even if the path contains spaces.
                //imageButton_navheadermain.setImageURI(new Uri.Builder().appendPath( imagePath ).build()); //nice try but doesn't work

            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() { //this is called many times on runtime (it is actually an event listener) whenever the user chooses to navigate https://developer.android.com/reference/android/support/v7/app/AppCompatActivity#onSupportNavigateUp()
        loadNavMenuImage();
        //Log.i("Youssef", "inside onSupportNavigateUp");

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
