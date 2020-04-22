package com.youssefdirani.navmenu_admin.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.youssefdirani.navmenu_admin.R;

public class HomeFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) { //called the first time we enter here ONLY - tested
        super.onCreate(savedInstanceState);
        Log.i("Youssef", "inside HomeFragment : onCreate");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _onAttach(context);
    }
    Activity activity = null;
    private void _onAttach(Context context) { //it gets called twice
        Log.i("Youssef", "inside HomeFragment _onAttach");
        activity = (Activity) context;
    }

    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) { //we enter this between onCreate and onStart - tested
        //container is nav_host_fragment and not mobile_navigation. Tested

        Bundle args = getArguments();
        if( args == null ) {
            Log.i("Youssef", "inside HomeFragment : no arguments");
        } else {
            int idOfNewMenuItem = getArguments().getInt("id");
            String title = getArguments().getString("title");
            Log.i("Youssef", "inside HomeFragment : id of the menu item is " + idOfNewMenuItem +
                    " and title of the menu item is " + title );
            //I don't care about the id. The title determines which database table to fetch
        }

        root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        textView.setText("Home");

        root.findViewById(R.id.home_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_HomeFragment_to_HomeSecondFragment);
            }
        });

        //it works
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView( this.getContext() );
        tv.setLayoutParams(lparams);
        tv.setText("dynamic text view");
        LinearLayout linearLayout = root.findViewById(R.id.layout_of_fragment_home);
        linearLayout.addView(tv);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("Youssef", "inside HomeFragment : onStart");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.i("Youssef", "inside HomeFragment : onPause");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i("Youssef", "inside HomeFragment : onStop");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Youssef", "inside HomeFragment : onDestroy");
    }

}
