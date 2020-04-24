package com.youssefdirani.navmenu_admin;

import android.app.AlertDialog;
import android.content.ComponentCallbacks2;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
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

        setupNavigation();


        // Set the drawer toggle as the DrawerListener
        drawer.addDrawerListener( new ActionBarDrawerToggle(this, drawer,
                R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                Log.i("Youssef", "Drawer has finished closing");
                switch( navItemAction ) {
                    case Delete_Item:
                        navItemAction = ""; //must be first thing to do, to avoid infinite recursive calls.
                        //The following doesn't need the hack if it was launched from the options menu, but when removing an
                        // item and the drawer is still open then it is unreliable
                        int checkedItemOrder = getCheckedItemOrder();
                        if( checkedItemOrder != -1 ) {
                            int size = getMenuItemsCount();
                            if( size < 2 ) { //actually it's == 1
                                Toast.makeText(MainActivity.this, "Cannot delete the last window !", Toast.LENGTH_LONG ).show();
                                return;
                            }
                            menu.removeItem( menu.getItem( checkedItemOrder ).getItemId() );
                            navigateToMenuItem( menu.getItem(0).getItemId(), menu.getItem(0).getTitle().toString() );
                            Toast.makeText(MainActivity.this, "Successful Deletion", Toast.LENGTH_SHORT ).show();
                            Log.i("Youssef", "After nav menu item deletion");
                        }
                        return;
                    case Move_Item_Up:
                        navItemAction = "";
                        int size = getMenuItemsCount();
                        checkedItemOrder = getCheckedItemOrder();
                        if( checkedItemOrder != -1 ) {
                            if( size < 2 ) {
                                Toast.makeText(MainActivity.this,"Cannot reorder.\nJust one item exists !", Toast.LENGTH_LONG ).show();
                                return;
                            }
                            if( checkedItemOrder == 0 ) {
                                Toast.makeText(MainActivity.this,
                                        "Menu Item is already on top !", Toast.LENGTH_LONG ).show();
                                return;
                            }
                            final String title2 = menu.getItem( checkedItemOrder ).getTitle().toString();
                            final Drawable drawable2 = menu.getItem( checkedItemOrder ).getIcon();
                            final int id2 = menu.getItem( checkedItemOrder ).getItemId();
                            final int order2 = menu.getItem( checkedItemOrder ).getOrder();
                            final String title1 = menu.getItem( checkedItemOrder - 1 ).getTitle().toString();
                            final Drawable drawable1 = menu.getItem( checkedItemOrder - 1 ).getIcon();
                            final int id1 = menu.getItem( checkedItemOrder - 1 ).getItemId();
                            final int order1 = menu.getItem( checkedItemOrder - 1 ).getOrder();
                            menu.removeItem( id1 );
                            menu.removeItem( id2 );
                            addMenuItem( id1, order2, title1 ); //the important thing is the order.
                            addMenuItem( id2, order1, title2 ); //this will be checked, and the other one will be unchecked.
                            menu.findItem( id2 ).setIcon( drawable2 );
                            menu.findItem( id1 ).setIcon( drawable1 );
                            //I won't be navigating. Since we already see a fragment, and no need to reenter in it again
                            Toast.makeText(MainActivity.this, "Successful reordering", Toast.LENGTH_SHORT ).show();
                        }
                        drawer.openDrawer(GravityCompat.START);
                        return;
                    case Move_Item_Down:
                        navItemAction = "";
                        size = getMenuItemsCount();
                        checkedItemOrder = getCheckedItemOrder();
                        if( checkedItemOrder != -1 ) {
                            if( size < 2 ) {
                                Toast.makeText(MainActivity.this,"Cannot reorder.\nJust one item exists !", Toast.LENGTH_LONG ).show();
                                return;
                            }
                            if( checkedItemOrder == size - 1 ) {
                                Toast.makeText(MainActivity.this,
                                        "Menu Item is already below all !", Toast.LENGTH_LONG ).show();
                                return;
                            }
                            final String title2 = menu.getItem( checkedItemOrder ).getTitle().toString();
                            final Drawable drawable2 = menu.getItem( checkedItemOrder ).getIcon();
                            final int id2 = menu.getItem( checkedItemOrder ).getItemId();
                            final int order2 = menu.getItem( checkedItemOrder ).getOrder();
                            final String title1 = menu.getItem( checkedItemOrder + 1 ).getTitle().toString();
                            final Drawable drawable1 = menu.getItem( checkedItemOrder + 1 ).getIcon();
                            final int id1 = menu.getItem( checkedItemOrder + 1 ).getItemId();
                            final int order1 = menu.getItem( checkedItemOrder + 1 ).getOrder();
                            menu.removeItem( id1 );
                            menu.removeItem( id2 );
                            addMenuItem( id1, order2, title1 ); //the important thing is the order.
                            addMenuItem( id2, order1, title2 ); //this will be checked, and the other one will be unchecked.
                            menu.findItem( id2 ).setIcon( drawable2 );
                            menu.findItem( id1 ).setIcon( drawable1 );
                            //I won't be navigating. Since we already see a fragment, and no need to reenter in it again
                            Toast.makeText(MainActivity.this, "Successful reordering", Toast.LENGTH_SHORT ).show();
                        }
                        drawer.openDrawer(GravityCompat.START);
                        return;
                    default:

                        return;
                }
            }
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do whatever you want here
                Log.i("Youssef", "Drawer has finished opening");
            }
        });

        client_app_data = getApplicationContext().getSharedPreferences("client_app_data", MODE_PRIVATE);
    }

    private final String Delete_Item = "delete item";
    private final String Move_Item_Up = "move item up";
    private final String Move_Item_Down = "move item down";
    private String navItemAction = "";
        //private final int[] FragmentId = { R.id.nav_0, R.id.nav_1, R.id.nav_2  };
        private final int[] FragmentId = { R.id.nav_home, R.id.nav_fragment1, R.id.nav_fragment2, R.id.nav_fragment3,
            R.id.nav_fragment4, R.id.nav_fragment5 };

        private void setupNavigation() {
            final int size = getMenuItemsCount();
            int[] itemsId = new int[ size ];
            for( int i = 0; i < size; i++ ) {
                //Log.i("setupNavigation", "nav menu item title is " + menu.getItem(i).getTitle());
                itemsId[i] = menu.getItem(i).getItemId();
            }
            mAppBarConfiguration = new AppBarConfiguration.Builder( itemsId )
                    .setDrawerLayout(drawer)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration );
            NavigationUI.setupWithNavController( navigationView, navController );
            //navigationView.setItemBackgroundResource(R.drawable.menubackground);
            navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() { //must be after setupWithNavController
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) { //MenuItem.OnMenuItemClickListener is any better
                    switch( menuItem.getItemId() ) {
                        case R.id.nav_color:
                            Bundle bundle = new Bundle();
                            bundle.putInt( "index_of_navmenuitem" , getCheckedItemOrder() ); //for technical reasons, I have to pass in the checked menu item now. like not being able to determine the checked menu item (weird). Not sure if it really needed, but anyway.
                            navController.navigate( R.id.nav_color, bundle );
                            drawer.closeDrawer(GravityCompat.START);
                            return true;
                        case R.id.nav_addnewitem:
                            Log.i("Youssef", "add New Menu Item is clicked.");
                            if( FragmentId.length <= getMenuItemsCount() ) { //the idea is == actually
                                Toast.makeText(MainActivity.this, "This is the maximum menu items you may have " +
                                                "for this version.\n" +
                                                "Please contact the developer for another version of the app.",
                                        Toast.LENGTH_LONG).show();
                                return false;
                            }
                            createMenuItem_AlertDialog();
                            drawer.closeDrawer(GravityCompat.START);
                            return true;
                        case R.id.nav_menuicon:
                            bundle = new Bundle();
                            bundle.putInt( "index_of_navmenuitem" , getCheckedItemOrder() ); //for technical reasons, I have to pass in the checked menu item now. That is because after getting out of ChooseNavMenuIconFragment class, we cannot determine the checked menu item (weird but this is what happens)
                            navController.navigate( R.id.nav_menuicon, bundle );
                            drawer.closeDrawer(GravityCompat.START);
                            return true;
                        case R.id.nav_renameitem:
                            //we want to rename the item
                            drawer.closeDrawer(GravityCompat.START);

                            int checkedItemOrder = getCheckedItemOrder();
                            //Log.i("Youssef", "checkedItemOrder is " + checkedItemOrder);
                            //Log.i("Youssef", "menu size is " + menu.size() );
                            if( checkedItemOrder != -1 ) {
                                rename_AlertDialog( menu.getItem( checkedItemOrder ) );
                            }
                            //surprisingly enough, navigationView.getCheckedItem().getOrder() always returns 0 thus not working right.
                            //invalidateOptionsMenu(); //https://stackoverflow.com/questions/28042070/how-to-change-the-actionbar-menu-items-icon-dynamically/35911398
                            return true;
                        case R.id.nav_deleteitem:
                            Log.i("Youssef", "deleting...");
                            //the following 2 lines are a hack. Without this hack, it works unreliably
                            drawer.closeDrawer(GravityCompat.START);
                            navItemAction = Delete_Item;
                            return true;
                        case R.id.nav_moveupitem:
                            drawer.closeDrawer(GravityCompat.START);
                            navItemAction = Move_Item_Up;
                            return true;
                        case R.id.nav_movedownitem:
                            drawer.closeDrawer(GravityCompat.START);
                            navItemAction = Move_Item_Down;
                            return true;
                        default:
                            //Log.i("Youssef", "menu item id is " + item.getItemId() );
                    }
                    if( menuItem.getGroupId() != R.id.main_drawer_group ) {
                        return false;
                    }
                    //Log.i("Youssef", "position 1.");
                    navigateToMenuItem( menuItem.getItemId(), menuItem.getTitle().toString() );
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                    //return false; //this doesn't switch to the corresponding fragment, rather the navigation menu stalls
                }

                private void shiftMenuItemsUp(int checkedItemOrder) { //shifting titles from bottom item to the checked item. Checked item won't shift because it will be deleted
                    for( int index = checkedItemOrder + 1 ; index <= menu.size() - 2 ; index++ ) {
                        menu.getItem( index - 1 ).setTitle( menu.getItem( index ).getTitle() );
                    }
                    /*
                    for( int index = checkedItemOrder + 1 ; index <= menu.size() - 2 ; index++  ) {
                        menu.getItem( index - 1 ).setTitle( menu.getItem( index ).getTitle() );
                    }
                    */
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
                    //int idOfNewMenuItem = FragmentId[ menu.size() - 2 ]; //2 because we have the Home and the add-new-item menu items
                    final int id = getAFreeId();
                    addMenuItem( id, menu.getItem( getMenuItemsCount() - 1 ).getOrder() + 1, userInputText );
                    navigateToMenuItem( id, userInputText ); //inside this we have we setChecked to true
                    Toast.makeText(MainActivity.this, "Navigation menu item is successfully added.",
                            Toast.LENGTH_LONG).show();
                }

                private int getAFreeId() { //that has not been used in any of the menu items
                    final int size = getMenuItemsCount();
                    for (int fragmentId : FragmentId) {
                        //Log.i("getAFreeId", "FragmentId[j] " + fragmentId );
                        int i;
                        for (i = 0; i < size; i++) {
                            //Log.i("getAFreeId", "menu.getItem(i).getItemId() " + menu.getItem(i).getItemId());
                            if( menu.getItem(i).getItemId() == fragmentId ) {
                                break;
                            }
                        }
                        if (i == size) { //id not used
                            return fragmentId;
                        }
                    }
                    return 0; //should not be reached
                }

                private void removeAndReputAddItemMenuItem() { //this is to put this item at last
                    String addNewItem_title = menuItem_addNewItem.getTitle().toString();
                    Log.i("Youssef", "R.id.nav_addnewitem was " + R.id.nav_addnewitem);
                    menu.removeItem(R.id.nav_addnewitem);
                    Log.i("Youssef", "R.id.nav_addnewitem is still " + R.id.nav_addnewitem);
                    menuItem_addNewItem = menu.add( R.id.main_drawer_group, R.id.nav_addnewitem, 0, addNewItem_title );
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

        private void addMenuItem( int id, int order, String title ) {
            final MenuItem createdMenuItem = menu.add( R.id.main_drawer_group, id,
                     order, title ); //the order is in coherence with orderInCategory inside activity_main_drawer.xml. It may get larger than getMenuItemsCount(), this is because we delete items without fixing the order then when we may add again a new menu item.
            final int checkedItemOrder = getCheckedItemOrder();
            if( checkedItemOrder >= 0 ) {
                menu.getItem( checkedItemOrder ).setChecked(false);
            }
            Log.i("Youssef", "order of newly created item is " + createdMenuItem.getOrder() );
            createdMenuItem.setChecked(true);
            setupNavigation(); //making it top-level (root) destination.
            toolbar.setTitle( title );
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

            private void navigateToMenuItem( int idOfNewMenuItem, String userInputText ) {
                Bundle bundle = new Bundle();
                bundle.putInt( "id", idOfNewMenuItem );
                bundle.putString( "title", userInputText );
                navController.navigate( idOfNewMenuItem, bundle );
                menu.findItem( idOfNewMenuItem ).setChecked(true);
                drawer.closeDrawer(GravityCompat.START); //after this, onResume in the fragment is called. Tested. Still, it's better to make sure using a timer or something.
                toolbar.setTitle( userInputText );
            }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Youssef", "inside MainActivity : onResume");
        //color change
        /*
        https://stackoverflow.com/questions/18033260/set-background-color-android
        https://stackoverflow.com/questions/40771900/how-to-pass-color-resource-as-parameter-android
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.material_on_primary_emphasis_high_type));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        navigationView.setItemTextColor(...);
        */
        //this can be useful
        //navigateToMenuItem( menu.getItem(0).getItemId(), menu.getItem(0).getTitle().toString() );
        /*
        menu.getItem(0).setChecked(true);
        int checkedItemOrder = getCheckedItemOrder();
        if( checkedItemOrder != -1 ) {
            toolbar.setTitle( menu.getItem( checkedItemOrder ).getTitle() );
        }
         */
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
        //menu.add(R.id.sidemenuitemgroup,25,1,"the second" );

        return true;
    }

    //Related to the navigation menu. Used to retrieve the image from gallery and save it
        public final int CHOOSE_MENUICON_REQUESTCODE = 2;
        @Override
        public void onActivityResult( int reqCode, int resultCode, Intent data ) {
            super.onActivityResult(reqCode, resultCode, data);
            Log.i("onActivityResult", "inside");
            switch( reqCode ) {
                case REQUEST_CODE_LOAD_IMG:
                    if( resultCode == RESULT_OK ) {
                        try {
                            final Uri imageUri = data.getData();

                            if (imageUri == null) {//should never happen
                                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                                return;
                            }
                            final SharedPreferences.Editor prefs_editor = client_app_data.edit();

                            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            selectedImage = Bitmap.createScaledBitmap(selectedImage, 220, 220, false);
                            //saving the image in the scaled size
                            String imagePath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                                    + File.separator + "navmenu.jpg";
                            File f = new File(imagePath); //https://stackoverflow.com/questions/57116335/environment-getexternalstoragedirectory-deprecated-in-api-level-29-java and https://developer.android.com/reference/android/content/Context#getExternalFilesDirs(java.lang.String)
                            OutputStream fOut = new FileOutputStream(f);
                            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
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
                    }
                    break;
                    /*
                case CHOOSE_MENUICON_REQUESTCODE:
                    if( resultCode == RESULT_OK ) {
                        int icon_drawable_id = data.getExtras().getInt("icon_drawable_id");
                        Log.i("ChooseMenuIconFragment", "icon name is " + icon_drawable_id );
                        Drawable myIcon = getResources().getDrawable( icon_drawable_id );
                        //menu.getItem( getCheckedItemOrder() ).setIcon(R.drawable.ic_menu_camera);
                        menu.getItem( getCheckedItemOrder() ).setIcon( myIcon );
                    }
                    break;
                     */
                default:
                    break;
            }
        }

        //these 3 methods better be implementation of an interface defined in class ChooseMenuIconFragment
        /*
        public void disappearChooseNavigationIcon_MenuItem() {
            toolbar.getMenu().findItem(R.id.chooseIcon_menuitem).setVisible(false);
        }
        public void appearChooseNavigationIcon_MenuItem() {
            toolbar.getMenu().findItem(R.id.chooseIcon_menuitem).setVisible(true);
        }
         */
        public void setIconOfCheckedNavMenuItem( String tag, int nav_menuitem_index ) {
            //since getCheckedItemOrder() when called from ChooseNavMenuIconFragment can't know the index (order), so we're using nav_menuitem_index
            //Log.i("seticon", "item index is " + getCheckedItemOrder());
            int icon_drawable_id = getResources().getIdentifier( tag, "drawable", getPackageName() );
            Drawable icon = getResources().getDrawable( icon_drawable_id );
            menu.getItem( nav_menuitem_index ).setIcon( icon );
        }
        public void updateToolbarTitle( int indexOfNewMenuItem ) {
            toolbar.setTitle( menu.getItem( indexOfNewMenuItem ).getTitle() );
        }

        //Related to the navigation menu. Used to know the checked navigation menu item. It's really needed, especially for onNavigationItemSelected
        private int getCheckedItemOrder() {
            final int size = getMenuItemsCount();
            for( int i = 0; i < size; i++ ) {
                MenuItem item = menu.getItem(i);
                if( item.isChecked() ) {
                    return i;
                }
            }
            return -1;
        }

        private int getMenuItemsCount() { //will always be menu.size() - 1, according to the structure of activity_main_drawer.xml file
            int count = 0;
            Log.i("Youssef", "menu size is " + menu.size() );
            for( int i = 0; i < menu.size(); i++ ) { //going till i < menu.size() - 1 is also fine, but anyway.
                MenuItem item = menu.getItem(i);
                if( item.getGroupId() == R.id.main_drawer_group ) {
                    Log.i("Youssef", "order of item is " + item.getOrder());
                    Log.i("Youssef", "id of item is " + item.getItemId());
                    count++;
                }
            }
            return count;
        }

        private boolean isNavigationItemNameAlreadyExisting( String newName ) {
            final int size = getMenuItemsCount();
            for (int i = 0; i < size; i++) {
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
            case R.id.homenavigationitem_mainmenuitem:
                navigateToMenuItem( menu.getItem(0).getItemId(), menu.getItem(0).getTitle().toString() );
                return true;
            case R.id.refresh_mainmenuitem:

                return true;

            case R.id.termsandconditions_menuitem:
                adminTermsAndConditions_AlertDialog();
                return true;

            case R.id.bottombar_renametab:

                Toast.makeText(MainActivity.this, "bottom bar rename successful ",
                        Toast.LENGTH_LONG).show();
                return true;
            case R.id.bottombar_add:

                return true;
            case R.id.bottombar_actionmenu:

                return true;

            default:
                //Log.i("Youssef", "menu item id is " + item.getItemId() );
                return super.onOptionsItemSelected(item);
        }
    }

        private void adminTermsAndConditions_AlertDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Terms And Conditions");
            // Set up the TextView
            final TextView output = new TextView(this);
            output.setPadding(30,16,30,16);
            output.setText("The following terms and conditions only apply to you 'the administrator'." +
                    "\n" +
                    "If you do not agree on any of the following then please don't use the app." +
                    "\n\n" +
                    "1) You may put any image, text, or content that is related to your profile, your business, your company, " +
                    "or your organization." +
                    "\n" +
                    "2) You may not put anything related to others without their consent (explicit or implicit)." +
                    "\n" +
                    "Please make sure you do not embarrass others or hurt their feelings." +
                    "\n" +
                    "3) You may not advertise anything that may harm others, any hateful or abusive words, " +
                    "any violent or repulsive content, any misleading content. Nor you may encourage any of these acts." +
                    "\n" +
                    "4) You may not advertise, show, or encourage any alcoholic drinks, nudity (for men or women), " +
                    "or pornography." +
                    "\n" +
                    "5) If you had to show any lady (others or yourself if you are a female), " +
                    "please make sure she is wearing a vail and that her clothing is spacious and do not clearly defines " +
                    "her body." +
                    "\n\n" +
                    "For any question or technical assistance, please contact the developer +961/70/853721");
            builder.setView(output);
            // Set up the buttons
            builder.setPositiveButton("Ok", null);
            builder.show();
        }


    //Called when the user presses on the stack navigation icon in order to navigate https://developer.android.com/reference/android/support/v7/app/AppCompatActivity#onSupportNavigateUp()
    @Override
    public boolean onSupportNavigateUp() {
        loadNavMenuImage();
        //Log.i("Youssef", "inside onSupportNavigateUp");
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

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
                imageButton_navheadermain.setImageURI( imageUri ); //It works even if the path contains spaces.

            }
        }
    }

}
