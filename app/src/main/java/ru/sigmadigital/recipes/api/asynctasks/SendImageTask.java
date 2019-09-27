package ru.sigmadigital.recipes.api.asynctasks;

import android.os.AsyncTask;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.Sender;
import ru.sigmadigital.recipes.model.response.BaseResponse;
import ru.sigmadigital.recipes.model.response.DoneResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;

public class SendImageTask extends AsyncTask<Void, Void, DoneResponse> {

    private RecipeResponse recipe;
    private InputStream inputStream;
    private OnSendImageListener listener;

    public SendImageTask(RecipeResponse recipe, InputStream inputStream, OnSendImageListener listener) {
        this.inputStream = inputStream;
        this.recipe = recipe;
        this.listener = listener;
    }

    @Override
    protected DoneResponse doInBackground(Void... voids) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", ValidateToken.getTokenString());
        headers.put("content-type", "image/*");

        if (isCancelled())
            return null;

        BaseResponse resp = Sender.sendPost(Network.BASE_URL + "api/recipe/" + recipe.getId() + "/photo", inputStream, headers);
        if (resp != null && resp.getCode() == 200) {
            return DoneResponse.fromJson(resp.getData(), DoneResponse.class);
        } else {
            if (!isCancelled())
                App.showToast(R.string.disconnect);
        }
        return null;
    }

    @Override
    protected void onPostExecute(DoneResponse baseResponse) {
        super.onPostExecute(baseResponse);
        if (listener != null && !isCancelled()) {
            listener.onSendImage(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnSendImageListener {
        void onSendImage(DoneResponse response);
    }
}
