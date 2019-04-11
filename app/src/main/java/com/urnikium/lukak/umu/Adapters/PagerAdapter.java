package com.urnikium.lukak.umu.Adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.urnikium.lukak.umu.Views.tab_predm;
import com.urnikium.lukak.umu.Views.tab_prof;
import com.urnikium.lukak.umu.Views.tab_program;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                tab_program tab1 = new tab_program();
                return tab1;

            case 1:
                tab_prof tab12 = new tab_prof();
                return tab12;

            case 2:
                tab_predm tab3 = new tab_predm();
                return tab3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}