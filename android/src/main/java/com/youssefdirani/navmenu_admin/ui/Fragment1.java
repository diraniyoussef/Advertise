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

public class Fragment1 extends Fragment {

    //private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        Bundle args = getArguments();
        if( args == null ) {
            Log.i("Youssef", "inside SlideshowFragment : no arguments");
        } else {
            int idOfNewMenuItem = getArguments().getInt("id");
            String title = getArguments().getString("title");
            Log.i("Youssef", "inside SlideshowFragment : id of the menu item is " + idOfNewMenuItem +
                    " and title of the menu item is " + title );
        }
        textView.setText("Slideshow"); //it works
        return root;
    }
}
