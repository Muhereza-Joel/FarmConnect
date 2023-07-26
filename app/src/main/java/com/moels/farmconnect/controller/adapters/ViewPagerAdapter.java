package com.moels.farmconnect.controller.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.moels.farmconnect.view.fragments.ChatListFragment;
import com.moels.farmconnect.view.fragments.ZonesListFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();
    private OnPageChangeListener onPageChangeListener;
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatListFragment();
            case 1:
                return new ZonesListFragment();
            case 2:
                return new ChatListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = fragmentTitleList.get(position);
        String capitalizedTitle = capitalizeWords(title);
        return capitalizedTitle;
    }

    private String capitalizeWords(String text) {
        String[] words = text.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                String capitalizedWord = Character.toLowerCase(word.charAt(0)) + word.substring(1);
                sb.append(capitalizedWord).append(" ");
            }
        }
        return sb.toString().trim();
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    public interface OnPageChangeListener {
        void onPageSelected(int position);
    }
    
}
