package ru.sigmadigital.recipes.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.model.response.CategoryResponse;
import ru.sigmadigital.recipes.model.response.ProductResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;


public class ProductListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<T> list;
    private OnProductCheckClick listener;
    private boolean isPurchaseClickable;

    private String html;
    private String htmlPayed;


    public interface OnProductCheckClick {
        void onProductCheckClick(ProductResponse product);
    }


    public ProductListAdapter(OnProductCheckClick listener, List<T> list, boolean isPurchaseClickable) {
        this.list = list;
        this.listener = listener;
        this.isPurchaseClickable = isPurchaseClickable;

        StringBuilder buf = new StringBuilder();
        try {
            InputStream json = App.getAppContext().getAssets().open("product.html");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        html = buf.toString();

        buf = new StringBuilder();
        try {
            InputStream json = App.getAppContext().getAssets().open("product-is-payed.html");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        htmlPayed = buf.toString();
    }


    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int type) {
        View v;
        switch (type) {
            case ItemType.rootCategory:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_title, null);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new CategoryItemHolder(v);
            case ItemType.recipe:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_subtitle, null);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new CategoryItemHolder(v);
            case ItemType.product:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product_with_button, null);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new ProductItemHolder(v);
            default:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product_with_button, null);
                return new ProductItemHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int i) {
        final T item = list.get(i);

        if (holder instanceof CategoryItemHolder) {
            if (item instanceof CategoryResponse)
                ((CategoryItemHolder) holder).name.setText(((CategoryResponse) item).getName());

            if (item instanceof RecipeResponse)
                ((CategoryItemHolder) holder).name.setText(String.format("%1s: %2s", ((RecipeResponse) item).getCategory().getName(), ((RecipeResponse) item).getName()));

        }

        if (holder instanceof ProductItemHolder) {

            //webView
            ((ProductItemHolder) holder).webView.getSettings().setLoadWithOverviewMode(true);
            ((ProductItemHolder) holder).webView.getSettings().setUseWideViewPort(true);

            if (((ProductResponse) item).isPurchased()) {
                setProductIntoWebView((ProductResponse) item, ((ProductItemHolder) holder).webView, true);
            } else {
                setProductIntoWebView((ProductResponse) item, ((ProductItemHolder) holder).webView, false);
            }

            //button
            ((ProductItemHolder) holder).button.setChecked(((ProductResponse) item).isPurchased());
            ((ProductItemHolder) holder).button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((((ProductItemHolder) holder).button.isChecked())) {
                        ((ProductResponse) item).setPurchased(true);
                        setProductIntoWebView((ProductResponse) item, ((ProductItemHolder) holder).webView, true);
                    } else {
                        ((ProductResponse) item).setPurchased(false);
                        setProductIntoWebView((ProductResponse) item, ((ProductItemHolder) holder).webView, false);
                    }

                    listener.onProductCheckClick((ProductResponse) item);
                }
            });

            if (!isPurchaseClickable && ((ProductResponse) item).isPurchased()) {
                ((ProductItemHolder) holder).button.setClickable(false);
            } else {
                ((ProductItemHolder) holder).button.setClickable(true);
            }
        }
    }

    private void setProductIntoWebView(ProductResponse product, WebView webView, boolean isCrossedText) {
        String hm;
        if (isCrossedText) {
            hm = htmlPayed.replaceAll(Pattern.quote("{1}"), product.getName())
                    .replaceAll(Pattern.quote("{2}"),
                            product.getCount() + " " + product.getUnits().getName());
        } else {
            hm = html.replaceAll(Pattern.quote("{1}"), product.getName())
                    .replaceAll(Pattern.quote("{2}"),
                            product.getCount() + " " + product.getUnits().getName());
        }

        webView.loadDataWithBaseURL("file:///android_asset/", hm, "text/html", "utf-8", "");
    }

    public List<T> getProductList() {
        return list;
    }

    public List<ProductResponse> getProductOnlyList() {
        List<ProductResponse> productResponses = new ArrayList<>();
        for (T t : list) {
            if (t instanceof ProductResponse) {
                productResponses.add((ProductResponse) t);
            }
        }
        return productResponses;
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public int getItemViewType(int position) {

        if (list.get(position) instanceof ProductResponse)
            return ItemType.product;

        if (list.get(position) instanceof CategoryResponse)
            return ItemType.rootCategory;

        if (list.get(position) instanceof RecipeResponse)
            return ItemType.recipe;

        return 0;
    }


    //holders
    private static class ItemType {
        private static final int product = 3;
        private static final int recipe = 4;
        private static final int rootCategory = 5;
    }

    private static class ProductItemHolder extends RecyclerView.ViewHolder {

        private WebView webView;
        private ToggleButton button;

        private ProductItemHolder(@NonNull View v) {
            super(v);
            button = v.findViewById(R.id.tongle);
            webView = v.findViewById(R.id.web_view);
        }
    }

    private static class CategoryItemHolder extends RecyclerView.ViewHolder {

        private TextView name;

        private CategoryItemHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.name);
        }
    }
}
