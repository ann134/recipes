package ru.sigmadigital.recipes.activity;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {


    protected int getFragmentContainer() {
        return 0;
    }

    protected void loadFragment(Fragment fragment, String title, boolean stack) {
        loadFragment(fragment, title, stack, getFragmentContainer());
    }

    protected void loadFragment(Fragment fragment, String title, boolean stack, int container) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.replace(container,
                fragment);
        if (stack) fragmentTransaction.addToBackStack(title);
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            Log.i("OnBackPressed", "finish");
            this.finish();
        } else {
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

}
