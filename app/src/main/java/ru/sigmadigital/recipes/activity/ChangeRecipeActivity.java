package ru.sigmadigital.recipes.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.fragment.ChangeRecipeFragment;
import ru.sigmadigital.recipes.model.response.CategoryResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;


public class ChangeRecipeActivity extends BaseActivity {

    private ImageView imvActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_recipe);

        initActionBar();


        if (getIntent().hasExtra("recipe")) {
            RecipeResponse recipe = (RecipeResponse) getIntent().getSerializableExtra("recipe");
            Log.d("CHANGE ACTIVITY recipe", recipe.getName());

            loadFragment(ChangeRecipeFragment.newInstance(recipe), getResources().getString(R.string.change_recipe), false);
            setTitle(getResources().getString(R.string.change_recipe));
        }

        if(getIntent().hasExtra("category")) {
            CategoryResponse category = (CategoryResponse) getIntent().getSerializableExtra("category");
            Log.d("CHANGE ACTIVITY categor", category.getName());

            loadFragment(ChangeRecipeFragment.newInstance(category), getResources().getString(R.string.add_recipe), false);
            setTitle(getResources().getString(R.string.add_recipe));
        }

    }

    private void initActionBar() {
        Toolbar toolbar = findViewById(R.id.my_action_bar);
        imvActionButton = findViewById(R.id.imv_action_btn);
        LinearLayout btnBackLayout = findViewById(R.id.button_back_layout);
        setSupportActionBar(toolbar);
        btnBackLayout.setVisibility(View.VISIBLE);
        btnBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(titleId);
    }

    @Override
    public void setTitle(CharSequence titleString) {
        super.setTitle(titleString);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(titleString);
    }


    public void setActionButtonImage(Drawable drawable) {
        imvActionButton.setImageDrawable(drawable);
    }

    public void setActionButtonClickListener(View.OnClickListener listener) {
        imvActionButton.setOnClickListener(listener);
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.fragment_container;
    }

}
