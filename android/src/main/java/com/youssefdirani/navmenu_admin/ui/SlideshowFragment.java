package com.youssefdirani.navmenu_admin.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.youssefdirani.navmenu_admin.R;

public class SlideshowFragment extends Fragment {

    //private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
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
