package ru.sigmadigital.recipes.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.activity.MainActivity;
import ru.sigmadigital.recipes.api.asynctasks.CategoriesTask;
import ru.sigmadigital.recipes.model.response.CategoryResponse;
import ru.sigmadigital.recipes.view.RadialMenuWidget;


public class RadialMenuFragment extends BaseFragment implements RadialMenuWidget.OnMenuItemClick, RadialMenuWidget.OnCenterClick {

    private CategoryResponse mainCategory = null;
    private List<CategoryResponse> subCategories;


    private TextView textView;
    private RelativeLayout radialMenuContainer;
    private View strokeRadialMenu;
    private ProgressBar loading;
    private TextView errorText;

    private CategoriesTask categoriesTask;


    public static RadialMenuFragment newInstance() {
        return new RadialMenuFragment();
    }

    public static RadialMenuFragment newInstance(CategoryResponse category) {
        RadialMenuFragment fragment = new RadialMenuFragment();
        Bundle args = new Bundle();
        args.putSerializable("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("category")) {
            mainCategory = (CategoryResponse) getArguments().getSerializable("category");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") final View v = inflater.inflate(R.layout.fragment_radial_menu, null);
        loading = v.findViewById(R.id.loading);
        errorText = v.findViewById(R.id.error_text);
        textView = v.findViewById(R.id.text);
        strokeRadialMenu = v.findViewById(R.id.stroke_radial_menu);
        radialMenuContainer = v.findViewById(R.id.radial_menu_container);

        init();
        getCategories();

        return v;
    }

    private void init() {
        if (mainCategory == null) {
            textView.setText(R.string.chose_category_of_food);
        } else {
            textView.setText(String.format(getString(R.string.chose_meal_or_autoselect), mainCategory.getName()));
            if (mainCategory.getId() == 5) {
                textView.setText(getResources().getString(R.string.chose_meal_for_guests));
            }
        }
    }

    private void getCategories() {
        categoriesTask = new CategoriesTask(getParentId(), new CategoriesTask.OnGetCategoriesListener() {
            @Override
            public void onGetCategories(List<CategoryResponse> categories) {
                subCategories = categories;
                if (subCategories.size() > 0)
                    initRadialMenu();
                else {
                    loading.setVisibility(View.GONE);
                    errorText.setVisibility(View.VISIBLE);
                }
            }
        });
        categoriesTask.execute();
    }

    private void initRadialMenu() {
        if (getActivity() != null) {
            loading.setVisibility(View.GONE);
            strokeRadialMenu.setVisibility(View.VISIBLE);

            RadialMenuWidget pieMenu = new RadialMenuWidget(getActivity(), subCategories, this);

            if (mainCategory == null)
                pieMenu.setCenter(getString(R.string.prepared_menu), R.drawable.logo_white, this);
            else
                pieMenu.setCenter(getString(R.string.autoselect), R.drawable.logo_white, this);

            radialMenuContainer.addView(pieMenu);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (categoriesTask != null) {
            categoriesTask.cancel(true);
        }
    }

    private long getParentId() {
        if (mainCategory == null)
            return 0;
        else
            return mainCategory.getId();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            if (getActivity() != null && getActivity() instanceof MainActivity) {

                ((MainActivity) getActivity()).getActionButton().setVisibility(View.GONE);
                ((MainActivity) getActivity()).hideAvatar(false);

                if (mainCategory == null) {
                    ((MainActivity) getActivity()).getToggle().setDrawerIndicatorEnabled(true);
                    ((MainActivity) getActivity()).getBtnBackLayout().setVisibility(View.GONE);
                } else {
                    ((MainActivity) getActivity()).getToggle().setDrawerIndicatorEnabled(false);
                    ((MainActivity) getActivity()).getBtnBackLayout().setVisibility(View.VISIBLE);
                }
            }
        }
    }


    @Override
    public void onItemSelected(CategoryResponse category) {

        if (category.getAction().equals("products")) {
            loadFragment(ProductsFragment.newInstance(), category.getName(), true);
            return;
        }

        if (category.getAction().equals("recipes")) {
            loadFragment(CategoryFragment.newInstance(category), category.getName(), true);
            return;
        }

        if (category.getAction().equals("subcat")) {
            loadFragment(RadialMenuFragment.newInstance(category), category.getName(), true);
        }
    }

    @Override
    public void centerSelected() {
        if (mainCategory == null) {
            loadFragment(PreparedMenuFragment.newInstance(), getString(R.string.prepared_menu), true);
        } else {
            loadFragment(ViewPagerFragment.newInstance(mainCategory), String.format("Автовыбор: %s", mainCategory.getName()), true);
        }
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content_main;
    }
}
