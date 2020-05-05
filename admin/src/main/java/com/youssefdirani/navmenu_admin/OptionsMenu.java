package com.youssefdirani.navmenu_admin;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;

class OptionsMenu {
    private MainActivity activity;

    OptionsMenu(MainActivity activity ) {
        this.activity = activity;

    }

    boolean onOptionsItemSelected( MenuItem item, boolean defautReturnValue ) {
        switch( item.getItemId() ) {
            case R.id.homenavigationitem_mainmenuitem:
                activity.navOperations.navigateToMenuItem( activity.navMenu.getItem(0).getItemId(),
                        activity.navMenu.getItem(0).getTitle().toString() );
                return true;
            case R.id.refresh_mainmenuitem:
                //for fetching data from server
                Toast.makeText( activity, "Refreshing Data to/from Server.",
                        Toast.LENGTH_LONG ).show();
                return true;
            case R.id.statusbar_color:
                Bundle bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , activity.navOperations.getCheckedItemOrder() );
                bundle.putString( "action", "status bar background color" );
                activity.navOperations.navController.navigate( R.id.nav_color, bundle );
                return true;
            case R.id.statusbar_iconcolor:
                boolean isChecked = !item.isChecked();
                item.setChecked(isChecked);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    View decor = activity.getWindow().getDecorView();
                    if( isChecked ) { //make dark
                        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        decor.setSystemUiVisibility(0);
                    }
                }
                return true;
            case R.id.topbar_backgroundcolor:
                bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , activity.navOperations.getCheckedItemOrder() );
                bundle.putString( "action", "top bar background color" );
                activity.navOperations.navController.navigate( R.id.nav_color, bundle );
                return true;
            case R.id.topbar_hamburgercolor:
                bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , activity.navOperations.getCheckedItemOrder() );
                bundle.putString( "action", "top bar hamburger color" );
                activity.navOperations.navController.navigate( R.id.nav_color, bundle );
                return true;
            case R.id.topbar_titlecolor:
                bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , activity.navOperations.getCheckedItemOrder() );
                bundle.putString( "action", "top bar title color" );
                activity.navOperations.navController.navigate( R.id.nav_color, bundle );
                return true;
            case R.id.topbar_3dotscolor:
                bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , activity.navOperations.getCheckedItemOrder() );
                bundle.putString( "action", "top bar 3-dots color" );
                activity.navOperations.navController.navigate( R.id.nav_color, bundle );
                return true;
            case R.id.bottombar_actionmenu:
                if( activity.bottomNavigationView.getVisibility() == BottomNavigationView.VISIBLE ) {
                    Log.i("Youssef", "after pressing the bottombar action menu. Bottom bar is visible");
                    item.getSubMenu().setGroupVisible( R.id.bottombar_exists,true ); //it works but toolbar.getMenu().setGroupEnabled( ... ); doesn't work maybe because the group is not a direct child
                    activity.toolbar.getMenu().findItem( R.id.bottombar_add ).setVisible( false );
                } else {
                    Log.i("Youssef", "after pressing the bottombar action menu. Bottom bar is invisible");
                    activity.toolbar.getMenu().findItem( R.id.bottombar_add ).setVisible( true );
                    item.getSubMenu().setGroupVisible( R.id.bottombar_exists,false );
                }
                return true;
            case R.id.bottombar_add:
                activity.bottomNavigationView.setVisibility( BottomNavigationView.VISIBLE );
                activity.bottomMenu.getItem(0).setChecked(true); //this is my convention
                return true;
            case R.id.bottombar_remove:
                activity.bottomMenu.getItem(0).setChecked(true); //this is my convention. It is useful when a user navigates to a navigation menu item (because we use getCheckedItemOrder_ofBottomBar() for the table name)
                activity.bottomNavigationView.setVisibility( BottomNavigationView.INVISIBLE );
                return true;
            case R.id.bottombar_color:
                bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , activity.navOperations.getCheckedItemOrder() );
                bundle.putString( "action", "bottom bar background color" );
                activity.navOperations.navController.navigate( R.id.nav_color, bundle );
                return true;
            case R.id.bottombar_addtab:
                if( activity.bottomMenu.size() >= activity.BottomNavFragmentId.length )  {
                    Toast.makeText( activity, "This is the maximum tabs you may have " +
                            "for this version.\n" +
                            "Please contact the developer for another version of the app.", Toast.LENGTH_LONG ).show();
                    return false;
                }
                activity.createMenuItem_AlertDialog( false );
                return true;
            case R.id.bottombar_menuicon:
                bundle = new Bundle();
                bundle.putInt( "index_of_navmenuitem" , activity.navOperations.getCheckedItemOrder() ); //just for the toolbar name. weird. We know we won't affect the navigation menu item icon.
                bundle.putString( "action", "bottom bar menu item" );
                activity.navOperations.navController.navigate( R.id.nav_menuicon, bundle );
                return true;
            case R.id.bottombar_renametab:
                int checkedItemOrder = activity.bottomNavOperations.getCheckedItemOrder();
                if( checkedItemOrder != -1 ) {
                    activity.rename_AlertDialog( activity.bottomMenu.getItem( checkedItemOrder ), false );
                }
                return true;
            case R.id.bottombar_deletetab:
                checkedItemOrder = activity.bottomNavOperations.getCheckedItemOrder();
                if( checkedItemOrder != -1 ) {
                    activity.bottomMenu.removeItem( activity.bottomMenu.getItem( checkedItemOrder ).getItemId() );
                    Toast.makeText( activity, "Bottom menu item is removed successfully",
                            Toast.LENGTH_LONG ).show();
                }
                return true;
            case R.id.bottombar_movetableft:
                int size = activity.bottomMenu.size();
                checkedItemOrder = activity.bottomNavOperations.getCheckedItemOrder();
                if( checkedItemOrder != -1 ) {
                    if( size < 2 ) {
                        Toast.makeText( activity,"Cannot reorder.\n" +
                                "Just one item exists !", Toast.LENGTH_LONG ).show();
                        return false;
                    }
                    if( checkedItemOrder == 0 ) {
                        Toast.makeText( activity,
                                "Menu Item is already on the most left !", Toast.LENGTH_LONG ).show();
                        return false;
                    }
                    final String title2 = activity.bottomMenu.getItem( checkedItemOrder ).getTitle().toString();
                    final Drawable drawable2 = activity.bottomMenu.getItem( checkedItemOrder ).getIcon();
                    final int id2 = activity.bottomMenu.getItem( checkedItemOrder ).getItemId();
                    final int order2 = activity.bottomMenu.getItem( checkedItemOrder ).getOrder();
                    final String title1 = activity.bottomMenu.getItem( checkedItemOrder - 1 ).getTitle().toString();
                    final Drawable drawable1 = activity.bottomMenu.getItem( checkedItemOrder - 1 ).getIcon();
                    final int id1 = activity.bottomMenu.getItem( checkedItemOrder - 1 ).getItemId();
                    final int order1 = activity.bottomMenu.getItem( checkedItemOrder - 1 ).getOrder();
                    activity.bottomMenu.removeItem( id1 );
                    activity.bottomMenu.removeItem( id2 );
                    activity.bottomMenu.add( R.id.bottombar_menuitems, id1, order2, title1 );
                    activity.bottomMenu.add( R.id.bottombar_menuitems, id2, order1, title2 )
                            .setChecked(true);
                    activity.bottomMenu.findItem( id2 ).setIcon( drawable2 );
                    activity.bottomMenu.findItem( id1 ).setIcon( drawable1 );
                    //I won't be navigating. Since we already see a fragment, and no need to reenter in it again
                    Toast.makeText( activity, "Successful reordering", Toast.LENGTH_SHORT ).show();
                }
                return true;
            case R.id.bottombar_movetabright:
                size = activity.bottomMenu.size();
                checkedItemOrder = activity.bottomNavOperations.getCheckedItemOrder();
                if( checkedItemOrder != -1 ) {
                    if( size < 2 ) {
                        Toast.makeText( activity,"Cannot reorder.\n" +
                                "Just one item exists !", Toast.LENGTH_LONG ).show();
                        return false;
                    }
                    if( checkedItemOrder == size - 1 ) {
                        Toast.makeText( activity,
                                "Menu Item is already on the most right !", Toast.LENGTH_LONG ).show();
                        return false;
                    }
                    final String title2 = activity.bottomMenu.getItem( checkedItemOrder ).getTitle().toString();
                    final Drawable drawable2 = activity.bottomMenu.getItem( checkedItemOrder ).getIcon();
                    final int id2 = activity.bottomMenu.getItem( checkedItemOrder ).getItemId();
                    final int order2 = activity.bottomMenu.getItem( checkedItemOrder ).getOrder();
                    final String title1 = activity.bottomMenu.getItem( checkedItemOrder + 1 ).getTitle().toString();
                    final Drawable drawable1 = activity.bottomMenu.getItem( checkedItemOrder + 1 ).getIcon();
                    final int id1 = activity.bottomMenu.getItem( checkedItemOrder + 1 ).getItemId();
                    final int order1 = activity.bottomMenu.getItem( checkedItemOrder + 1 ).getOrder();
                    activity.bottomMenu.removeItem( id1 );
                    activity.bottomMenu.removeItem( id2 );
                    activity.bottomMenu.add( R.id.bottombar_menuitems, id1, order2, title1 );
                    activity.bottomMenu.add( R.id.bottombar_menuitems, id2, order1, title2 )
                            .setChecked(true);
                    activity.bottomMenu.findItem( id2 ).setIcon( drawable2 );
                    activity.bottomMenu.findItem( id1 ).setIcon( drawable1 );
                    //I won't be navigating. Since we already see a fragment, and no need to reenter in it again
                    Toast.makeText( activity, "Successful reordering", Toast.LENGTH_SHORT ).show();
                }
                return true;
            case R.id.termsandconditions_menuitem:
                adminTermsAndConditions_AlertDialog();
                return true;
            default:
                //Log.i("Youssef", "menu item id is " + item.getItemId() );
                return defautReturnValue;
        }
    }

    private void adminTermsAndConditions_AlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder( activity );
        builder.setTitle("Terms And Conditions");
        // Set up the TextView
        final TextView output = new TextView( activity );
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
                "\n" +
                "6) You may not put any kind of music in the app." +
                "\n" +
                "7) You may not directly link to anything that infringes the above restrictions." +
                "\n\n" +
                "For any question or technical assistance, please contact the developer +961/70/853721");
        final ScrollView scrollView = new ScrollView( activity );
        scrollView.arrowScroll(View.SCROLL_AXIS_VERTICAL);
        scrollView.addView( output );
        builder.setView(scrollView);
        // Set up the buttons
        builder.setPositiveButton("Ok", null);
        builder.show();
    }

    void setFirstOptionsMenuIcon() { //it's because of the visibility thing, I  had to call it from ChooseMenuIconFragment as well
        Drawable first_icon = activity.navMenu.getItem(0).getIcon();
        if( first_icon != null ) {
            Log.i("Youssef", "inside setFirstOptionsMenuIcon : icon is not null");
            activity.toolbar.getMenu().getItem(0).setVisible( true );
            activity.toolbar.getMenu().getItem(0).setIcon( first_icon );
        } else {
            Log.i("Youssef", "inside setFirstOptionsMenuIcon : icon is null");
            activity.toolbar.getMenu().getItem(0).setVisible( false ); //probably better than toolbar.getMenu().getItem(0).setIcon( 0 );
        }
    }

}
