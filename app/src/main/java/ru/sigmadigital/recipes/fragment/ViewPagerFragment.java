package ru.sigmadigital.recipes.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.adapter.ViewPagerAdapter;
import ru.sigmadigital.recipes.api.asynctasks.RecipesTask;
import ru.sigmadigital.recipes.model.realmObjects.UsedRecipes;
import ru.sigmadigital.recipes.model.response.CategoryResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.util.SettingHelper;


public class ViewPagerFragment extends BaseFragment implements ViewPagerAdapter.OnRecipeButtonClick {


    private CategoryResponse currentCategory;

    private ViewPagerAdapter adapter;
    private ViewPager viewPager;
    private LinearLayout indicatorContainer;
    private ProgressBar progressBar;
    private TextView emptyList;


    private List<RecipeResponse> recipeResponseList;
    private List<RecipeResponse> recipesForPreparedMenu;

    private RecipesTask recipesTask;


    public static ViewPagerFragment newInstance(CategoryResponse category) {

        ViewPagerFragment fragment = new ViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("category", category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable("category") != null) {
            currentCategory = (CategoryResponse) getArguments().getSerializable("category");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_view_pager, null);

        viewPager = view.findViewById(R.id.view_pager);
        indicatorContainer = view.findViewById(R.id.indicator_container);
        progressBar = view.findViewById(R.id.loading);
        emptyList = view.findViewById(R.id.tv_empty_list);

        recipesForPreparedMenu = new ArrayList<>();
        recipeResponseList = new ArrayList<>();
        recipesTask = new RecipesTask(currentCategory.getId(), new RecipesTask.OnGetRecipes() {
            @Override
            public void onGetRecipes(List<RecipeResponse> recipes) {
                if (recipes != null) {
                    recipeResponseList = recipes;
                    initViewPager(recipes);
                }
            }
        });
        recipesTask.execute();

        if (savedInstanceState != null) {
            int recipePosition = savedInstanceState.getInt("recipePosition", 0);
            viewPager.setCurrentItem(recipePosition);
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                updateIndicatorImages();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        return view;
    }

    private void initViewPager(List<RecipeResponse> list) {
        List<Long> categoryList = new ArrayList<>();
        List<RecipeResponse> randomRecipes = new ArrayList<>();

        for (RecipeResponse recipe : list) {
            if (!categoryList.contains(recipe.getCategory().getId())) {
                categoryList.add(recipe.getCategory().getId());
            }
        }

        if (categoryList.contains(114l) && categoryList.contains(115l) && categoryList.contains(116l)) {
            categoryList.remove(114l);
            categoryList.remove(115l);
            categoryList.remove(116l);
            int n = new Random().nextInt(2);
            switch (n) {
                case 0:
                    categoryList.add(114l);
                    break;
                case 1:
                    categoryList.add(115l);
                    break;
                case 2:
                    categoryList.add(116l);
                    break;
            }
        }

        if (categoryList.contains(223l) && categoryList.contains(224l) && categoryList.contains(225l)) {
            categoryList.remove(223l);
            categoryList.remove(224l);
            categoryList.remove(225l);
            int n = new Random().nextInt(2);
            switch (n) {
                case 0:
                    categoryList.add(223l);
                    break;
                case 1:
                    categoryList.add(224l);
                    break;
                case 2:
                    categoryList.add(225l);
                    break;
            }
        }

        if (categoryList.contains(553l) && categoryList.contains(554l) && categoryList.contains(555l)) {
            categoryList.remove(553l);
            categoryList.remove(554l);
            categoryList.remove(555l);
            int n = new Random().nextInt(2);
            switch (n) {
                case 0:
                    categoryList.add(553l);
                    break;
                case 1:
                    categoryList.add(554l);
                    break;
                case 2:
                    categoryList.add(555l);
                    break;
            }
        }

        int categorySize = categoryList.size();

        for (int i = 0; i < categorySize; i++) {
            RecipeResponse randomRecipe = list.get(new Random().nextInt(list.size()));
            if (categoryList.contains(randomRecipe.getCategory().getId()) && !isRecipeAlreadyUsed(randomRecipe)) {
                randomRecipes.add(randomRecipe);
                categoryList.remove(randomRecipe.getCategory().getId());
            } else {
                i--;
            }
        }

        if (randomRecipes.size() < 1) {
            emptyList.setVisibility(View.VISIBLE);
        }

        adapter = new ViewPagerAdapter(getChildFragmentManager(), randomRecipes, this);
        viewPager.setAdapter(adapter);

        updateIndicatorImages();

        progressBar.setVisibility(View.GONE);
    }

    private int getRecipesCount(Long category) {
        int count = 0;
        for (RecipeResponse recipe : recipeResponseList) {
            if (recipe.getCategory().getId() == category)
                count++;
        }
        return count;
    }

    private boolean isRecipeAlreadyUsed(RecipeResponse recipe) {
        UsedRecipes ur = SettingHelper.getUsedRecipes(recipe.getCategory().getId());
        if (ur != null) {
            for (Long resId : ur.getUsedRecipes()) {
                if (recipe.getId() == resId) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recipesTask != null) {
            recipesTask.cancel(true);
        }
    }

    private void updateIndicatorImages() {
        indicatorContainer.removeAllViewsInLayout();

        for (int i = 0; i < adapter.getCount(); i++) {
            addChildIndicator();
        }

        for (int n = 0; n < indicatorContainer.getChildCount(); n++) {
            indicatorContainer.getChildAt(n).setBackgroundResource(R.drawable.indicator_bg_unselect);
        }
        if (recipeResponseList.size() > 0) {
            indicatorContainer.getChildAt(viewPager.getCurrentItem()).setBackgroundResource(R.drawable.indicator_bg_select);
        }

    }

    private void addChildIndicator() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_pager_indicator, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.view_pager_indicator_margin);
        indicatorContainer.addView(view, lp);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("recipePosition", viewPager.getCurrentItem());
    }


    @Override
    public void addRecipe(RecipeResponse recipe) {
        Toast.makeText(getActivity(), R.string.toast_add, Toast.LENGTH_SHORT).show();
        recipe.setSelected(RecipeResponse.GOOD_SELECT);
        recipesForPreparedMenu.add(recipe);

        SettingHelper.addToUsedRecipes(recipe, getRecipesCount(recipe.getCategory().getId()));

        updateOrExit();
    }

    @Override
    public void deleteRecipe(RecipeResponse recipe) {
        Toast.makeText(getActivity(), R.string.toast_add, Toast.LENGTH_SHORT).show();
        recipe.setSelected(RecipeResponse.BAD_SELECT);
        recipesForPreparedMenu.add(recipe);
        updateOrExit();
    }


    private void updateOrExit() {
        adapter.deleteRecipePage(viewPager.getCurrentItem());

        if (adapter.getCount() == 0) {
            Collections.sort(recipesForPreparedMenu, new Comparator<RecipeResponse>() {
                @Override
                public int compare(RecipeResponse o1, RecipeResponse o2) {
                    long lastDigitO1 = o1.getCategory().getId() % 10;
                    long lastDigitO2 = o2.getCategory().getId() % 10;
                    if (lastDigitO1 == lastDigitO2) {
                        return 0;
                    } else {
                        return lastDigitO1 > lastDigitO2 ? 1 : -1;
                    }
                }
            });

            SettingHelper.savePreparedRecipes(recipesForPreparedMenu);
            getFragmentManager().popBackStack();
            loadFragment(PreparedMenuFragment.newInstance(), getString(R.string.prepared_menu), true);
        } else {
            updateIndicatorImages();
        }
    }


    @Override
    protected int getFragmentContainer() {
        return R.id.content_main;
    }
}
