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


public class RegistrationTask extends AsyncTask<Void, Void, TokenResponse> {

    private User user;
    private OnRegistrationListener listener;

    public RegistrationTask(User user, OnRegistrationListener listener) {
        this.user = user;
        this.listener = listener;
    }

    @Override
    protected TokenResponse doInBackground(Void... voids) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");

        if (isCancelled())
            return null;

        BaseResponse resp = Sender.sendPost(Network.BASE_URL + "api/registration", user.toString(), headers);
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
            listener.onRegistration(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnRegistrationListener {
        void onRegistration(TokenResponse token);
    }
}
