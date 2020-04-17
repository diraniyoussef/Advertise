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
import android.view.SubMenu;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.youssefdirani.navmenu_admin.ui.ActiveFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /* //that was the original, but I used the navController.getGraph() which may be better for dynamic changes
        mAppBarConfiguration = new AppBarConfiguration.Builder( //(navController.getGraph()) has a slightly different behavior
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow) //these are real fragments, not the fragment layout
                //It's really interesting that these ids are to both the fragments and to the navigation items. They are and have to be the same !
                .setDrawerLayout(drawer) //the 3 stacked layers (navigation) button needs to set a drawerlayout in order to appear.
                .build();
         */

        //to add a menu programmatically https://stackoverflow.com/questions/36333641/add-items-to-menu-group-programatically-in-navigation-view
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //getting Items Ids to feed it as an argument to AppBarConfiguration.Builder
        menu = navigationView.getMenu();

        final MenuItem menuItem_addNewItem = menu.findItem(R.id.nav_addnewitem);

        menuItem_addNewItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick( MenuItem item ) {
                Log.i("Youssef", "add New Menu Item is clicked.");
                createMenuItem_AlertDialog();
                drawer.closeDrawer(GravityCompat.START); //after this, onResume in the fragment is called. Tested. Still, it's better to make sure using a timer or something.
                return false;
            }
        });

        //Log.i("Youssef", "id of my new item " + menuItem1.getItemId() );
        //Log.i("Youssef", "id of my next new item " + menuItem2.getItemId() );

        setupNavigation();

        //This is a listener for the case when the user chooses a navigation menu item. //https://developer.android.com/reference/android/support/design/widget/NavigationView.OnNavigationItemSelectedListener
        //IT MIGHT WORK CORRECTLY USING https://www.simplifiedcoding.net/android-navigation-drawer-example-using-fragments/
        //as long as you use the displaySelectedScreen method is defined there.
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() { //must be after setupWithNavController
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) { //MenuItem.OnMenuItemClickListener is any better
                Log.i("Youssef", "order of chosen item is " + menuItem.getOrder() ); //this getOrder() is always 0 ! Could it be that it's like menu item and not a submenu tem ?? I doubt it
                Log.i("Youssef", "order of chosen item is " + getCheckedItemOrder( navigationView ) ); //Here this is misleading, because the menu item is selected but not yet checked. The previous item is still checked, then when we close the navigation bar, the new item becomes checked.
                //View.generateViewId(); //https://stackoverflow.com/questions/8937380/how-to-set-id-of-dynamic-created-layout
                Log.i("Youssef", "id of chosen item is " + menuItem.getItemId() ); //gets it correctly
                Log.i("Youssef", "title of chosen item is " + menuItem.getTitle() ); //gets it correctly

                if( menuItem.getItemId() == R.id.nav_addnewitem ) {
                    return false;
                }

                toolbar.setTitle( menuItem.getTitle() );
                navigateToCreatedMenuItem( menuItem.getItemId(), menuItem.getTitle().toString() );

                String title = menuItem.getTitle().toString();
                if( title.equals( getString( R.string.menu_newitem ) ) ) {
                    Log.i("Youssef", "We are requesting to make a new item");
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
                //return false; //this doesn't switch to the corresponding fragment, rather the navigation menu stalls
            }
        });

        client_app_data = getApplicationContext().getSharedPreferences("client_app_data", MODE_PRIVATE);

    }

        private void setupNavigation() {
            /*
            Set<Integer> topLevelDestinations = new HashSet<>();
            topLevelDestinations.add(R.id.fragment1);
            topLevelDestinations.add(R.id.fragment2);
             */
            int[] itemsId = new int[ menu.size() ];
            for( int i = 0; i < menu.size(); i++ ) { //starting from i = 1 because I don't want to show the Home navigation menu item
                itemsId[i] = menu.getItem(i).getItemId();
                Log.i("Youssef", "id is " + itemsId[i] ); //surprisingly enough, all dynamically added items have an id of 0, yet it works in AppBarConfiguration.Builder( itemsId ), since AFAIT a repetitive 0 will implicitly refer to the added items
            }
            mAppBarConfiguration = new AppBarConfiguration.Builder( itemsId )
                    .setDrawerLayout(drawer)
                    .build();
            //NavigationUI.setupActionBarWithNavController(this,navController,drawer);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            //NavigationUI.setupWithNavController(toolbar, navController,mAppBarConfiguration);
/*
            SubMenu topChannelMenu = menu.addSubMenu("Top Channels");
            topChannelMenu.setIcon(R.drawable.ic_menu_camera);
            MenuItem m = topChannelMenu.add("Foo");
            m.setIcon(R.drawable.ic_menu_share);
            topChannelMenu.add("Bar");
            topChannelMenu.add("Baz");
            Menu topChannelMenu1 = menu.addSubMenu("Top Channels1");
            topChannelMenu1.add("Foo1");
            topChannelMenu1.add("Bar1");
            topChannelMenu1.add("Baz1");
 */
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
                    if( userInputText.equals("") ) {
                        return; //we won't make a change
                    }
                    if( userInputText.length() > 25 ) {
                        Toast.makeText(MainActivity.this, "The name you entered is too long.",
                                Toast.LENGTH_LONG).show();
                        return; //we won't make a change
                    }
                    if( isNavigationItemNameAlreadyExisting( userInputText ) ) {
                        Toast.makeText(MainActivity.this, "The name you entered already exists.",
                                Toast.LENGTH_LONG).show();
                        return; //we won't make a change
                    }

                    //Now creating the new menuItem
                    int idOfNewMenuItem = View.generateViewId(); //this can be unnecessary
                    Log.i("Youssef", "id of the menu item is " + idOfNewMenuItem );
                    idOfNewMenuItem = R.id.nav_gallery;
//                    final MenuItem createdMenuItem = menu.add( R.id.main_drawer_group, idOfNewMenuItem, 0, userInputText); //the order argument doesn't work, yet it's fine //MenuItem menuItem1 = menu.add("my new lovely item");
                    //createdMenuItem.setTitle(userInputText);
//                    createdMenuItem.setChecked(true); //must. A technical issue.

                    MenuItem menuItem = menu.findItem(R.id.nav_gallery);
                    menuItem.setVisible(true); //it's good but it doesn't respect the order. it will appear it in the order of the xml file

                    /*
                    navigateToCreatedMenuItem( idOfNewMenuItem, userInputText );
                    setupNavigation(); //making it top-level (root) destination. NOT WORKING
                     */
                    //getLayoutInflater().inflate( R.layout.app_bar_main,null ); //crash
/*
                    final int idOfNewMenuItem1 = idOfNewMenuItem;
                    createdMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick( MenuItem item ) {
                            Log.i("Youssef", "newly created menu item is clicked.");
                            /*
                            //this is probably not needed and it may behave undesirably
                            ActiveFragment fragment = new ActiveFragment();
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.add(R.id.nav_host_fragment, fragment);
                            //ft.replace(R.id.nav_host_fragment, fragment);
                            ft.commit();
                            //navController.navigate( fragment.getId(), bundle );
                             */
                            /*
                            View v = fragment.getView();
                            if( v != null ) {
                                Log.i("Youssef", "view is not null");
                            } else {
                                Log.i("Youssef", "view is null");
                            }
                             */
                            /*createdMenuItem.setChecked(true);
                             */
/*                            navigateToCreatedMenuItem( idOfNewMenuItem1, userInputText );
                            createdMenuItem.setChecked(true); //must. A technical issue.
                            //TextView textView = fragment.root.findViewById(R.id.text_active);
                            //textView.setText("Changing everything from the main activity");
                            return false;
                        }
                    });
 */
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

            private void navigateToCreatedMenuItem( int idOfNewMenuItem, String userInputText ) {
                Bundle bundle = new Bundle();
                bundle.putInt( "id", idOfNewMenuItem );
                bundle.putString( "title", userInputText );


                //navController.navigate(R.id.nav_active, bundle); //navController.navigate(R.id.nav_active); //it works on its own.
                navController.navigate( idOfNewMenuItem, bundle );

                Log.i("Youssef","right before closing the DrawerLayout. Title should be " + userInputText);
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
                    if( userInputText.equals("") ) {
                        return; //we won't make a change
                    }
                    if( userInputText.length() > 25 ) {
                        Toast.makeText(MainActivity.this, "The name you entered is too long.",
                                Toast.LENGTH_LONG).show();
                        return; //we won't make a change
                    }
                    if( isNavigationItemNameAlreadyExisting( userInputText ) ) {
                        Toast.makeText(MainActivity.this, "The name you entered already exists.",
                                Toast.LENGTH_LONG).show();
                        return; //we won't make a change
                    }
                    menuItem.setTitle( userInputText );
                    toolbar.setTitle( userInputText ); //necessary. Another way (probably) is to change the label of the corresponding fragment.
                    /*menuItem.setIcon(R.drawable.ic_menu_send); //it works but limited can't get an image imported by user
                    * */
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
/* //this is put in the Fragment class
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.activity_user_fragment, container, false);
    }
 */

}
