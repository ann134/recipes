package ru.sigmadigital.recipes.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.model.response.ProductResponse;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private List<ProductResponse> products = new ArrayList<>();

    public ProductsAdapter() {
    }

    public ProductsAdapter(List<ProductResponse> products) {
        this.products = products;
    }


    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int type) {
        @SuppressLint("InflateParams")
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ProductHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final ProductHolder holder, final int i) {
        final ProductResponse product = products.get(i);
        holder.productName.setText(product.getName());
        holder.productCount.setText(product.getCount());
        holder.productUnit.setText(product.getUnits().getName());

        final boolean[] areAllViewsDrawn = {false, false, false};

        holder.productName.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                holder.productName.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                areAllViewsDrawn[0] = true;
                setDotsWidth(areAllViewsDrawn, holder, holder.productName, holder.productCount, holder.productUnit, holder.dots);
            }
        });

        holder.productCount.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                holder.productCount.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                areAllViewsDrawn[1] = true;
                setDotsWidth(areAllViewsDrawn, holder, holder.productName, holder.productCount, holder.productUnit, holder.dots);
            }
        });

        holder.productUnit.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                holder.productUnit.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                areAllViewsDrawn[2] = true;
                setDotsWidth(areAllViewsDrawn, holder, holder.productName, holder.productCount, holder.productUnit, holder.dots);
            }
        });
    }


    private void setDotsWidth(boolean[] areAllViewsDrawn, ProductHolder holder, TextView name, TextView count, TextView unit, View dots) {
        if (!areAllViewsDrawn[0] || !areAllViewsDrawn[1] || !areAllViewsDrawn[2])
            return;

        int lineCount = name.getLayout().getLineCount();
        float lastLineRight = name.getLayout().getLineRight(lineCount - 1);
        int screenWidth = holder.itemView.getWidth();
        float marginsWidth = (30 + 30 + 8) * holder.itemView.getContext().getResources().getDisplayMetrics().density;

        ConstraintSet set = new ConstraintSet();
        set.clone(holder.constraintLayout);

        if (lastLineRight + count.getWidth() + unit.getWidth() + marginsWidth > screenWidth) {
            set.constrainHeight(name.getId(), name.getHeight() + name.getLineHeight());
            set.constrainWidth(dots.getId(), (int) (screenWidth - count.getWidth() - unit.getWidth() - marginsWidth));
            set.connect(dots.getId(), ConstraintSet.RIGHT, count.getId(), ConstraintSet.LEFT, 0);

        } else {
            set.constrainWidth(dots.getId(), (int) (screenWidth - lastLineRight - count.getWidth() - unit.getWidth() - marginsWidth));
            set.connect(dots.getId(), ConstraintSet.RIGHT, count.getId(), ConstraintSet.LEFT, 0);
        }

        set.applyTo(holder.constraintLayout);
    }


    public void addProduct(ProductResponse product) {
        products.add(product);
        notifyItemRangeChanged(0, getItemCount());
    }

    private void removeProduct(int product) {
        products.remove(product);
        notifyItemRemoved(product);
    }

    public List<ProductResponse> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponse> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    //swipes and drags
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(products, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(products, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int position) {
        removeProduct(position);
    }



    public class ProductHolder extends RecyclerView.ViewHolder implements SimpleItemTouchHelperCallback.ItemTouchHelperViewHolder {

        private FrameLayout frame;
        private ConstraintLayout constraintLayout;
        private TextView productName;
        private TextView productCount;
        private TextView productUnit;
        private View dots;

        private ProductHolder(@NonNull View v) {
            super(v);
            frame = v.findViewById(R.id.frame);
            constraintLayout = v.findViewById(R.id.layout);
            productName = v.findViewById(R.id.name);
            productCount = v.findViewById(R.id.count);
            productUnit = v.findViewById(R.id.unit);
            dots = v.findViewById(R.id.dots);
        }

        @Override
        public void onItemSelected() {
            frame.setBackgroundResource(R.color.background_item_selected);
        }

        @Override
        public void onItemClear() {
            Log.e("onItemClear", "onItemClear");
            frame.setBackgroundResource(R.color.background_item);
        }
    }
}
