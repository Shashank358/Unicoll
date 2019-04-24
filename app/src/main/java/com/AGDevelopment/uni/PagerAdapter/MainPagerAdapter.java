package com.AGDevelopment.uni.PagerAdapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.AGDevelopment.uni.FeedFragment;
import com.AGDevelopment.uni.ForumsFragment;
import com.AGDevelopment.uni.NotifyFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public MainPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;

    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            return new FeedFragment();
        } else if (i == 1){
            return new ForumsFragment();
        } else{
            return new NotifyFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "Feed";
            case 1:
                return "Forums";
            case 2:
                return "Notify";
            default:
                return null;
        }
    }
}
