package ru.sigmadigital.recipes.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.activity.MainActivity;
import ru.sigmadigital.recipes.adapter.PreparedMenuAdapter;
import ru.sigmadigital.recipes.model.realmObjects.PreparedMenuRealm;
import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.util.SettingHelper;


public class PreparedMenuFragment/*<T>*/ extends BaseFragment implements PreparedMenuAdapter.OnRecipeItemClick, View.OnClickListener {

    private PreparedMenuAdapter adapter;

    private ImageView buttonAddMenu;
    private ImageView buttonEditMenu;
    private LinearLayout buttonsLayout;


    public static PreparedMenuFragment newInstance() {
        return new PreparedMenuFragment();
    }

    public static PreparedMenuFragment newInstance(RecipeResponse recipeFromChoose) {
        PreparedMenuFragment fragment = new PreparedMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("recipe", recipeFromChoose);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("recipe")) {
            RecipeResponse recipeFromChoose = (RecipeResponse) getArguments().getSerializable("recipe");
        }

        if (savedInstanceState != null && savedInstanceState.containsKey("recipePosition")) {
            int currCheckedPosition = savedInstanceState.getInt("recipePosition");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.fragment_prepared_menu, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        RecyclerView recyclerView = view.findViewById(R.id.recicler_view);
        buttonAddMenu = view.findViewById(R.id.button_add_menu);
        buttonEditMenu = view.findViewById(R.id.button_edit_menu);
        buttonsLayout = view.findViewById(R.id.buttons_layout);
        Button buttonChange = view.findViewById(R.id.change_button);
        Button buttonDelete = view.findViewById(R.id.delete_button);
        TextView tvEmptyMenu = view.findViewById(R.id.empty_prepared_menu);
        TextView tvDate = view.findViewById(R.id.date);


        List<RecipeResponse> recipesList = SettingHelper.getPreparedRecipesList();
        if (recipesList != null && recipesList.size() > 0) {

            adapter = new PreparedMenuAdapter(this, makeGenericList(recipesList));
            recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
            recyclerView.setAdapter(adapter);

            String date = getTimeFromPrepMenu(App.getRealm().where(PreparedMenuRealm.class).equalTo("id", 10).findFirst());
            tvDate.setText(date);

            buttonAddMenu.setOnClickListener(this);
            buttonEditMenu.setOnClickListener(this);

            buttonChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter.isOneRecipeChecked()) {
                        loadFragment(CategoryFragment.newInstance(adapter.getCheckedRecipe()), adapter.getCheckedRecipe().getCategory().getName(), true);
                    } else {
                        Toast.makeText(getActivity(), "Необходимо выбрать рецепт ", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter.isOneRecipeChecked() && adapter.getCheckedRecipePosition() != -1) {
                        adapter.deleteRecipe(adapter.getCheckedRecipePosition());

                    } else {
                        Toast.makeText(getActivity(), "Необходимо выбрать рецепт ", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            buttonAddMenu.setVisibility(View.GONE);
            buttonEditMenu.setVisibility(View.GONE);
            tvEmptyMenu.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initActionButtons();
    }

    private void initActionButtons() {
        if (getActivity() != null && getActivity() instanceof MainActivity && SettingHelper.getPreparedRecipesList() != null && SettingHelper.getPreparedRecipesList().size() > 0) {
            ((MainActivity) getActivity()).hideAvatar(true);

            ((MainActivity) getActivity()).getToggle().setDrawerIndicatorEnabled(false);
            ((MainActivity) getActivity()).getBtnBackLayout().setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).getActionButton().setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).setActionButtonImage(getResources().getDrawable(R.drawable.ic_trash));
            ((MainActivity) getActivity()).setActionButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getFragmentManager() != null) {
                        DeleteDialogFragment.newInstance("Удалить Готовое меню?", new DeleteDialogFragment.OnActionClickListener() {
                            @Override
                            public void onActionClick(int action) {
                                if (action == DeleteDialogFragment.PRESS_OK) {
                                    SettingHelper.clearPreparedRecipes();
                                    if (getFragmentManager() != null) {
                                        getFragmentManager().popBackStack();
                                    }
                                    loadFragment(PreparedMenuFragment.newInstance(), getResources().getString(R.string.prepared_menu), true);
                                }
                            }
                        }).show(getFragmentManager(), "dialog");
                    }


                }
            });

            /*((MainActivity) getActivity()).getSecondActionButtonLayout().setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).setSecondActionButtonImage(getResources().getDrawable(R.drawable.ic_edit));
            ((MainActivity) getActivity()).setSecondActionButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter != null && adapter.getItemCount() > 0) {
                        if (adapter.getEditableState()) {
                            adapter.setEditableState(false);
                            adapter.notifyDataSetChanged();
                            buttonsLayout.setVisibility(View.GONE);
                            buttonAddMenu.setVisibility(View.VISIBLE);
                        } else {
                            adapter.setEditableState(true);
                            adapter.notifyDataSetChanged();
                            buttonsLayout.setVisibility(View.VISIBLE);
                            buttonAddMenu.setVisibility(View.GONE);
                        }
                    }
                }
            });*/
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getSecondActionButtonLayout().setVisibility(View.GONE);
        }
    }


    @Override
    public void onRecipeClick(RecipeResponse recipe) {
        loadFragment(RecipeFragment.newInstance(recipe), recipe.getName(), true);
    }

    @Override
    public void onRecipeForChangeClick() {

        editMode(true);

       /* buttonsLayout.setVisibility(View.VISIBLE);
        buttonAddMenu.setVisibility(View.GONE);
        jump(56);*/
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_menu: {
                if (App.getRealm() != null) {
                    PreparedMenuRealm preparedMenuRealm = App.getRealm().where(PreparedMenuRealm.class).equalTo("id", 10).findFirst();

                    PreparedMenuRealm preparedMenuForSave = new PreparedMenuRealm();
                    if (preparedMenuRealm != null) {
                        preparedMenuForSave.setRecipes(preparedMenuRealm.getRecipes());
                    }
                    long idAndTime = System.currentTimeMillis();
                    preparedMenuForSave.setId(idAndTime);
                    preparedMenuForSave.setDate(idAndTime);
                    SettingHelper.addToArchiveMenu(preparedMenuForSave);
                    if (getFragmentManager() != null) {
                        InfoDialogFragment.newInstance(getString(R.string.add_to_archive)).show(getFragmentManager(), "dialog");
                    }
                }
                break;
            }
            case R.id.button_edit_menu: {
                if (adapter != null && adapter.getItemCount() > 0) {
                    if (adapter.getEditableState()) {
                        adapter.setEditableState(false);
                        adapter.notifyDataSetChanged();

                        editMode(false);

                        /*buttonsLayout.setVisibility(View.GONE);
                        buttonAddMenu.setVisibility(View.VISIBLE);

                        jump(0);*/
                    } else {
                        adapter.setEditableState(true);
                        adapter.notifyDataSetChanged();

                        editMode(true);

                        /*buttonsLayout.setVisibility(View.VISIBLE);
                        buttonAddMenu.setVisibility(View.GONE);

                        jump(56);*/
                    }
                }
                break;
            }
        }
    }


    private void editMode(boolean isEditMode) {
        if (isEditMode) {
            buttonsLayout.setVisibility(View.VISIBLE);
            buttonAddMenu.setVisibility(View.GONE);
            buttonEditMenu.setVisibility(View.GONE);
        } else {
            buttonsLayout.setVisibility(View.GONE);
            buttonAddMenu.setVisibility(View.VISIBLE);
            buttonEditMenu.setVisibility(View.VISIBLE);
        }
    }

    private void jump(int bottom) {
        float dp = getContext().getResources().getDisplayMetrics().density;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (50 * dp), (int) (50 * dp));
        layoutParams.setMargins((int) (24 * dp), 0, 0, (int) ((24 + bottom) * dp));
        layoutParams.gravity = Gravity.START | Gravity.BOTTOM;
        buttonEditMenu.setLayoutParams(layoutParams);
    }


    private List makeGenericList(List<RecipeResponse> recipesList) {
        List list = new ArrayList<>();
        if (recipesList != null && recipesList.size() > 0) {
            for (RecipeResponse recipe : recipesList) {
                if (list.size() == 0) {
                    list.add(recipe.getGlobalCategory());
                }
                if (recipe.getGlobalCategory().getId() != recipe.getCategory().getId()) {
                    list.add(recipe.getCategory());
                }
                list.add(recipe);
            }
        }
        return list;
    }

    private String getTimeFromPrepMenu(PreparedMenuRealm prepMenu) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(new Date(prepMenu.getDate()));
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("recipePosition", adapter.getCheckedRecipePosition());
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content_main;
    }


    public boolean isEditMode() {
        if (adapter != null && adapter.getItemCount() > 0) {
            if (adapter.getEditableState()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void exitEditMode() {
        if (adapter != null && adapter.getItemCount() > 0) {
            if (adapter.getEditableState()) {
                adapter.setEditableState(false);
                adapter.notifyDataSetChanged();
                editMode(false);
            }
        }
    }
}
