package ru.sigmadigital.recipes.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.activity.MainActivity;
import ru.sigmadigital.recipes.adapter.ArchiveMenuAdapter;
import ru.sigmadigital.recipes.model.realmObjects.ArchiveMenuRealm;
import ru.sigmadigital.recipes.model.realmObjects.PreparedMenuRealm;
import ru.sigmadigital.recipes.util.SettingHelper;


public class ArchiveMenuFragment extends BaseFragment {

    public static ArchiveMenuFragment newInstance() {
        return new ArchiveMenuFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_archive_menu, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        RecyclerView recyclerView = view.findViewById(R.id.rv_saved_food);
        TextView tv_emptyArch = view.findViewById(R.id.tv_empty_archive);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        if (App.getRealm() != null) {
            ArchiveMenuRealm archive = App.getRealm().where(ArchiveMenuRealm.class).findFirst();

            ArchiveMenuAdapter.OnPreparedMenuItemClicker listener = new ArchiveMenuAdapter.OnPreparedMenuItemClicker() {
                @Override
                public void onItemClick(PreparedMenuRealm currPrepMenu) {
                    SettingHelper.savePreparedRecipes(currPrepMenu);
                    loadFragment(PreparedMenuFragment.newInstance(), getString(R.string.prepared_menu), true);
                }
            };

            if (archive != null && archive.getPreparedMenus().size() > 0) {
                ArchiveMenuAdapter adapter = new ArchiveMenuAdapter(getFragmentManager(), archive.getPreparedMenus(), listener);
                recyclerView.setAdapter(adapter);
            } else {
                tv_emptyArch.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getActionButton().setVisibility(View.GONE);
            ((MainActivity) getActivity()).hideAvatar(true);
        }
    }


    @Override
    protected int getFragmentContainer() {
        return R.id.content_main;
    }
}
