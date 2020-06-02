package com.youssefdirani.navmenu_admin.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.youssefdirani.navmenu_admin.MainActivity;
import com.youssefdirani.navmenu_admin.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class ChooseNavMenuIconFragment extends Fragment {
    private MainActivity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i("ChooseMenuIconFragment", "no arguments");
        View root = inflater.inflate(R.layout.fragment_menuicons, container, false);

        Bundle args = getArguments();
        if( args == null ) {
            Log.i("Youssef", "inside ChooseNavMenuIconFragment : no arguments");
        } else {
            final int indexOfNewMenuItem = getArguments().getInt("index_of_navmenuitem");//for technical reasons, I have to pass in the checked menu item now. That is because after getting out of ChooseNavMenuIconFragment class, we cannot determine the checked menu item (weird but this is what happens)

            activity = (MainActivity) getActivity();
            //activity.disappearChooseNavigationIcon_MenuItem();

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton imageButton = (ImageButton) v;
                    //Drawable icon_drawable = imageButton.getDrawable(); //R.drawable.ic_remove_red_eye_black_24dp; //R.drawable.ic_action_home
                    String tag = v.getTag().toString();
                    Log.i("ChooseMenuIconFragment", "inside onClick " + tag );
                    //int id = Integer.parseInt( tag );

                    activity.setIconOfCheckedNavMenuItem( tag, indexOfNewMenuItem);
                    activity.onBackPressed(); //better than activity.getSupportFragmentManager().popBackStack(); //https://stackoverflow.com/questions/2717954/android-simulate-back-button
                    activity.updateToolbarTitle( indexOfNewMenuItem ); //unfortunately needed.
                }
            };
            root.findViewById(R.id.imagebutton_ic_menu_camera).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_action_home).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_home_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_menu_gallery).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_menu_manage).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_menu_send).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_menu_share).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_menu_slideshow).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_perm_camera_mic_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_person_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_person_pin_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_pets_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_refresh_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_remove_red_eye_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_settings_applications_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_settings_ethernet_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_shopping_basket_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_shopping_cart_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_touch_app_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_vignette_black_24dp).setOnClickListener(onClickListener);
            root.findViewById(R.id.imagebutton_ic_weekend_black_24dp).setOnClickListener(onClickListener);
        }
        return root;
    }

    @Override
    public void onResume() {
        Log.i("ChooseMenuIconFragment", "inside onResume");
        super.onResume();
        //activity.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("ChooseMenuIconFragment", "inside onPause");
        //MUST RETURN SOMETHING USING onActivityResult(...); that is for technical reasons.
        //activity.onActivityResult( activity.CHOOSE_MENUICON_REQUESTCODE, RESULT_CANCELED,null );
        //activity.appearChooseNavigationIcon_MenuItem();
    }
}
