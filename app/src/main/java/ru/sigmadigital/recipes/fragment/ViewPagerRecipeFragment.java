package ru.sigmadigital.recipes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.adapter.ProductsAdapter;
import ru.sigmadigital.recipes.adapter.ViewPagerAdapter;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.util.PicassoUtil;


public class ViewPagerRecipeFragment extends Fragment implements View.OnClickListener {

    private RecipeResponse recipe;
    private ViewPagerAdapter.OnRecipeButtonClick listener;


    public static ViewPagerRecipeFragment newInstance(RecipeResponse recipe) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("recipe", recipe);
        ViewPagerRecipeFragment fragment = new ViewPagerRecipeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(ViewPagerAdapter.OnRecipeButtonClick listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("recipe")) {
            recipe = (RecipeResponse) getArguments().getSerializable("recipe");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager_recipe, container, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        TextView name = view.findViewById(R.id.name);
        name.setText(recipe.getName());

        ImageView image = view.findViewById(R.id.image);
        PicassoUtil.getPicasso()
                .load(Network.BASE_URL + "api/recipe/" + recipe.getId() + "/photo")
                .placeholder(R.drawable.defoult_recipe_full)
                .error(R.drawable.defoult_recipe_full)
                .into(image);


        RecyclerView recyclerProducts = view.findViewById(R.id.products);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerProducts.setAdapter(new ProductsAdapter(recipe.getProducts()));


        view.findViewById(R.id.add).setOnClickListener(this);
        view.findViewById(R.id.delete).setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add: {
                listener.addRecipe(recipe);
                break;
            }
            case R.id.delete: {
                listener.deleteRecipe(recipe);
                break;
            }
        }
    }

}
