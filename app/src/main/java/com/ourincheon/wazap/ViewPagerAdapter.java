package com.ourincheon.wazap;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Youngdo on 2016-02-02.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter
{
    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();
    private DataStorage storage = new DataStorage();

    public ViewPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title)
    {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position)
    {
        return FragmentPage.newInstance(position);
    }

    @Override
    public int getCount()
    {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {

        return mFragmentTitles.get(position);
    }


}
