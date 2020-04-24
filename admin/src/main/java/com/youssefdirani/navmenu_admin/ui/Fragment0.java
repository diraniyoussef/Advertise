package com.youssefdirani.navmenu_admin.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.youssefdirani.navmenu_admin.R;

public class Fragment0 extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) { //called the first time we enter here ONLY (because they're added to the backstack) - tested
        super.onCreate(savedInstanceState);
        Log.i("Youssef", "inside Fragment0 : onCreate");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if( args == null ) {
            Log.i("Youssef", "inside Fragment0 : no arguments");
        } else {
            int idOfNewMenuItem = getArguments().getInt("id");
            String title = getArguments().getString("title");
            Log.i("Youssef", "inside Fragment0 : id of the menu item is " + idOfNewMenuItem +
                    " and title of the menu item is " + title );
        }

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);
        //textView.setText("Gallery");
        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.i("Youssef", "inside Fragment0 : onStart");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i("Youssef", "inside Fragment0 : onResume");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.i("Youssef", "inside Fragment0 : onPause");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i("Youssef", "inside Fragment0 : onStop");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Youssef", "inside Fragment0 : onDestroy");
    }
}
