package com.youssefdirani.navmenu_admin;

import android.app.AlertDialog;
import android.content.ComponentCallbacks2;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static android.content.res.Resources.getSystem;

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
    private Menu navMenu;
    private Toolbar toolbar;
    private NavController navController; //a real (host) fragment
    private MenuItem menuItem_addNewItem;
    private BottomNavigationView bottomNavigationView;
    private Menu bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Youssef", "inside MainActivity : onCreate");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navMenu = navigationView.getMenu();

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
                            int size = getNavMenuItemsCount();
                            if( size < 2 ) { //actually it's == 1
                                Toast.makeText(MainActivity.this, "Cannot delete the last window !", Toast.LENGTH_LONG ).show();
                                return;
                            }
                            navMenu.removeItem( navMenu.getItem( checkedItemOrder ).getItemId() );
                            navigateToMenuItem( navMenu.getItem(0).getItemId(), navMenu.getItem(0).getTitle().toString() );
                            Toast.makeText(MainActivity.this, "Successful Deletion", Toast.LENGTH_SHORT ).show();
                            Log.i("Youssef", "After nav menu item deletion");
                        }
                        return;
                    case Move_Item_Up:
                        navItemAction = "";
                        int size = getNavMenuItemsCount();
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
                            final String title2 = navMenu.getItem( checkedItemOrder ).getTitle().toString();
                            final Drawable drawable2 = navMenu.getItem( checkedItemOrder ).getIcon();
                            final int id2 = navMenu.getItem( checkedItemOrder ).getItemId();
                            final int order2 = navMenu.getItem( checkedItemOrder ).getOrder();
                            final String title1 = navMenu.getItem( checkedItemOrder - 1 ).getTitle().toString();
                            final Drawable drawable1 = navMenu.getItem( checkedItemOrder - 1 ).getIcon();
                            final int id1 = navMenu.getItem( checkedItemOrder - 1 ).getItemId();
                            final int order1 = navMenu.getItem( checkedItemOrder - 1 ).getOrder();
                            navMenu.removeItem( id1 );
                            navMenu.removeItem( id2 );
                            addNavMenuItem( id1, order2, title1 ); //the important thing is the order.
                            addNavMenuItem( id2, order1, title2 ); //this will be checked, and the other one will be unchecked.
                            navMenu.findItem( id2 ).setIcon( drawable2 );
                            navMenu.findItem( id1 ).setIcon( drawable1 );
                            if( checkedItemOrder - 1 == 0 ) {
                                setFirstOptionsMenuIcon();
                            }
                            //I won't be navigating. Since we already see a fragment, and no need to reenter in it again
                            Toast.makeText(MainActivity.this, "Successful reordering", Toast.LENGTH_SHORT ).show();
                        }
                        drawer.openDrawer(GravityCompat.START);
                        return;
                    case Move_Item_Down:
                        navItemAction = "";
                        size = getNavMenuItemsCount();
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
                            final String title2 = navMenu.getItem( checkedItemOrder ).getTitle().toString();
                            final Drawable drawable2 = navMenu.getItem( checkedItemOrder ).getIcon();
                            final int id2 = navMenu.getItem( checkedItemOrder ).getItemId();
                            final int order2 = navMenu.getItem( checkedItemOrder ).getOrder();
                            final String title1 = navMenu.getItem( checkedItemOrder + 1 ).getTitle().toString();
                            final Drawable drawable1 = navMenu.getItem( checkedItemOrder + 1 ).getIcon();
                            final int id1 = navMenu.getItem( checkedItemOrder + 1 ).getItemId();
                            final int order1 = navMenu.getItem( checkedItemOrder + 1 ).getOrder();
                            navMenu.removeItem( id1 );
                            navMenu.removeItem( id2 );
                            addNavMenuItem( id1, order2, title1 ); //the important thing is the order.
                            addNavMenuItem( id2, order1, title2 ); //this will be checked, and the other one will be unchecked.
                            navMenu.findItem( id2 ).setIcon( drawable2 );
                            navMenu.findItem( id1 ).setIcon( drawable1 );
                            //I won't be navigating. Since we already see a fragment, and no need to reenter in it again
                            if( checkedItemOrder == 0 ) {
                                setFirstOptionsMenuIcon();
                            }
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
/*
        FragmentManager fragmentManager = getSupportFragmentManager(); // not null and can be useful
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //not null
        //Fragment fragment = fragmentManager.findFragmentByTag("home fragment tag"); //is null unfortunately
        //fragmentManager.getFragmentFactory()
 */
        bottomNavigationView = findViewById( R.id.bottomnavview);
        bottomMenu = bottomNavigationView.getMenu();
        setupBottomNavigation();

    }

    public void hideOptionsMenuAndBottomMenu() {
        toolbar.getMenu().setGroupVisible( R.id.optionsmenu_actionitemgroup,false );
        bottomNavigationView.setVisibility( BottomNavigationView.INVISIBLE );
    }
    public void showOptionsMenuAndBottomMenu() {
        toolbar.getMenu().setGroupVisible( R.id.optionsmenu_actionitemgroup,true );
        bottomNavigationView.setVisibility( BottomNavigationView.VISIBLE );
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) { //MenuItem.OnMenuItemClickListener is any better
                Log.i("Youssef", "bottom tab " + getCheckedItemOrder_ofBottomBar() + " is clicked.");
                switch( menuItem.getItemId() ) {
                    case R.id.navigation_bottom_1:
                        //Log.i("Youssef", "bottom tab " + getCheckedItemOrder_ofBottomBar() + " is clicked.");

                        return true;
                    case R.id.navigation_bottom_2:
                        //Log.i("Youssef", "second bottom tab is clicked.");

                        return true;
                    case R.id.navigation_bottom_3:
                        //Log.i("Youssef", "third bottom tab is clicked.");

                        return true;
                    case R.id.navigation_bottom_4:
                        //Log.i("Youssef", "fourth bottom tab is clicked.");

                        return true;
                    default:

                        return false;
                }
            }
        });
    }

    private final String Delete_Item = "delete item";
    private final String Move_Item_Up = "move item up";
    private final String Move_Item_Down = "move item down";
    private String navItemAction = "";
        //private final int[] FragmentId = { R.id.nav_0, R.id.nav_1, R.id.nav_2  };
        private final int[] FragmentId = { R.id.nav_home, R.id.nav_fragment1, R.id.nav_fragment2, R.id.nav_fragment3,
            R.id.nav_fragment4, R.id.nav_fragment5 };

        private void setupNavigation() {
            final int size = getNavMenuItemsCount();
            int[] itemsId = new int[ size ];
            for( int i = 0; i < size; i++ ) {
                //Log.i("setupNavigation", "nav menu item title is " + menu.getItem(i).getTitle());
                itemsId[i] = navMenu.getItem(i).getItemId();
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
                            bundle.putInt( "id_of_layout" , R.id.linearlayout_navheader ); //for technical reasons, I have to pass in the checked menu item now. That is because after getting out of ChooseNavMenuIconFragment class, we cannot determine the checked menu item (weird but this is what happens)
                            bundle.putInt( "index_of_navmenuitem" , getCheckedItemOrder() );
                            bundle.putString( "action", "navigation layout background" );
                            navController.navigate( R.id.nav_color, bundle );
                            drawer.closeDrawer(GravityCompat.START);
                            return true;
                        case R.id.nav_addnewitem:
                            Log.i("Youssef", "add New Menu Item is clicked.");
                            if( FragmentId.length <= getNavMenuItemsCount() ) { //the idea is == actually
                                Toast.makeText(MainActivity.this, "This is the maximum menu items you may have " +
                                                "for this version.\n" +
                                                "Please contact the developer for another version of the app.",
                                        Toast.LENGTH_LONG).show();
                                return false;
                            }
                            createMenuItem_AlertDialog( true );
                            drawer.closeDrawer(GravityCompat.START);
                            return true;
                        case R.id.nav_menuicon:
                            bundle = new Bundle();
                            bundle.putInt( "index_of_navmenuitem" , getCheckedItemOrder() ); //for technical reasons, I have to pass in the checked menu item now. That is because after getting out of ChooseNavMenuIconFragment class, we cannot determine the checked menu item (weird but this is what happens)
                            bundle.putString( "action", "navigation menu icon" );
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
                                rename_AlertDialog( navMenu.getItem( checkedItemOrder ), true );
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
                    for( int index = checkedItemOrder + 1 ; index <= navMenu.size() - 2 ; index++ ) {
                        navMenu.getItem( index - 1 ).setTitle( navMenu.getItem( index ).getTitle() );
                    }
                    /*
                    for( int index = checkedItemOrder + 1 ; index <= menu.size() - 2 ; index++  ) {
                        menu.getItem( index - 1 ).setTitle( menu.getItem( index ).getTitle() );
                    }
                    */
                }
            });
        }

        final int[] BottomNavFragmentId = { R.id.navigation_bottom_1, R.id.navigation_bottom_2,
            R.id.navigation_bottom_3, R.id.navigation_bottom_4 };

        private void createMenuItem_AlertDialog( final boolean isNavNotBottomNav ) {
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
                    if( isUserInputTextNotFine( userInputText, isNavNotBottomNav ) ) {
                        return;
                    }

                    //Now creating the new menuItem
                    //int idOfNewMenuItem = FragmentId[ menu.size() - 2 ]; //2 because we have the Home and the add-new-item menu items
                    final int id;
                    if( isNavNotBottomNav ) {
                        id = getAFreeId( FragmentId, true, navMenu );
                        addNavMenuItem( id, navMenu.getItem(getNavMenuItemsCount() - 1).getOrder() + 1, userInputText );
                        navigateToMenuItem( id, userInputText ); //inside this we have we setChecked to true
                        Toast.makeText(MainActivity.this, "Navigation menu item is successfully added.",
                                Toast.LENGTH_LONG).show();
                    } else { //adding a bottom bar menu item. Actually it's making an item visible again
                        id = getAFreeId( BottomNavFragmentId, false, bottomMenu );
                        bottomMenu.add( R.id.bottombar_menuitems, id,
                                bottomMenu.getItem(bottomMenu.size() - 1 ).getOrder() + 1, userInputText )
                                    .setChecked(true);
                        Toast.makeText(MainActivity.this, "Bottom navigation menu item is successfully added.",
                                Toast.LENGTH_LONG).show();
                    }
                }

                private int getAFreeId( int[] FragmentId, boolean isNavNotBottomNav, Menu navMenu ) { //that has not been used in any of the menu items
                    final int size;
                    if( isNavNotBottomNav ) {
                        size = getNavMenuItemsCount();
                    } else {
                        size = bottomMenu.size();
                    }
                    for (int fragmentId : FragmentId) {
                        //Log.i("getAFreeId", "FragmentId[j] " + fragmentId );
                        int i;
                        for (i = 0; i < size; i++) {
                            //Log.i("getAFreeId", "menu.getItem(i).getItemId() " + menu.getItem(i).getItemId());
                            if( navMenu.getItem(i).getItemId() == fragmentId ) {
                                break;
                            }
                        }
                        if (i == size) { //id not used
                            return fragmentId;
                        }
                    }
                    return 0; //should not be reached
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

        private void addNavMenuItem( int id, int order, String title ) {
            final MenuItem createdMenuItem = navMenu.add( R.id.main_drawer_group, id,
                     order, title ); //the order is in coherence with orderInCategory inside activity_main_drawer.xml. It may get larger than getMenuItemsCount(), this is because we delete items without fixing the order then when we may add again a new menu item.
            final int checkedItemOrder = getCheckedItemOrder();
            if( checkedItemOrder >= 0 ) {
                navMenu.getItem( checkedItemOrder ).setChecked(false);
            }
            Log.i("Youssef", "order of newly created item is " + createdMenuItem.getOrder() );
            createdMenuItem.setChecked(true);
            setupNavigation(); //making it top-level (root) destination.
            toolbar.setTitle( title );
        }

            private boolean isUserInputTextNotFine( @org.jetbrains.annotations.NotNull String userInputText,
                                                  boolean isNavNotBottomNav ) {
                final int Max_Menu_Item_Chars = 25;
                if( userInputText.equals("") ) {
                    return true; //we won't make a change
                }
                if( userInputText.length() > Max_Menu_Item_Chars ) {
                    Toast.makeText(MainActivity.this, "The name you entered is too long.",
                            Toast.LENGTH_LONG).show();
                    return true; //we won't make a change
                }
                if( isNavigationItemNameAlreadyExisting( userInputText, isNavNotBottomNav ) ) {
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
                    navMenu.findItem( idOfNewMenuItem ).setChecked(true);
                    drawer.closeDrawer(GravityCompat.START); //after this, onResume in the fragment is called. Tested. Still, it's better to make sure using a timer or something.
                    toolbar.setTitle( userInputText );
                }
    @Override
    public void onStart() {
        super.onStart();
        Log.i("Youssef", "inside MainActivity : onStart");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.i("Youssef", "inside MainActivity : onPause");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i("Youssef", "inside MainActivity : onStop");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Youssef", "inside MainActivity : onDestroy");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Youssef", "inside MainActivity : onResume");
    }

        public void setFirstOptionsMenuIcon() { //it's because of the visibility thing, I  had to call it from ChooseMenuIconFragment as well
            Drawable first_icon = navMenu.getItem(0).getIcon();
            if( first_icon != null ) {
                Log.i("Youssef", "inside setFirstOptionsMenuIcon : icon is not null");
                toolbar.getMenu().getItem(0).setVisible( true );
                toolbar.getMenu().getItem(0).setIcon( first_icon );
            } else {
                Log.i("Youssef", "inside setFirstOptionsMenuIcon : icon is null");
                toolbar.getMenu().getItem(0).setVisible( false ); //probably better than toolbar.getMenu().getItem(0).setIcon( 0 );
            }
        }

    private final int REQUEST_CODE_LOAD_IMG = 1;
    private ImageButton imageButton_navheadermain;
    private final String imagePath_key = "imageUri";

    //Called when user presses the 3 vertical dots on the right. Initiated when the user presses the 3 vertical dots.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//this should be before adding menus maybe. findItem https://stackoverflow.com/questions/16500415/findviewbyid-for-menuitem-returns-null (and using menu.findItem may be better than findViewById ?, not sure)
        Log.i("Youssef", "MainActivity - inside onCreateOptionsMenu");
        setFirstOptionsMenuIcon();
        return true;
    }

        //Related to the navigation menu. Used to retrieve the image from gallery and save it
        //public final int CHOOSE_MENUICON_REQUESTCODE = 2;
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

                     */
                default:
                    break;
            }
        }

        public void setLayoutColor( int linearlayout_id, String tag ) {
            LinearLayout linearLayout = findViewById( linearlayout_id );
            int color_id = getResources().getIdentifier( tag, "color", getPackageName() );
            linearLayout.setBackgroundColor( getResources().getColor( color_id ) );
        }
        public void setIconOfCheckedNavMenuItem( String tag, int nav_menuitem_index, String menu ) {
            //since getCheckedItemOrder() when called from ChooseNavMenuIconFragment can't know the index (order), so we're using nav_menuitem_index
            //Log.i("seticon", "item index is " + getCheckedItemOrder());
            if( tag.equalsIgnoreCase("ic_no_icon") ) {
                if( menu.equals("nav menu") ) {
                    MenuItem menuItem = navMenu.getItem( nav_menuitem_index );
                    menuItem.setIcon(0);
                    /* //that was before I try setIcon(0)
                    int order = menuItem.getOrder();
                    int id = menuItem.getItemId();
                    String title = menuItem.getTitle().toString();
                    navMenu.removeItem( id );
                    addNavMenuItem( id, order, title );
                    */
                } else if( menu.equals("bottom nav menu") ) {
                    //it's good that the bottom bar still remembers the checked item order after returning back from ChooseNavMenuIconFragment. Here we won't be using nav_menuitem_index, it's been useful for the toolbar
                    int bottomNavIndex = getCheckedItemOrder_ofBottomBar();
                    MenuItem menuItem = bottomMenu.getItem( bottomNavIndex );
                    menuItem.setIcon(0);
                    /*//it was before I try setIcon(0)
                    int order = menuItem.getOrder();
                    int id = menuItem.getItemId();
                    String title = menuItem.getTitle().toString();
                    bottomMenu.removeItem( id );
                    bottomMenu.add( R.id.bottombar_actionmenu, id, order, title );
                    bottomMenu.findItem( id ).setChecked(true);
                    */
                }
                return;
            }
            int icon_drawable_id = getResources().getIdentifier( tag, "drawable", getPackageName() );
            Drawable icon = getResources().getDrawable( icon_drawable_id );
            if( menu.equals("nav menu") ) {
                navMenu.getItem( nav_menuitem_index ).setIcon( icon );
            } else if( menu.equals("bottom nav menu") ) {
                bottomMenu.getItem( getCheckedItemOrder_ofBottomBar() ).setIcon( icon ); //it's good that the bottom bar still remembers the checked item order after returning back from ChooseNavMenuIconFragment
            }
        }
        public void updateToolbarTitle( int indexOfNewMenuItem ) {
            toolbar.setTitle( navMenu.getItem( indexOfNewMenuItem ).getTitle() );
        }

        //Related to the navigation menu. Used to know the checked navigation menu item. It's really needed, especially for onNavigationItemSelected
        private int getCheckedItemOrder() { //this is different than OnNavigationItemSelectedListener in that in the latter, it's not yet checked, but in the process of being so. While with getCheckedItemOrder it's already checked.
            final int size = getNavMenuItemsCount();
            for( int i = 0; i < size; i++ ) {
                MenuItem item = navMenu.getItem(i);
                if( item.isChecked() ) {
                    return i;
                }
            }
            return -1;
        }
        private int getCheckedItemOrder_ofBottomBar() {
            for( int i = 0; i < bottomMenu.size(); i++ ) {
                if( bottomMenu.getItem(i).isChecked() ) {
                    return i;
                }
            }
            return -1;
        }

        private int getNavMenuItemsCount() { //will always be menu.size() - 1, according to the structure of activity_main_drawer.xml file
            int count = 0;
            Log.i("Youssef", "menu size is " + navMenu.size() );
            for( int i = 0; i < navMenu.size(); i++ ) { //going till i < menu.size() - 1 is also fine, but anyway.
                MenuItem item = navMenu.getItem(i);
                if( item.getGroupId() == R.id.main_drawer_group ) {
                    Log.i("Youssef", "order of item is " + item.getOrder());
                    Log.i("Youssef", "id of item is " + item.getItemId());
                    count++;
                }
            }
            return count;
        }

        private boolean isNavigationItemNameAlreadyExisting( String newName, boolean isNavNotBottomNav ) {
            final int size;
            if( isNavNotBottomNav ) {
                size = getNavMenuItemsCount();
            } else {
                size = bottomMenu.size();
            }
            for (int i = 0; i < size; i++) {
                MenuItem item;
                if( isNavNotBottomNav ) {
                    item = navMenu.getItem(i);
                } else {
                    item = bottomMenu.getItem(i);
                }
                if( newName.equalsIgnoreCase( item.getTitle().toString() ) ) {
                    return true;
                }
            }
            return false;
        }

        //Related to the navigation menu. Used to rename the checked navigation menu item
        private void rename_AlertDialog(MenuItem selectedMenuItem, final boolean isNavNotBottomNav ) {
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
                    if( isUserInputTextNotFine( userInputText, isNavNotBottomNav ) ) {
                        return;
                    }
                    menuItem.setTitle( userInputText );
                    if( isNavNotBottomNav ) {
                        toolbar.setTitle(userInputText); //necessary. Another way (probably) is to change the label of the corresponding fragment.
                    }
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


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i("Youssef", "MainActivity - inside onPrepareOptionsMenu");
        return true;
    }


    //Called when the user presses a menu item below the 3 vertical dots.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("Youssef", "MainActivity - inside onOptionsItemSelected");
        switch( item.getItemId() ) {
            case R.id.homenavigationitem_mainmenuitem:
                navigateToMenuItem( navMenu.getItem(0).getItemId(), navMenu.getItem(0).getTitle().toString() );
                return true;
            case R.id.refresh_mainmenuitem:
                //for fetching data from server

                return true;
            case R.id.statusbar_color:
                Bundle bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , getCheckedItemOrder() );
                bundle.putString( "action", "status bar background color" );
                navController.navigate( R.id.nav_color, bundle );
                return true;
            case R.id.statusbar_iconcolor:
                boolean isChecked = !item.isChecked();
                item.setChecked(isChecked);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    View decor = getWindow().getDecorView();
                    if( isChecked ) { //make dark
                        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        decor.setSystemUiVisibility(0);
                    }
                }
                return true;
            case R.id.topbar_backgroundcolor:
                bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , getCheckedItemOrder() );
                bundle.putString( "action", "top bar background color" );
                navController.navigate( R.id.nav_color, bundle );
                return true;
            case R.id.topbar_titlecolor:
                bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , getCheckedItemOrder() );
                bundle.putString( "action", "top bar title color" );
                navController.navigate( R.id.nav_color, bundle );
                return true;
            case R.id.topbar_3dotscolor:
                bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , getCheckedItemOrder() );
                bundle.putString( "action", "top bar 3-dots color" );
                navController.navigate( R.id.nav_color, bundle );
                return true;
            case R.id.bottombar_actionmenu:
                if( bottomNavigationView.getVisibility() == BottomNavigationView.VISIBLE ) {
                    Log.i("Youssef", "after pressing the bottombar action menu. Bottom bar is visible");
                    item.getSubMenu().setGroupVisible( R.id.bottombar_exists,true ); //it works but toolbar.getMenu().setGroupEnabled( ... ); doesn't work maybe because the group is not a direct child
                    toolbar.getMenu().findItem( R.id.bottombar_add ).setVisible( false );
                } else {
                    Log.i("Youssef", "after pressing the bottombar action menu. Bottom bar is invisible");
                    toolbar.getMenu().findItem( R.id.bottombar_add ).setVisible( true );
                    item.getSubMenu().setGroupVisible( R.id.bottombar_exists,false );
                }
                return true;
            case R.id.bottombar_add:
                bottomNavigationView.setVisibility( BottomNavigationView.VISIBLE );
                bottomMenu.getItem(0).setChecked(true); //this is my convention
                return true;
            case R.id.bottombar_remove:
                bottomNavigationView.setVisibility( BottomNavigationView.INVISIBLE );
                return true;
            case R.id.bottombar_color:
                bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , getCheckedItemOrder() );
                bundle.putString( "action", "bottom bar background color" );
                navController.navigate( R.id.nav_color, bundle );
                return true;
            case R.id.bottombar_addtab:
                if( bottomMenu.size() >= BottomNavFragmentId.length )  {
                    Toast.makeText(MainActivity.this, "This is the maximum tabs you may have " +
                            "for this version.\n" +
                            "Please contact the developer for another version of the app.", Toast.LENGTH_LONG ).show();
                    return false;
                }
                createMenuItem_AlertDialog( false );
                return true;
            case R.id.bottombar_menuicon:
                bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , getCheckedItemOrder() ); //just for the toolbar name. weird. We know we won't affect the navigation menu item icon.
                bundle.putString( "action", "bottom bar menu item" );
                navController.navigate( R.id.nav_menuicon, bundle );
                return true;
            case R.id.bottombar_renametab:
                int checkedItemOrder = getCheckedItemOrder_ofBottomBar();
                if( checkedItemOrder != -1 ) {
                    rename_AlertDialog( bottomMenu.getItem( checkedItemOrder ), false );
                }
                return true;
            case R.id.bottombar_deletetab:
                checkedItemOrder = getCheckedItemOrder_ofBottomBar();
                if( checkedItemOrder != -1 ) {
                    bottomMenu.removeItem( bottomMenu.getItem( checkedItemOrder ).getItemId() );
                    Toast.makeText(MainActivity.this, "Bottom menu item is removed successfully",
                            Toast.LENGTH_LONG ).show();
                }
                return true;
            case R.id.bottombar_movetableft:
                int size = bottomMenu.size();
                checkedItemOrder = getCheckedItemOrder_ofBottomBar();
                if( checkedItemOrder != -1 ) {
                    if( size < 2 ) {
                        Toast.makeText(MainActivity.this,"Cannot reorder.\n" +
                                "Just one item exists !", Toast.LENGTH_LONG ).show();
                        return false;
                    }
                    if( checkedItemOrder == 0 ) {
                        Toast.makeText(MainActivity.this,
                                "Menu Item is already on the most left !", Toast.LENGTH_LONG ).show();
                        return false;
                    }
                    final String title2 = bottomMenu.getItem( checkedItemOrder ).getTitle().toString();
                    final Drawable drawable2 = bottomMenu.getItem( checkedItemOrder ).getIcon();
                    final int id2 = bottomMenu.getItem( checkedItemOrder ).getItemId();
                    final int order2 = bottomMenu.getItem( checkedItemOrder ).getOrder();
                    final String title1 = bottomMenu.getItem( checkedItemOrder - 1 ).getTitle().toString();
                    final Drawable drawable1 = bottomMenu.getItem( checkedItemOrder - 1 ).getIcon();
                    final int id1 = bottomMenu.getItem( checkedItemOrder - 1 ).getItemId();
                    final int order1 = bottomMenu.getItem( checkedItemOrder - 1 ).getOrder();
                    bottomMenu.removeItem( id1 );
                    bottomMenu.removeItem( id2 );
                    bottomMenu.add( R.id.bottombar_menuitems, id1, order2, title1 );
                    bottomMenu.add( R.id.bottombar_menuitems, id2, order1, title2 )
                            .setChecked(true);
                    bottomMenu.findItem( id2 ).setIcon( drawable2 );
                    bottomMenu.findItem( id1 ).setIcon( drawable1 );
                    //I won't be navigating. Since we already see a fragment, and no need to reenter in it again
                    Toast.makeText(MainActivity.this, "Successful reordering", Toast.LENGTH_SHORT ).show();
                }
                return true;
            case R.id.bottombar_movetabright:
                size = bottomMenu.size();
                checkedItemOrder = getCheckedItemOrder_ofBottomBar();
                if( checkedItemOrder != -1 ) {
                    if( size < 2 ) {
                        Toast.makeText(MainActivity.this,"Cannot reorder.\n" +
                                "Just one item exists !", Toast.LENGTH_LONG ).show();
                        return false;
                    }
                    if( checkedItemOrder == size - 1 ) {
                        Toast.makeText(MainActivity.this,
                                "Menu Item is already on the most right !", Toast.LENGTH_LONG ).show();
                        return false;
                    }
                    final String title2 = bottomMenu.getItem( checkedItemOrder ).getTitle().toString();
                    final Drawable drawable2 = bottomMenu.getItem( checkedItemOrder ).getIcon();
                    final int id2 = bottomMenu.getItem( checkedItemOrder ).getItemId();
                    final int order2 = bottomMenu.getItem( checkedItemOrder ).getOrder();
                    final String title1 = bottomMenu.getItem( checkedItemOrder + 1 ).getTitle().toString();
                    final Drawable drawable1 = bottomMenu.getItem( checkedItemOrder + 1 ).getIcon();
                    final int id1 = bottomMenu.getItem( checkedItemOrder + 1 ).getItemId();
                    final int order1 = bottomMenu.getItem( checkedItemOrder + 1 ).getOrder();
                    bottomMenu.removeItem( id1 );
                    bottomMenu.removeItem( id2 );
                    bottomMenu.add( R.id.bottombar_menuitems, id1, order2, title1 );
                    bottomMenu.add( R.id.bottombar_menuitems, id2, order1, title2 )
                            .setChecked(true);
                    bottomMenu.findItem( id2 ).setIcon( drawable2 );
                    bottomMenu.findItem( id1 ).setIcon( drawable1 );
                    //I won't be navigating. Since we already see a fragment, and no need to reenter in it again
                    Toast.makeText(MainActivity.this, "Successful reordering", Toast.LENGTH_SHORT ).show();
                }
                return true;
            case R.id.termsandconditions_menuitem:
                adminTermsAndConditions_AlertDialog();
                return true;
            default:
                //Log.i("Youssef", "menu item id is " + item.getItemId() );
                return super.onOptionsItemSelected(item);
        }
    }

        public void setStatusBarColor( String tag ) {
            int color_id = getResources().getIdentifier( tag, "color", getPackageName() );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor( ContextCompat.getColor(this, color_id ) );
            }
        }
        public void setTopBarBackgroundColor( String tag ) {
            int color_id = getResources().getIdentifier( tag, "color", getPackageName() );
            toolbar.setBackgroundColor( ContextCompat.getColor(this, color_id) );
        }
        public void setTopBarTitleColor( String tag ) {
            int color_id = getResources().getIdentifier( tag, "color", getPackageName() );
            toolbar.setTitleTextColor( getResources().getColor( color_id ) ); //the action bar text
        }
        public void setTopBar3DotsColor( String tag ) {
            int color_id = getResources().getIdentifier( tag, "color", getPackageName() );
            toolbar.getOverflowIcon().setColorFilter(
                    ContextCompat.getColor(this, color_id ), PorterDuff.Mode.SRC_ATOP );
        }
        public void setBottomBarBackgroundColor( String tag ) {
            int color_id = getResources().getIdentifier( tag, "color", getPackageName() );
            bottomNavigationView.setBackgroundColor( ContextCompat.getColor(this, color_id) );
        }


/* //Trying to change to color of the hamburger icon, but not working...
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor( getResources().getColor( R.color.colorGreen ) );
*/
    //the 3 dots

    //navigationView.setItemTextColor( R.color.colorGreen );



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
                    "please make sure she is wearing a vail and that her clothing is spacious enough not to clearly define " +
                    "her body." +
                    "\n\n" +
                    "For any question or technical assistance, please contact the developer +961/70/853721");
            final ScrollView scrollView = new ScrollView(this);
            scrollView.arrowScroll(View.SCROLL_AXIS_VERTICAL);
            scrollView.addView( output );
            builder.setView(scrollView);
            // Set up the buttons
            builder.setPositiveButton("Ok", null);
            builder.show();
        }

    //Called when the user presses on the stack navigation icon in order to navigate https://developer.android.com/reference/android/support/v7/app/AppCompatActivity#onSupportNavigateUp()
    @Override
    public boolean onSupportNavigateUp() {
        loadNavMenuImage();
        Log.i("Youssef", "MainActivity - inside onSupportNavigateUp");
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

    @Override
    public void onBackPressed()
    {
        if( getCheckedItemOrder() != -1 ) { //root (top-level). IDK how universal this is
            finish();
        }
        super.onBackPressed();  // optional depending on your needs
    }

}
