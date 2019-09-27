package ru.sigmadigital.recipes.api.asynctasks;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.Sender;
import ru.sigmadigital.recipes.model.response.BaseResponse;
import ru.sigmadigital.recipes.model.response.DoneResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;

public class DeleteRecipeTask extends AsyncTask<Void, Void, DoneResponse> {

    private RecipeResponse recipe;
    private OnDeleteRecipeListener listener;

    public DeleteRecipeTask(RecipeResponse recipe, OnDeleteRecipeListener listener) {
        this.recipe = recipe;
        this.listener = listener;
    }

    @Override
    protected DoneResponse doInBackground(Void... voids) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", ValidateToken.getTokenString());

        if (isCancelled())
            return new DoneResponse();

        BaseResponse resp = Sender.sendGet(Network.BASE_URL + "api/recipe/" + recipe.getId() + "/delete", headers);
        if (resp != null && resp.getCode() == 200) {
            return DoneResponse.fromJson(resp.getData(), DoneResponse.class);
        } else {
            if (!isCancelled())
                App.showToast(R.string.disconnect);
        }
        return new DoneResponse();
    }

    @Override
    protected void onPostExecute(DoneResponse baseResponse) {
        super.onPostExecute(baseResponse);
        if (listener != null && !isCancelled()) {
            listener.onDeleteRecipe(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnDeleteRecipeListener {
        void onDeleteRecipe(DoneResponse response);
    }

}
