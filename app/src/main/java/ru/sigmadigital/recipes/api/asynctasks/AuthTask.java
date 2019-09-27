package ru.sigmadigital.recipes.api.asynctasks;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.Sender;
import ru.sigmadigital.recipes.model.User;
import ru.sigmadigital.recipes.model.response.BaseResponse;
import ru.sigmadigital.recipes.model.response.TokenResponse;


public class AuthTask extends AsyncTask<Void, Void, TokenResponse> {

    private User user;
    private OnAuthorizationListener listener;

    public AuthTask(User user, OnAuthorizationListener listener) {
        this.user = user;
        this.listener = listener;
    }

    @Override
    protected TokenResponse doInBackground(Void... voids) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");

        if (isCancelled())
            return null;

        BaseResponse resp = Sender.sendPost(Network.BASE_URL + "api/autorization", user.toString(), headers);
        if (resp != null && resp.getCode() == 200) {
            return TokenResponse.fromJson(resp.getData(), TokenResponse.class);
        } else {
            if (!isCancelled())
                App.showToast(R.string.disconnect);
        }
        return null;
    }

    @Override
    protected void onPostExecute(TokenResponse baseResponse) {
        super.onPostExecute(baseResponse);
        if (listener != null) {
            listener.OnAuthorization(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnAuthorizationListener {
        void OnAuthorization(TokenResponse token);
    }
}
