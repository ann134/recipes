package ru.sigmadigital.recipes.fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import ru.sigmadigital.recipes.model.realmObjects.PreparedMenuRealm;
import ru.sigmadigital.recipes.model.realmObjects.RealmProduct;
import ru.sigmadigital.recipes.model.response.ProductResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.util.SettingHelper;


public class ProductListFragment extends BaseFragment implements View.OnClickListener, ProductListAdapter.OnProductCheckClick {

    private ProductListAdapter adapter;


    public static ProductListFragment newInstance() {
        return new ProductListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_list, container, false);

        List<RecipeResponse> recipes = SettingHelper.getPreparedRecipesList();
        List list = new ArrayList();
        for (RecipeResponse x : recipes) {
            for (ProductResponse y : x.getProducts()) {
                if (!y.isPurchased()) {
                    list.add(y);
                }
            }
        }

        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        adapter = new ProductListAdapter(this, list/*, ProductListAdapter.PRODUCT_LIST_FLAG*/, false);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        v.findViewById(R.id.save).setOnClickListener(this);
        v.findViewById(R.id.share).setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideAvatar(true);
            ((MainActivity) getActivity()).getActionButton().setVisibility(View.VISIBLE);
            Drawable ic = getResources().getDrawable(R.drawable.ic_trash);
            ic.setColorFilter(getResources().getColor(R.color.icon_toolbar), PorterDuff.Mode.SRC_IN);
            ((MainActivity) getActivity()).setActionButtonImage(ic);
            ((MainActivity) getActivity()).setActionButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.clear();
                }
            });
        }
    }


    @Override
    public void onProductCheckClick(ProductResponse product) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                save();
                break;
            case R.id.share:
                share();
                break;
        }
    }

    private void save(){
        App.getRealm().beginTransaction();
        PreparedMenuRealm prepmenu = App.getRealm().where(PreparedMenuRealm.class).equalTo("id", 10).findFirst();

        if (adapter != null && prepmenu != null) {
            List<ProductResponse> adapterProducts = adapter.getProductOnlyList();
            List<RealmProduct> realmProducts = prepmenu.getProductResponses();


            for (RealmProduct realmProduct : realmProducts) {
                for (ProductResponse adapterProduct : adapterProducts) {
                    if (adapterProduct.getId() == realmProduct.getId()) {
                        realmProduct.setPurchased(adapterProduct.isPurchased());
                    }
                }
            }

            adapter.notifyDataSetChanged();

            App.getRealm().insertOrUpdate(prepmenu);
        }
        App.getRealm().commitTransaction();
    }

    private void share(){
        StringBuilder sb = new StringBuilder();
        sb.append("Список продуктов:\n");
        for (Object x : adapter.getProductList()) {
            sb.append(((ProductResponse) x).getName())
                    .append(" - ")
                    .append(((ProductResponse) x).getCount())
                    .append(" ")
                    .append(((ProductResponse) x).getUnits().getName())
                    .append("\n");
        }

        String s = sb.toString();
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, s);
        try {
            startActivity(Intent.createChooser(intent, "action"));
        } catch (android.content.ActivityNotFoundException ex) {

        }
    }


    @Override
    protected int getFragmentContainer() {
        return R.id.content_main;
    }

}
