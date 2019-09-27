package ru.sigmadigital.recipes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.activity.MainActivity;
import ru.sigmadigital.recipes.adapter.ProductListAdapter;
import ru.sigmadigital.recipes.model.realmObjects.RealmProduct;
import ru.sigmadigital.recipes.model.response.CategoryResponse;
import ru.sigmadigital.recipes.model.response.ProductResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.util.SettingHelper;


public class ProductsFragment extends BaseFragment implements View.OnClickListener, ProductListAdapter.OnProductCheckClick {


    public static ProductsFragment newInstance() {
        return new ProductsFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_products, container, false);

        List<RecipeResponse> recipes = SettingHelper.getPreparedRecipesList();
        if (recipes.size() > 0) {
            v.findViewById(R.id.tv_empty_products).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.product_list).setVisibility(View.GONE);
            v.findViewById(R.id.text).setVisibility(View.GONE);
            return v;
        }

        List list = new ArrayList();
        for (RecipeResponse x : recipes) {
            if (list.size() == 0) {
                CategoryResponse category = x.getCategory();
                while (category.getParent() != null) {
                    category = category.getParent();
                }
                list.add(category);
            }
            list.add(x);
            for (ProductResponse y : x.getProducts()) {
                list.add(y);
            }
        }


        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        final ProductListAdapter adapter = new ProductListAdapter(this, list/*, ProductListAdapter.PRODUCTS_FLAG*/, true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        v.findViewById(R.id.product_list).setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideAvatar(false);
            ((MainActivity) getActivity()).getActionButton().setVisibility(View.GONE);
        }
    }


    @Override
    public void onProductCheckClick(ProductResponse product) {
        if (App.getRealm() != null) {
            App.getRealm().beginTransaction();
            RealmProduct realmProduct = App.getRealm().where(RealmProduct.class).equalTo("id", product.getId()).findFirst();
            if (realmProduct != null)
                realmProduct.setPurchased(product.isPurchased());
            App.getRealm().commitTransaction();

        }
    }

    @Override
    public void onClick(View v) {
        loadFragment(ProductListFragment.newInstance(), getString(R.string.products_list), true);
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content_main;
    }
}
