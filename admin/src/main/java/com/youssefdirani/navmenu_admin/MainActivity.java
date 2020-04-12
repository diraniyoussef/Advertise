package com.youssefdirani.navmenu_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        client_app_data = getApplicationContext()
                .getSharedPreferences("client_app_data", MODE_PRIVATE);
    }

    private final int REQUEST_CODE_LOAD_IMG = 1;
    private ImageButton imageButton_navheadermain;
    private SharedPreferences client_app_data;
    private final String imagePath_key = "imageUri";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        imageButton_navheadermain = findViewById(R.id.imagebutton_navheadermain); //return null if put before onCreateOptionsMenu(..)
        //for the client app, it's best to store the image to sd-card and the path to shared preferences https://stackoverflow.com/questions/8586242/how-to-store-images-using-sharedpreference-in-android
        imageButton_navheadermain.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_CODE_LOAD_IMG);
            }
        });
        //loading the image from the last known URI (if selected by user)
        String imagePath = client_app_data.getString( imagePath_key, "" );
        if( !imagePath.equals("") ) {
             //this should work I believe but no need
            Log.i("Youssef", "imagePath is " + imagePath);
/*
            Uri imageUri = Uri.fromFile( new File( imagePath ) );
            try {
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageButton_navheadermain.setImageBitmap(selectedImage);
            } catch( FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Couldn't get image from memory", Toast.LENGTH_LONG).show();
            }
*/
            imageButton_navheadermain.setImageURI( Uri.fromFile( new File( imagePath ) ) ); //it works even if the path contains spaces
            /*imageButton_navheadermain.setImageURI(new Uri.Builder().appendPath( imagePath ).build()); //nice try but doesn't work
             */
        }

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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
                String imageUriPath = getRealPathFromURI(imageUri); //returned the following /storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20200326-WA0007.jpeg
                /* //this might be an alternative to getRealPathFromURI
                try {
                    String imageUriPath = new File(new URI(imageUri.getPath())).getAbsolutePath();
                } catch( Exception e ) {

                }
                */
                if( imageUriPath == null ) {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                    return;
                }
                final SharedPreferences.Editor prefs_editor = client_app_data.edit();
                prefs_editor.putString(imagePath_key, imageUriPath).apply();
/*              //fortunately no need to in my case
                int indexOfContent = imageUriPath.indexOf("content");
                if( indexOfContent == -1 ) {
                    prefs_editor.putString(imagePath_key, imageUriPath).apply();
                } else {
                    prefs_editor.putString(imagePath_key, imageUriPath.substring( indexOfContent )).apply();
                }
*/
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageButton_navheadermain.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
