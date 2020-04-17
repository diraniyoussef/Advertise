package com.youssefdirani.navmenu_admin.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youssefdirani.navmenu_admin.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ActiveFragment extends Fragment {
    public View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
/*
        Bundle bundle = getIntent().getExtras();
        if( bundle == null ) {
            finish();
            return;
        }
 */
        Log.i("Youssef", "inside ActiveFragment : no arguments");
        Bundle args = getArguments();
        if( args == null ) {
            Log.i("Youssef", "inside ActiveFragment : no arguments");
        } else {
            int idOfNewMenuItem = getArguments().getInt("id");
            Log.i("Youssef", "inside ActiveFragment : id of the menu item is " + idOfNewMenuItem);
        }
        //ConfirmationFragmentArgs.fromBundle();

        root = inflater.inflate(R.layout.fragment_active, container, false);
        final TextView textView = root.findViewById( R.id.text_active );
        /*
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);

        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        textView.setText("I'm an active fragment"); //it works
        //it is up to this method to access the database
        return root;
    }

    @Override
    public void onResume() {



        Log.i("Youssef", "inside ActiveFragment onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("Youssef", "inside ActiveFragment onPause");
        super.onPause();
    }
}
