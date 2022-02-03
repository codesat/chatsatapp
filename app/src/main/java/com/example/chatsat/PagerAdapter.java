package com.example.chatsat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

// it will extend to FragmentPagerAdapter and then by alt+enter
// implements methods and matching super constructor
public class PagerAdapter extends FragmentPagerAdapter {

    int tabcount; // as there are three different tab

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);

        tabcount=behavior; // from here the number of tabs will get assigned to
        // tabcount
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {


        // from here selected  number of tab will get assigned to position
        // and based on the numebr assigned to each tab
        // ist corresponding fragment will get replaced
        switch (position){
            case 0:
                return new chatfragment();

            case 1:
                return new statusfragment();

            case 2:
                return new callfragment();

            default:
                return new chatfragment();


        }


    }

    @Override
    public int getCount() {
       // return 0;
        return tabcount; // instead of 0 we ahve to return the tab count
    }

    // we need a java class to handle all these framents that when
    // scroll or click on diffreent tabs
    // then we have to change the fragment
}
