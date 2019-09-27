package ru.sigmadigital.recipes.fragment;

import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.activity.MainActivity;

@SuppressLint("ValidFragment")
public abstract class BaseFragment extends Fragment {

    protected void loadFragment(Fragment fragment, String title, boolean stack) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        if (fragmentManager != null) {
            fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(getFragmentContainer(),
                    fragment);
            if (stack) fragmentTransaction.addToBackStack(title);
            fragmentTransaction.commit();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getToggle().setDrawerIndicatorEnabled(false);
            ((MainActivity) getActivity()).getBtnBackLayout().setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).setActionButtonImage(getResources().getDrawable(R.drawable.logo_gray));
            ((MainActivity) getActivity()).setActionButtonClickListener(null);
        }
    }

    protected abstract int getFragmentContainer();

}
