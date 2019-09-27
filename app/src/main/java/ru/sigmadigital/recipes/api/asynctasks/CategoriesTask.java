package ru.sigmadigital.recipes.api.asynctasks;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.Sender;
import ru.sigmadigital.recipes.model.response.BaseResponse;
import ru.sigmadigital.recipes.model.response.CategoryResponse;


public class CategoriesTask extends AsyncTask<Void, Void, List<CategoryResponse>> {

    private long parent;
    private OnGetCategoriesListener listener;

    public CategoriesTask(long parent, OnGetCategoriesListener listener) {
        this.parent = parent;
        this.listener = listener;
    }

    @Override
    protected List<CategoryResponse> doInBackground(Void... voids) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", ValidateToken.getTokenString());

        if (isCancelled())
            return new ArrayList<>();

        BaseResponse resp = Sender.sendGet(Network.BASE_URL + "api/categories/" + parent, headers);
        if (resp != null && resp.getCode() == 200) {
            return CategoryResponse.fromJsonList(resp.getData());
        } else {
            if (!isCancelled())
                App.showToast(R.string.disconnect);
        }
        return new ArrayList<>();
    }

    @Override
    protected void onPostExecute(List<CategoryResponse> baseResponse) {
        super.onPostExecute(baseResponse);
        if (listener != null && !isCancelled()) {
            listener.onGetCategories(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnGetCategoriesListener {
        void onGetCategories(List<CategoryResponse> categories);
    }
}