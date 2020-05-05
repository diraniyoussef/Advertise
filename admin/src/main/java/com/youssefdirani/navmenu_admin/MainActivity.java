package com.youssefdirani.navmenu_admin;

import android.app.AlertDialog;
import android.content.ComponentCallbacks2;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class MainActivity extends AppCompatActivity {

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

    Menu navMenu;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    Menu bottomMenu;

    DbOperations dbOperations = new DbOperations( this );
    NavOperations navOperations = new NavOperations( MainActivity.this );
    BottomNavOperations bottomNavOperations = new BottomNavOperations( MainActivity.this );
    OptionsMenu optionsMenu = new OptionsMenu( MainActivity.this );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Youssef", "inside MainActivity : onCreate");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navMenu = navOperations.getNavigationView().getMenu();
        navOperations.startingSetup( navMenu );

        bottomNavigationView = findViewById( R.id.bottomnavview);
        bottomMenu = bottomNavigationView.getMenu();
        bottomNavOperations.setupBottomNavigation();
    }

    //Called when the user presses on the stack navigation icon in order to navigate https://developer.android.com/reference/android/support/v7/app/AppCompatActivity#onSupportNavigateUp()
    @Override
    public boolean onSupportNavigateUp() {
        Log.i("Youssef", "MainActivity - inside onSupportNavigateUp");
        return navOperations.onSupportNavigateUp()
                || super.onSupportNavigateUp();
    }


    public void hideOptionsMenuAndBottomMenu() {
        toolbar.getMenu().setGroupVisible( R.id.optionsmenu_actionitemgroup,false );
        bottomNavigationView.setVisibility( BottomNavigationView.INVISIBLE );
    }
    public void showOptionsMenuAndBottomMenu( int indexOfNavMenuItem ) {
        toolbar.getMenu().setGroupVisible( R.id.optionsmenu_actionitemgroup,true );

        bottomNavigationView.setVisibility( BottomNavigationView.VISIBLE );

    }

    //private final int[] FragmentId = { R.id.nav_0, R.id.nav_1, R.id.nav_2  };
    final int[] FragmentId = { R.id.nav_home, R.id.nav_fragment1, R.id.nav_fragment2, R.id.nav_fragment3,
        R.id.nav_fragment4, R.id.nav_fragment5 };
    private boolean[] bottomBar_exists; //initialized in onStart(). The length of this array is the same as FragmentId's length. And each element of this array is set to false.
    final int[] BottomNavFragmentId = { R.id.navigation_bottom_1, R.id.navigation_bottom_2,
        R.id.navigation_bottom_3, R.id.navigation_bottom_4 };

    void createMenuItem_AlertDialog( final boolean isNavNotBottomNav ) {
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
                    navOperations.addNavMenuItem( id, navMenu.getItem(navOperations.getNavMenuItemsCount() - 1).getOrder() + 1, userInputText );
                    navOperations.navigateToMenuItem( id, userInputText ); //inside this we have we setChecked to true
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
                    size = navOperations.getNavMenuItemsCount();
                } else {
                    size = bottomMenu.size();
                }
                for (int fragmentId : FragmentId) {
                    //Log.i("getAFreeId", "FragmentId[j] " + fragmentId );
                    int i;
                    for (i = 0; i < size; i++) {
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

    private boolean isUserInputTextNotFine( @org.jetbrains.annotations.NotNull String userInputText,
                                          boolean isNavNotBottomNav ) {
        final int Max_Menu_Item_Chars = 25;
        if( userInputText.equals("") ) {
            return true; //we won't make a change
        }
        if( userInputText.contains("_") ) {
            Toast.makeText(MainActivity.this, "We're not allowed to use underscore \"_\" in naming.",
                    Toast.LENGTH_LONG).show();
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

    @Override
    public void onStart() {
        super.onStart();
        bottomBar_exists = new boolean[ FragmentId.length ];
        for( int i = 0; i < FragmentId.length; i++ ) {
            bottomBar_exists[i] = false;
        }

        Log.i("Youssef", "inside MainActivity : onStart");
        //dbOperations
        dbOperations.mustHaveEntity();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Youssef", "inside MainActivity : onResume");

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
        dbOperations.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Youssef", "inside MainActivity : onDestroy");
    }

    //Called when user presses the 3 vertical dots on the right. Initiated when the user presses the 3 vertical dots.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//this should be before adding menus maybe. findItem https://stackoverflow.com/questions/16500415/findviewbyid-for-menuitem-returns-null (and using menu.findItem may be better than findViewById ?, not sure)
        Log.i("Youssef", "MainActivity - inside onCreateOptionsMenu");

        return true;
    }
    public void setFirstOptionsMenuIcon() { //for technical reasons - weird - it's ChooseMenuIconFragment the reason.
        optionsMenu.setFirstOptionsMenuIcon();
    }

    public void setLayoutColor( int linearlayout_id, final String tag ) {
        navOperations.setLayoutColor( linearlayout_id, tag );
    }

    public void setIconOfCheckedMenuItem( String tag, int nav_menuitem_index, String menu ) {
        //since getCheckedItemOrder() when called from ChooseNavMenuIconFragment can't know the index (order), so we're using nav_menuitem_index
        //Log.i("seticon", "item index is " + getCheckedItemOrder());
        if( tag.equalsIgnoreCase("ic_no_icon") ) {
            if( menu.equals("nav menu") ) {
                MenuItem menuItem = navMenu.getItem( nav_menuitem_index );
                menuItem.setIcon(0);
            } else if( menu.equals("bottom nav menu") ) {
                //it's good that the bottom bar still remembers the checked item order after returning back from ChooseNavMenuIconFragment. Here we won't be using nav_menuitem_index, it's been useful for the toolbar
                int bottomNavIndex = bottomNavOperations.getCheckedItemOrder();
                MenuItem menuItem = bottomMenu.getItem( bottomNavIndex );
                menuItem.setIcon(0);
            }
            return;
        }
        int icon_drawable_id = getResources().getIdentifier( tag, "drawable", getPackageName() );
        Drawable icon = getResources().getDrawable( icon_drawable_id );
        if( menu.equals("nav menu") ) {
            navMenu.getItem( nav_menuitem_index ).setIcon( icon );
        } else if( menu.equals("bottom nav menu") ) {
            bottomMenu.getItem( bottomNavOperations.getCheckedItemOrder() ).setIcon( icon ); //it's good that the bottom bar still remembers the checked item order after returning back from ChooseNavMenuIconFragment
        }
    }

    public void updateToolbarTitle( int indexOfNewMenuItem ) {
        toolbar.setTitle( navMenu.getItem( indexOfNewMenuItem ).getTitle() );
    }

    private boolean isNavigationItemNameAlreadyExisting( String newName, boolean isNavNotBottomNav ) {
        final int size;
        if( isNavNotBottomNav ) {
            size = navOperations.getNavMenuItemsCount();
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
    void rename_AlertDialog( MenuItem selectedMenuItem, final boolean isNavNotBottomNav ) {
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
    public boolean onOptionsItemSelected( MenuItem item ) {
        Log.i("Youssef", "MainActivity - inside onOptionsItemSelected");
        return optionsMenu.onOptionsItemSelected( item, super.onOptionsItemSelected(item) );
    }

    //These here are callbacks from other classes.
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
    public void setTopBarHamburgerColor( String tag ) {
        int color_id = getResources().getIdentifier( tag, "color", getPackageName() );
        toolbar.getNavigationIcon().setColorFilter(
                ContextCompat.getColor(this, color_id ), PorterDuff.Mode.SRC_ATOP );
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

    //Related to the navigation menu. Used to retrieve the image from gallery and save it
    @Override
    public void onActivityResult( int reqCode, int resultCode, Intent data ) {
        super.onActivityResult(reqCode, resultCode, data);
        navOperations.onActivityResult( reqCode, resultCode, data );
    }

    @Override
    public void onBackPressed()
    {
        if( navOperations.getCheckedItemOrder() != -1 ) { //root (top-level). IDK how universal this is
            finish();
        }
        super.onBackPressed();  // optional depending on your needs
    }

}
