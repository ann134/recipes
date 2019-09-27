package ru.sigmadigital.recipes.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.fragment.ViewPagerRecipeFragment;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<RecipeResponse> recipeList;
    private OnRecipeButtonClick listener;


    public interface OnRecipeButtonClick {
        void addRecipe(RecipeResponse recipe);
        void deleteRecipe(RecipeResponse recipe);
    }


    public ViewPagerAdapter(FragmentManager fm, List<RecipeResponse> recipeList, OnRecipeButtonClick listener) {
        super(fm);
        this.recipeList = recipeList;
        this.listener = listener;
    }


    @Override
    public Fragment getItem(int i) {
        ViewPagerRecipeFragment fragment = ViewPagerRecipeFragment.newInstance(recipeList.get(i));
        fragment.setListener(listener);

        return fragment;
    }


    public void deleteRecipePage(int index){
        recipeList.remove(index);
        notifyDataSetChanged();
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }


    @Override
    public int getCount() {
        return recipeList.size();
    }

}
