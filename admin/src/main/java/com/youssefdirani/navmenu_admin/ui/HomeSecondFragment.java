package com.youssefdirani.navmenu_admin.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youssefdirani.navmenu_admin.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class HomeSecondFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
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
        textView.setText("I'm HomeSecondFragment"); //it works
        return root;
    }
}
