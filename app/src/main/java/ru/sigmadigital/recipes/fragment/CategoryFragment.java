package ru.sigmadigital.recipes.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.activity.ChangeRecipeActivity;
import ru.sigmadigital.recipes.activity.MainActivity;
import ru.sigmadigital.recipes.adapter.RecipesAdapter;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.asynctasks.RecipesTask;
import ru.sigmadigital.recipes.model.response.CategoryResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.util.PicassoUtil;
import ru.sigmadigital.recipes.util.SettingHelper;


public class CategoryFragment extends BaseFragment implements View.OnClickListener, RecipesAdapter.OnRecipeItemClick {

    private CategoryResponse category;
    private List<RecipeResponse> recipes;
    private RecipeResponse recipeForChange;

    private RecipesTask recipesTask;

    private RecyclerView recyclerView;
    private ProgressBar loading;
    private TextView errorText;
    private TextView emptyListText;
    private Button autoSelect;
    private ImageView newRecipe;


    public static CategoryFragment newInstance(CategoryResponse category) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    public static CategoryFragment newInstance(RecipeResponse recipe) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("recipe", recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("category")) {
            category = (CategoryResponse) getArguments().getSerializable("category");
        }

        if (getArguments() != null && getArguments().containsKey("recipe")) {
            recipeForChange = (RecipeResponse) getArguments().getSerializable("recipe");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_category, container, false);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        recyclerView = v.findViewById(R.id.recicler_view);
        LinearLayoutManager manager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(manager);
        loading = v.findViewById(R.id.loading);
        errorText = v.findViewById(R.id.error_text);
        emptyListText = v.findViewById(R.id.emptylist_text);
        autoSelect = v.findViewById(R.id.auto_select);
        newRecipe = v.findViewById(R.id.new_recipe);

        if (category == null) {
            category = recipeForChange.getCategory();
        }

        ImageView image = v.findViewById(R.id.image);
        PicassoUtil.getPicasso()
                .load(Network.BASE_URL + "api/category/" + category.getId() + "/photo")
                .error(R.drawable.defoult_recipe_full)
                .into(image);
        getRecipes(category);

        autoSelect.setOnClickListener(this);
        newRecipe.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getActionButton().setVisibility(View.GONE);
            ((MainActivity) getActivity()).hideAvatar(false);
        }

        if (category != null) {
            getRecipes(category);
        }
        if (recipeForChange != null) {
            goneButtons();
            getRecipes(recipeForChange.getCategory());
        }
    }

    private void getRecipes(CategoryResponse category) {
        recipesTask = new RecipesTask(category.getId(), new RecipesTask.OnGetRecipes() {
            @Override
            public void onGetRecipes(List<RecipeResponse> recipeResponses) {

                recipes = recipeResponses;

                if (recipes == null) {
                    loading.setVisibility(View.GONE);
                    errorText.setVisibility(View.VISIBLE);
                    return;
                }

                if (recipes.size() > 0)
                    initRecyclerAdapter();
                else {
                    loading.setVisibility(View.GONE);
                    emptyListText.setVisibility(View.VISIBLE);
                }

            }
        });
        recipesTask.execute();
    }

    private void initRecyclerAdapter() {
        loading.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
        emptyListText.setVisibility(View.GONE);

        RecipesAdapter recipesAdapter = new RecipesAdapter(this, recipes);
        recyclerView.setAdapter(recipesAdapter);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (recipesTask != null)
            recipesTask.cancel(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auto_select: {
                loadFragment(ViewPagerFragment.newInstance(category), String.format("Автовыбор: %s", category.getName()), true);
                break;
            }
            case R.id.new_recipe: {
                Intent intent = new Intent(getActivity(), ChangeRecipeActivity.class);
                intent.putExtra("current_title", getResources().getString(R.string.add_recipe));
                intent.putExtra("category", category);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onRecipeClick(RecipeResponse recipe) {
        if (recipeForChange != null) {
            SettingHelper.changeRecipe(recipeForChange, recipe);
            if (getFragmentManager() != null)
                getFragmentManager().popBackStack();
        } else {
            loadFragment(RecipeFragment.newInstance(recipe), recipe.getName(), true);
        }
    }


    @Override
    protected int getFragmentContainer() {
        return R.id.content_main;
    }


    private void goneButtons() {
        autoSelect.setVisibility(View.GONE);
        newRecipe.setVisibility(View.GONE);

    }
}
