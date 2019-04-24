package com.AGDevelopment.uni.PagerAdapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.AGDevelopment.uni.FeedFragment;
import com.AGDevelopment.uni.ForumsFragment;
import com.AGDevelopment.uni.NotifyFragment;
import com.AGDevelopment.uni.PeopleFragment;
import com.AGDevelopment.uni.UniversitiesFragment;

public class SearchPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SearchPagerAdapter(FragmentManager fm, Context mContext) {
        super(fm);
        this.mContext = mContext;
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            return new PeopleFragment();
        } else {
            return new UniversitiesFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "People";
            case 1:
                return "Universities";
            default:
                return null;
        }
    }
}
