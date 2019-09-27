package ru.sigmadigital.recipes.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.activity.ChangeRecipeActivity;
import ru.sigmadigital.recipes.activity.MainActivity;
import ru.sigmadigital.recipes.adapter.ProductsAdapter;
import ru.sigmadigital.recipes.adapter.StepsAdapter;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.asynctasks.RecipeTask;
import ru.sigmadigital.recipes.model.response.ProductResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.model.response.StepResponse;
import ru.sigmadigital.recipes.util.PicassoUtil;


public class RecipeFragment extends BaseFragment implements View.OnClickListener {

    private RecipeResponse recipe;
    private ImageView image;

    private RecyclerView recyclerSteps;
    private RecyclerView recyclerProducts;

    private final int DELETE_RECIPE = 22;

    private RecipeTask recipeTask;


    public static RecipeFragment newInstance(RecipeResponse recipe) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("recipe", recipe);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("recipe")) {
            recipe = (RecipeResponse) getArguments().getSerializable("recipe");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_recipe, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        image = view.findViewById(R.id.image);
        view.findViewById(R.id.imv_btn_share).setOnClickListener(this);

        recyclerSteps = view.findViewById(R.id.steps);
        recyclerSteps.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerSteps.setAdapter(new StepsAdapter(recipe.getSteps()));

        recyclerProducts = view.findViewById(R.id.products);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerProducts.setAdapter(new ProductsAdapter(recipe.getProducts()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getActionButton().setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).hideAvatar(true);

            ((MainActivity) getActivity()).setActionButtonImage(getResources().getDrawable(R.drawable.ic_edit));
            ((MainActivity) getActivity()).setActionButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ChangeRecipeActivity.class);
                    intent.putExtra("current_title", getResources().getString(R.string.change_recipe));
                    intent.putExtra("recipe", recipe);
                    startActivityForResult(intent, DELETE_RECIPE);
                }
            });
        }

        loadRecipe();
    }

    private void loadRecipe() {
        recipeTask = new RecipeTask(recipe.getId(), new RecipeTask.OnGetRecipe() {
            @Override
            public void onGetRecipe(RecipeResponse recipeResponse) {
                if (recipeResponse != null) {
                    recipe = recipeResponse;
                    if (getActivity() != null) {
                        getActivity().setTitle((recipe != null && recipe.getName() != null) ? recipe.getName() : "");
                    }
                    recyclerSteps.setAdapter(new StepsAdapter(recipe.getSteps()));
                    recyclerProducts.setAdapter(new ProductsAdapter(recipe.getProducts()));
                }
            }
        });
        recipeTask.execute();

        PicassoUtil.getPicasso()
                .load(Network.BASE_URL + "api/recipe/" + recipe.getId() + "/photo")
                .placeholder(R.drawable.defoult_recipe_full)
                .error(R.drawable.defoult_recipe_full)
                .into(image);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recipeTask != null) {
            recipeTask.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {
        String recipeString;
        StringBuilder builder = new StringBuilder();
        builder.append(recipe.getName()).append("\n");
        builder.append(getString(R.string.ingredients_products)).append("\n");

        for (ProductResponse product : recipe.getProducts()) {
            builder.append(product.getName()).append(" - ").append(product.getCount()).append(" ").append(product.getUnits().getName()).append("\n");
        }
        builder.append(getString(R.string.recipe_steps)).append("\n");
        for (StepResponse step : recipe.getSteps()) {
            builder.append((step.getPosition() + 1)).append(". ").append(step.getAction()).append("\n");
        }
        recipeString = builder.toString();


        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, recipeString);
        try {
            startActivity(Intent.createChooser(intent, "action"));
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public RecipeResponse getRecipe() {
        return recipe;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DELETE_RECIPE) {
            if (resultCode == Activity.RESULT_OK) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        }
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content_main;
    }
}
