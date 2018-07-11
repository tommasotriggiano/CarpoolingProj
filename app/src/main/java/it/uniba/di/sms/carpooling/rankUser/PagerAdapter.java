package it.uniba.di.sms.carpooling.rankUser;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by loiodice on 11/07/2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter{

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
               HistoricalPointFragment tab1 = new HistoricalPointFragment();
                return tab1;
            case 1:
                RankOtherUsersFragment tab2 = new RankOtherUsersFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
