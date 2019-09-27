package ru.sigmadigital.recipes.api.asynctasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.Sender;
import ru.sigmadigital.recipes.model.response.BaseResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;


public class RecipesTask extends AsyncTask<Void, Void, List<RecipeResponse>> {

    private long category;
    private OnGetRecipes listener;

    public RecipesTask(long category, OnGetRecipes listener) {
        this.category = category;
        this.listener = listener;
    }

    @Override
    protected List<RecipeResponse> doInBackground(Void... voids) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", ValidateToken.getTokenString());
        headers.put("content-type", "application/json");

        if (isCancelled()) {
            return null;
        }

        BaseResponse resp = Sender.sendGet(Network.BASE_URL + "api/recipes/" + category, headers);
        if (resp != null && resp.getCode() == 200) {
            Type list = new TypeToken<ArrayList<RecipeResponse>>() {
            }.getType();
            List<RecipeResponse> items = new Gson().fromJson(resp.getData(), list);
            if (items != null) {
                for (int i = 0; i < items.size(); i++) {
                    items.get(i).sortSteps();
                }
            }
            return items;
        } else {
            if (!isCancelled())
                App.showToast(R.string.disconnect);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<RecipeResponse> baseResponse) {
        super.onPostExecute(baseResponse);
        if (listener != null && !isCancelled()) {
            listener.onGetRecipes(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }


    public interface OnGetRecipes {
        void onGetRecipes(List<RecipeResponse> recipes);
    }
}
