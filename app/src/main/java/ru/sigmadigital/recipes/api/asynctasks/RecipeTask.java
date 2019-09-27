package ru.sigmadigital.recipes.api.asynctasks;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.Sender;
import ru.sigmadigital.recipes.model.response.BaseResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;


public class RecipeTask extends AsyncTask<Void, Void, RecipeResponse> {


    private long recipeId;
    private OnGetRecipe listener;

    public RecipeTask(long recipeId, OnGetRecipe listener) {
        this.recipeId = recipeId;
        this.listener = listener;
    }

    @Override
    protected RecipeResponse doInBackground(Void... voids) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", ValidateToken.getTokenString());
        headers.put("content-type", "application/json");

        if (isCancelled()) {
            return null;
        }

        BaseResponse resp = Sender.sendGet(Network.BASE_URL + "api/recipe/" + recipeId, headers);
        if (resp != null && resp.getCode() == 200) {
            RecipeResponse rresp = RecipeResponse.fromJson(resp.getData(), RecipeResponse.class);
            if (rresp != null) {
                rresp.sortSteps();
            } else {
                if (!isCancelled())
                    App.showToast(R.string.disconnect);
            }
            return rresp;
        }
        return null;
    }

    @Override
    protected void onPostExecute(RecipeResponse baseResponse) {
        super.onPostExecute(baseResponse);
        if (listener != null && !isCancelled()) {
            listener.onGetRecipe(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnGetRecipe {
        void onGetRecipe(RecipeResponse recipe);
    }
}
