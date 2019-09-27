package ru.sigmadigital.recipes.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.util.PicassoUtil;


public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeItemHolder> {


    private OnRecipeItemClick listener;
    private List<RecipeResponse> recipes;


    public interface OnRecipeItemClick {
        void onRecipeClick(RecipeResponse recipe);
    }

    public RecipesAdapter(OnRecipeItemClick listener, List<RecipeResponse> recipes) {
        this.listener = listener;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeItemHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recipe, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new RecipeItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeItemHolder holder, final int i) {
        final RecipeResponse recipe = recipes.get(i);

        PicassoUtil.getPicasso()
                .load(Network.BASE_URL + "api/recipe/" + recipe.getId() + "/photo")
                .placeholder(R.drawable.defoult_recipe_preview)
                .error(R.drawable.defoult_recipe_preview)
                .into(holder.image);

        holder.name.setText(recipe.getName());
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < recipes.get(i).getProducts().size(); n++) {
            sb.append(recipes.get(i).getProducts().get(n).getName()).append(", ");
        }
        if (sb.length() >= 2)
            sb.delete(sb.length() - 2, sb.length() - 1);

        holder.description.setText(sb.toString());


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }


    class RecipeItemHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView image;
        private TextView name;
        private TextView description;

        private RecipeItemHolder(@NonNull View v) {
            super(v);

            cardView = v.findViewById(R.id.card_view);
            image = v.findViewById(R.id.image_recipe);
            name = v.findViewById(R.id.name);
            description = v.findViewById(R.id.description);
        }
    }

}
