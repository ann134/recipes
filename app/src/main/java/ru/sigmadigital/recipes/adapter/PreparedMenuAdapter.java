package ru.sigmadigital.recipes.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.model.response.CategoryResponse;
import ru.sigmadigital.recipes.model.response.ProductResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.util.PicassoUtil;
import ru.sigmadigital.recipes.util.SettingHelper;


public class PreparedMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Serializable {

    private final OnRecipeItemClick listener;
    private final List list;
    private boolean editableState;
    private boolean oneRecipeChecked;
    private int checkedRecipePosition = -1;


    public interface OnRecipeItemClick {
        void onRecipeClick(RecipeResponse recipe);
        void onRecipeForChangeClick();
    }

    public PreparedMenuAdapter(OnRecipeItemClick listener, List list) {
        this.listener = listener;
        this.list = list;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int type) {
        View v;
        switch (type) {
            case ItemType.recipeCard:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recipe, null);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new RecipeItemHolder(v);
            case ItemType.subCategory:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_subtitle, null);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new CategoryItemHolder(v);
            case ItemType.mainCategory:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_title, null);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new CategoryItemHolder(v);
            default:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recipe, null);
                return new RecipeItemHolder(v);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryItemHolder) {
            final CategoryResponse item = (CategoryResponse) list.get(position);
            ((CategoryItemHolder) holder).name.setText((item).getName());
        }

        if (holder instanceof RecipeItemHolder) {
            final RecipeResponse item = (RecipeResponse) list.get(position);

            PicassoUtil.getPicasso()
                    .load(Network.BASE_URL + "api/recipe/" + item.getId() + "/photo")
                    .placeholder(R.drawable.defoult_recipe_preview)
                    .error(R.drawable.defoult_recipe_preview)
                    .into(((RecipeItemHolder) holder).recipeImage);

            if (editableState) {
                ((RecipeItemHolder) holder).checkButton.setVisibility(View.VISIBLE);
                ((RecipeItemHolder) holder).checkButton.setOnCheckedChangeListener(null);
                ((RecipeItemHolder) holder).checkButton.setChecked(false);
                ((RecipeItemHolder) holder).tvRecipeChange.setClickable(false);
            } else {
                ((RecipeItemHolder) holder).checkButton.setVisibility(View.GONE);
            }

            if (oneRecipeChecked) {
                ((RecipeItemHolder) holder).checkButton.setClickable(false);
                if(position == getCheckedRecipePosition()){
                    ((RecipeItemHolder) holder).checkButton.setChecked(true);
                    ((RecipeItemHolder) holder).checkButton.setClickable(true);
                }
            } else {
                ((RecipeItemHolder) holder).checkButton.setClickable(true);
            }

            ((RecipeItemHolder) holder).recipeName.setText((item).getName());
            List<ProductResponse> productsList = (item).getProducts();
            StringBuilder sb = new StringBuilder();
            for (ProductResponse product : productsList) {
                if (productsList.indexOf(product) == productsList.size() - 1) {
                    sb.append(product.getName());
                } else {
                    sb.append(product.getName()).append(", ");
                }
            }

            ((RecipeItemHolder) holder).recipeDesc.setText(sb.toString());

            ((RecipeItemHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecipeClick(item);
                }
            });

                ((RecipeItemHolder) holder).checkButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (editableState) {
                            int i = holder.getAdapterPosition();
                            if (isChecked) {
                                oneRecipeChecked = true;
                                checkedRecipePosition = i;
                                notifyItemRangeChanged(0, i);
                                if (list.size() - 1 > i) {
                                    notifyItemRangeChanged(i + 1, list.size() - 1);
                                }
                            } else {
                                oneRecipeChecked = false;
                                checkedRecipePosition = -1;
                                notifyItemRangeChanged(0, i);
                                if (list.size() - 1 > i) {
                                    notifyItemRangeChanged(i + 1, list.size());
                                }
                            }
                        }
                    }
                });

            if ((item).getSelected() == RecipeResponse.BAD_SELECT) {
                ((RecipeItemHolder) holder).container.setBackgroundResource(R.drawable.card_view_bg_red);
                ((RecipeItemHolder) holder).tvRecipeChange.setVisibility(View.VISIBLE);
                ((RecipeItemHolder) holder).tvRecipeChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setEditableState(true);
                        listener.onRecipeForChangeClick();
                        checkedRecipePosition = holder.getAdapterPosition();
                        oneRecipeChecked = true;
                        notifyDataSetChanged();
                    }
                });
            } else {
                ((RecipeItemHolder) holder).container.setBackgroundResource(R.drawable.card_view_bg);
                ((RecipeItemHolder) holder).tvRecipeChange.setVisibility(View.GONE);
            }

        }
    }


    @Override
    public int getItemViewType(int position) {

        if (list.get(position) instanceof CategoryResponse) {
            if (((CategoryResponse) list.get(position)).getParent() != null) {
                return ItemType.subCategory;
            } else {
                return ItemType.mainCategory;
            }
        }

        if (list.get(position) instanceof RecipeResponse)
            return ItemType.recipeCard;

        return 0;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    private static class ItemType {
        private static final int mainCategory = 3;
        private static final int subCategory = 4;
        private static final int recipeCard = 5;
    }

    private static class RecipeItemHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView recipeName;
        private final TextView recipeDesc;
        private final ImageView recipeImage;
        private final ToggleButton checkButton;
        private final LinearLayout container;
        private final TextView tvRecipeChange;

        private RecipeItemHolder(@NonNull View v) {
            super(v);

            cardView = v.findViewById(R.id.card_view);
            recipeName = v.findViewById(R.id.name);
            recipeDesc = v.findViewById(R.id.description);
            recipeImage = v.findViewById(R.id.image_recipe);
            checkButton = v.findViewById(R.id.button_check);
            container = v.findViewById(R.id.container_layout);
            tvRecipeChange = v.findViewById(R.id.tv_change_recipe);

        }
    }

    private static class CategoryItemHolder extends RecyclerView.ViewHolder {

        private final TextView name;

        private CategoryItemHolder(@NonNull View v) {
            super(v);

            name = v.findViewById(R.id.name);
        }
    }

    public void setEditableState(boolean state) {
        this.editableState = state;
    }

    public boolean getEditableState() {
        return editableState;
    }


    public boolean isOneRecipeChecked() {
        return oneRecipeChecked;
    }

    public int getCheckedRecipePosition() {
        return checkedRecipePosition;
    }

    public RecipeResponse getCheckedRecipe() {
        return (RecipeResponse) list.get(getCheckedRecipePosition());
    }

    public void deleteRecipe(int position) {
        oneRecipeChecked = false;

        List<RecipeResponse> recipeListForUpdate = SettingHelper.getPreparedRecipesList();
        RecipeResponse checkedRecipe = (RecipeResponse) list.get(position);
        for (int n = 0; n < recipeListForUpdate.size(); n++) {
            if (recipeListForUpdate.get(n).getId() == checkedRecipe.getId()) {
                recipeListForUpdate.remove(n);
                n--;
            }
        }
        SettingHelper.savePreparedRecipes(recipeListForUpdate);

        list.remove(position - 1);
        list.remove(position - 1);
        checkedRecipePosition = -1;

        notifyDataSetChanged();
    }
}

