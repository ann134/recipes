package ru.sigmadigital.recipes.api.asynctasks;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.Sender;
import ru.sigmadigital.recipes.model.request.CreateRecipeRequest;
import ru.sigmadigital.recipes.model.response.BaseResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;


public class UpdateRecipeTask extends AsyncTask<Void, Void, RecipeResponse> {

    private long recipeId;
    private CreateRecipeRequest recipe;
    private OnUpdateRecipeListener listener;

    public UpdateRecipeTask(CreateRecipeRequest recipe, long recipeId, OnUpdateRecipeListener listener) {
        this.recipe = recipe;
        this.recipeId = recipeId;
        this.listener = listener;
    }

    @Override
    protected RecipeResponse doInBackground(Void... voids) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", ValidateToken.getTokenString());
        headers.put("content-type", "application/json");

        if (isCancelled())
            return null;

        BaseResponse resp = Sender.sendPost(Network.BASE_URL + "api/recipe/" + recipeId, recipe.toString(), headers);
        if (resp != null && resp.getCode() == 200) {
            return RecipeResponse.fromJson(resp.getData(), RecipeResponse.class);
        } else {
            if (!isCancelled())
                App.showToast(R.string.disconnect);
        }
        return null;
    }

    @Override
    protected void onPostExecute(RecipeResponse baseResponse) {
        super.onPostExecute(baseResponse);
        if (listener != null && !isCancelled()) {
            listener.onUpdateRecipe(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnUpdateRecipeListener {
        void onUpdateRecipe(RecipeResponse recipe);
    }
}
