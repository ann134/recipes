package ru.sigmadigital.recipes.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.fragment.DeleteDialogFragment;
import ru.sigmadigital.recipes.model.realmObjects.PreparedMenuRealm;
import ru.sigmadigital.recipes.model.response.CategoryResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;


public class ArchiveMenuAdapter extends RecyclerView.Adapter<ArchiveMenuAdapter.SavedFoodFolder> {

    private List<PreparedMenuRealm> preparedMenuRealmList;
    private OnPreparedMenuItemClicker listener;
    private FragmentManager fm;

    public ArchiveMenuAdapter(FragmentManager fm, List<PreparedMenuRealm> list, OnPreparedMenuItemClicker listener) {
        this.preparedMenuRealmList = list;
        this.listener = listener;
        this.fm = fm;
    }

    public interface OnPreparedMenuItemClicker {
        void onItemClick(PreparedMenuRealm currPrepMenu);
    }

    @NonNull
    @Override
    public SavedFoodFolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_saved_menu, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new SavedFoodFolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedFoodFolder holder, final int i) {

        final PreparedMenuRealm currPrepMenu = preparedMenuRealmList.get(i);
        if (currPrepMenu != null) {

            holder.foodContainer.removeAllViews();
            List<RecipeResponse> recipes = new ArrayList<>(currPrepMenu.getRecipeResponses());
            for (RecipeResponse x : recipes) {
                addChildFoodLayout(holder.foodContainer, x.getCategory().getName(), x.getName());
            }

            RecipeResponse recipe = recipes.get(0);
            CategoryResponse mainCategory = recipe.getCategory();
            while (mainCategory.getParent() != null) {
                mainCategory = mainCategory.getParent();
            }
            holder.tvAutoChoose.setText(String.format("Автовыбор: %s", mainCategory.getName()));

            Date date = new Date(currPrepMenu.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            holder.tvDate.setText(String.format(Locale.ENGLISH, "%td/%tm/%tY", calendar.getTime(), calendar.getTime(), calendar.getTime()));

        }

        holder.imvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeleteDialogFragment.newInstance("Удалить меню из архива?", new DeleteDialogFragment.OnActionClickListener() {
                    @Override
                    public void onActionClick(int action) {
                        if (action == DeleteDialogFragment.PRESS_OK) {
                            if (App.getRealm() != null && currPrepMenu != null) {
                                App.getRealm().beginTransaction();
                                long id = currPrepMenu.getId();
                                preparedMenuRealmList.remove(i);
                                App.getRealm().where(PreparedMenuRealm.class).equalTo("id", id).findAll().deleteAllFromRealm();
                                App.getRealm().commitTransaction();
                                notifyItemRemoved(i);
                                notifyItemRangeChanged(0, preparedMenuRealmList.size());
                            }
                        }
                    }
                }).show(fm, "dialog");
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(currPrepMenu);
            }
        });
    }

    private void addChildFoodLayout(LinearLayout foodContainer, String category, String name) {
        @SuppressLint("InflateParams") View childView = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_saved_menu_recipe, null);
        TextView tvFoodName = childView.findViewById(R.id.tv_food_name);
        TextView tvFoodCategory = childView.findViewById(R.id.tv_food_category);
        tvFoodName.setText(name);
        tvFoodCategory.setText(category);
        foodContainer.addView(childView);
    }


    @Override
    public int getItemCount() {
        return preparedMenuRealmList.size();
    }


    class SavedFoodFolder extends RecyclerView.ViewHolder {

        private ImageView imvDelete;
        private TextView tvAutoChoose;
        private TextView tvDate;
        private LinearLayout foodContainer;

        private SavedFoodFolder(@NonNull View itemView) {
            super(itemView);

            imvDelete = itemView.findViewById(R.id.imv_delete);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAutoChoose = itemView.findViewById(R.id.tv_auto_choose);
            foodContainer = itemView.findViewById(R.id.food_container);
        }
    }
}


