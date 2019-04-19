package com.urnikium.lukak.umu.Adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.urnikium.lukak.umu.Views.Tab_SelectCourse;
import com.urnikium.lukak.umu.Views.Tab_SelectProf;
import com.urnikium.lukak.umu.Views.Tab_SelectProgramme;

public class Adapter_SelectionPager extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public Adapter_SelectionPager(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Tab_SelectProgramme tab1 = new Tab_SelectProgramme();
                return tab1;

            case 1:
                Tab_SelectProf tab12 = new Tab_SelectProf();
                return tab12;

            case 2:
                Tab_SelectCourse tab3 = new Tab_SelectCourse();
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