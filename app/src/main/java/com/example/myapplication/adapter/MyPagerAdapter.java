package com.example.myapplication.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapplication.fragment.FragmentListMyThreads;
import com.example.myapplication.fragment.FragmentListThreads;

public class MyPagerAdapter extends FragmentPagerAdapter {
   private final Fragment[] fragments = {new FragmentListThreads(), new FragmentListMyThreads()};
   private final String[] tabTitles = {"Opinions", "My Opinions"};

   public MyPagerAdapter(FragmentManager fragmentManager) {
      super(fragmentManager);
   }

   @Override
   public Fragment getItem(int position) {
      return fragments[position];
   }

   @Override
   public int getCount() {
      return fragments.length;
   }

   @Override
   public CharSequence getPageTitle(int position) {
      return tabTitles[position];
   }
}