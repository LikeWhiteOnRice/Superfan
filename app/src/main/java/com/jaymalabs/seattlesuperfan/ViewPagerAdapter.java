package com.jaymalabs.seattlesuperfan;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by E134292 on 3/24/2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MainFragment tab1 = new MainFragment();
                return tab1;
            case 1:
                ScheduleFragment tab2 = new ScheduleFragment();
                return tab2;
            case 2:
                RosterFragment tab3 = new RosterFragment();
                return tab3;
            case 3:
                GamedayFragment tab4 = new GamedayFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
