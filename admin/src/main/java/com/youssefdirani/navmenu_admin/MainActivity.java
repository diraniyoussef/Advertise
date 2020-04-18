package com.youssefdirani.navmenu_admin;

import android.app.AlertDialog;
import android.content.ComponentCallbacks2;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
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
    private NavigationView navigationView;

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

    private DrawerLayout drawer; //This is the "super large" layout element.
    private Menu menu;
    private Toolbar toolbar;
    private NavController navController; //a real (host) fragment
    private MenuItem menuItem_addNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        menu = navigationView.getMenu();

        setupAddNewItem_MenuItem();
        setupNavigation();

        client_app_data = getApplicationContext().getSharedPreferences("client_app_data", MODE_PRIVATE);
    }

    private void setupAddNewItem_MenuItem() {
        menuItem_addNewItem = menu.findItem(R.id.nav_addnewitem);
        menuItem_addNewItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick( MenuItem item ) {
                Log.i("Youssef", "add New Menu Item is clicked.");
                if( FragmentId.length <= menu.size() - 2 ) {
                    Toast.makeText(MainActivity.this, "This is the maximum menu items you may have " +
                                    "for this version.\n" +
                                    "Please contact the developer for another version of the app.",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                createMenuItem_AlertDialog();
                drawer.closeDrawer(GravityCompat.START); //after this, onResume in the fragment is called. Tested. Still, it's better to make sure using a timer or something.
                return false;
            }
        });
    }

    private final int[] FragmentId = { R.id.nav_0, R.id.nav_1, R.id.nav_2  };

        private void setupNavigation() {
            int[] itemsId = new int[ menu.size() ];
            for( int i = 0; i < menu.size(); i++ ) {
                itemsId[i] = menu.getItem(i).getItemId();
            }
            mAppBarConfiguration = new AppBarConfiguration.Builder( itemsId )
                    .setDrawerLayout(drawer)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            //navigationView.setItemBackgroundResource(R.drawable.menubackground);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() { //must be after setupWithNavController
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) { //MenuItem.OnMenuItemClickListener is any better
                    if( menuItem.getItemId() == R.id.nav_addnewitem ) {
                        return false;
                    }
                    toolbar.setTitle( menuItem.getTitle() );
                    Log.i("Youssef", "position 1.");
                    navigateToCreatedMenuItem( menuItem.getItemId(), menuItem.getTitle().toString() );
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                    //return false; //this doesn't switch to the corresponding fragment, rather the navigation menu stalls
                }
            });
        }

        private void createMenuItem_AlertDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please enter the name of the new window");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected
            input.setInputType(InputType.TYPE_CLASS_TEXT); // | InputType.TYPE_TEXT_VARIATION_PASSWORD); //this sets the input as a password, and will mask the text
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String userInputText = input.getText().toString();
                    if(isUserInputTextNotFine(userInputText)) {
                        return;
                    }

                    //Now creating the new menuItem
                    int idOfNewMenuItem = FragmentId[ menu.size() - 2 ]; //2 because we have the Home and the add-new-item menu items
                    final MenuItem createdMenuItem = menu.add( R.id.main_drawer_group, idOfNewMenuItem, 0, userInputText); //the order argument doesn't work, yet it's fine
                    createdMenuItem.setChecked(true); //must. A technical issue.
                    Log.i("Youssef", "position 2.");
                    navigateToCreatedMenuItem( idOfNewMenuItem, userInputText );
                    removeAndReputAddItemMenuItem();
                    setupAddNewItem_MenuItem();
                    setupNavigation(); //making it top-level (root) destination. NOT WORKING
                    toolbar.setTitle( userInputText );
/*
                    final int idOfNewMenuItem1 = idOfNewMenuItem;
                    createdMenuItem.setOnMenuItemClickListener( new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick( MenuItem item ) {
                            Log.i("Youssef", "newly created menu item is clicked.");
                            navigateToCreatedMenuItem( idOfNewMenuItem1, userInputText );
                            createdMenuItem.setChecked(true); //must. A technical issue.
                            return false;
                        }
                    });
 */
                }

                private void removeAndReputAddItemMenuItem() { //this is to put this item at last
                    String addNewItem_title = menuItem_addNewItem.getTitle().toString();
                    Log.i("Youssef", "R.id.nav_addnewitem was " + R.id.nav_addnewitem);
                    menu.removeItem(R.id.nav_addnewitem);
                    Log.i("Youssef", "R.id.nav_addnewitem is still " + R.id.nav_addnewitem);
                    menuItem_addNewItem = menu.add( R.id.main_drawer_group, R.id.nav_addnewitem, 0, addNewItem_title);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

            private boolean isUserInputTextNotFine(@org.jetbrains.annotations.NotNull String userInputText ) {
                final int Max_Menu_Item_Chars = 25;
                if( userInputText.equals("") ) {
                    return true; //we won't make a change
                }
                if( userInputText.length() > Max_Menu_Item_Chars ) {
                    Toast.makeText(MainActivity.this, "The name you entered is too long.",
                            Toast.LENGTH_LONG).show();
                    return true; //we won't make a change
                }
                if( isNavigationItemNameAlreadyExisting( userInputText ) ) {
                    Toast.makeText(MainActivity.this, "The name you entered already exists.",
                            Toast.LENGTH_LONG).show();
                    return true; //we won't make a change
                }
                return false;
            }

            private void navigateToCreatedMenuItem( int idOfNewMenuItem, String userInputText ) {
                Bundle bundle = new Bundle();
                bundle.putInt( "id", idOfNewMenuItem );
                bundle.putString( "title", userInputText );
                navController.navigate( idOfNewMenuItem, bundle );
                drawer.closeDrawer(GravityCompat.START); //after this, onResume in the fragment is called. Tested. Still, it's better to make sure using a timer or something.
                toolbar.setTitle( userInputText ); //before navigating.
            }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private final int REQUEST_CODE_LOAD_IMG = 1;
    private ImageButton imageButton_navheadermain;
    private final String imagePath_key = "imageUri";

    //Called when user presses the 3 vertical dots on the right. Initiated when the user presses the 3 vertical dots.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//this should be before adding menus maybe. findItem https://stackoverflow.com/questions/16500415/findviewbyid-for-menuitem-returns-null (and using menu.findItem may be better than findViewById ?, not sure)
        //Log.i("Youssef", "inside onCreateOptionsMenu");
        return true;
    }

        //Related to the navigation menu. Used to retrieve the image from gallery and save it
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

        //Related to the navigation menu. Used to know the checked navigation menu item
        private int getCheckedItemOrder( NavigationView navigationView ) {
            Menu menu = navigationView.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                if (item.isChecked()) {
                    return i;
                }
            }
            return -1;
        }

        private boolean isNavigationItemNameAlreadyExisting( String newName ) {
            Menu menu = navigationView.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                if( newName.equals( item.getTitle().toString() ) ) {
                    return true;
                }
            }
            return false;
        }

        //Related to the navigation menu. Used to rename the checked navigation menu item
        private void rename_AlertDialog( MenuItem selectedMenuItem ) {
            final MenuItem menuItem = selectedMenuItem;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Renaming");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected
            input.setInputType(InputType.TYPE_CLASS_TEXT); // | InputType.TYPE_TEXT_VARIATION_PASSWORD); //this sets the input as a password, and will mask the text
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String userInputText = input.getText().toString();
                    if(isUserInputTextNotFine(userInputText)) {
                        return;
                    }
                    menuItem.setTitle( userInputText );
                    toolbar.setTitle( userInputText ); //necessary. Another way (probably) is to change the label of the corresponding fragment.
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

    //Called when the user presses a menu item below the 3 vertical dots.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch( item.getItemId() ) {
            case R.id.homenavigationitem_menuitem:
                navController.navigate(R.id.nav_home);
                toolbar.setTitle( menu.findItem( R.id.nav_home ).getTitle() );
                return true;
            case R.id.renamenavigationitem_menuitem:
                //we want to rename the item
                int checkedItemOrder = getCheckedItemOrder( navigationView );
                Log.i("Youssef", "checkedItemOrder is " + checkedItemOrder);
                Log.i("Youssef", "menu size is " + navigationView.getMenu().size() );
                if( checkedItemOrder != -1 ) {
                    rename_AlertDialog( navigationView.getMenu().getItem( checkedItemOrder ) );
                }
                //surprisingly enough, navigationView.getCheckedItem().getOrder() always returns 0 thus not working right.
                //invalidateOptionsMenu(); //https://stackoverflow.com/questions/28042070/how-to-change-the-actionbar-menu-items-icon-dynamically/35911398
                return true;
            case R.id.deletenavigationitem_menuitem:

                return true;
            case R.id.moveupnavigationitem_menuitem:

                return true;
            case R.id.movedownnavigationitem_menuitem:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

        //Related to the navigation menu (header actually)
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

    //Called when the user presses on the stack navigation icon in order to navigate https://developer.android.com/reference/android/support/v7/app/AppCompatActivity#onSupportNavigateUp()
    @Override
    public boolean onSupportNavigateUp() {
        loadNavMenuImage();
        //Log.i("Youssef", "inside onSupportNavigateUp");

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
