package ru.ramich.musiclist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FragmentPager extends FragmentStatePagerAdapter {

    public FragmentPager(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SongsFragment();
            case 1:
                return new ArtistsFragment();
            default:
                return null;
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Songs";
                break;
            case 1:
                title = "Artists";
                break;
        }
        return title;
    }


    @Override
    public int getCount() {
        return 2;
    }
}
