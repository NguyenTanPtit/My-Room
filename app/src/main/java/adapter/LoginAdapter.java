package adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import fragment.LogintabFragment;
import fragment.SignupTabFragment;

public class LoginAdapter extends FragmentPagerAdapter {

    private Context context;
    int totalTab;

    public LoginAdapter(@NonNull FragmentManager fm, Context context, int totalTab) {
        super(fm);
        this.context = context;
        this.totalTab = totalTab;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new LogintabFragment();
        }
        return new SignupTabFragment();
    }

    @Override
    public int getCount() {
        return totalTab;
    }
}
