package com.youssefdirani.navmenu_admin;

import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;

class BottomNavOperations {
    private MainActivity activity;

    BottomNavOperations(MainActivity activity ) {
        this.activity = activity;

    }

    void setupBottomNavigation() {
        activity.bottomNavigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) { //MenuItem.OnMenuItemClickListener is any better
                Log.i("Youssef", "bottom tab " + getCheckedItemOrder() + " is clicked.");
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

    int getCheckedItemOrder() {
        for( int i = 0; i < activity.bottomMenu.size(); i++ ) {
            if( activity.bottomMenu.getItem(i).isChecked() ) {
                return i;
            }
        }
        return -1;
    }

}
