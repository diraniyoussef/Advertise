package com.youssefdirani.navmenu_admin.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.youssefdirani.navmenu_admin.MainActivity;
import com.youssefdirani.navmenu_admin.R;

public class HomeFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) { //called the first time we enter here ONLY - tested
        super.onCreate(savedInstanceState);
        Log.i("Youssef", "inside HomeFragment : onCreate");
    }
    private MainActivity activity;
    /*
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

    private void _onAttach(Context context) { //it gets called twice
        Log.i("Youssef", "inside HomeFragment _onAttach");
        activity = (Activity) context;
    }
    */
    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) { //we enter this between onCreate and onStart - tested
        //container is nav_host_fragment and not mobile_navigation. Tested
        activity = (MainActivity) getActivity();

        int navIndex;
        int bottombarIndex;
        String tableName;
        Bundle args = getArguments();

        if( args == null ) {
            tableName = "Welcome and thank you for using this app.\n" +
                    "Please wait while we fetch data from the server.";
            Log.i("Youssef", "inside HomeFragment : no arguments");
        } else {
            //this is set usually inside this method navigateToMenuItem
            bottombarIndex = getArguments().getInt("bottombar_order"); //this will actually be 0 if the bottom bar was hidden
            navIndex = getArguments().getInt("nav_order");
            tableName = navIndex + "_" + bottombarIndex;


            Log.i("Youssef", "inside HomeFragment : table name is " + tableName );
            //I don't care about the id. The title determines which database table to fetch

        }

        root = inflater.inflate(R.layout.fragment_home, container, false);


        /*
        final TextView textView = root.findViewById(R.id.text_home);
        textView.setText( navTitle );
         */
        /*
        //it works
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView( this.getContext() );
        tv.setLayoutParams(lparams);
        tv.setText("dynamic text view");
        LinearLayout linearLayout = root.findViewById(R.id.layout_of_fragment_home);
        linearLayout.addView(tv);
        */
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("Youssef", "inside HomeFragment : onStart");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i("Youssef", "inside HomeFragment : onResume");
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
