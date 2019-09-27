package ru.sigmadigital.recipes.api.asynctasks;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.Sender;
import ru.sigmadigital.recipes.model.request.UpdatePasswordRequest;
import ru.sigmadigital.recipes.model.response.BaseResponse;
import ru.sigmadigital.recipes.model.response.DoneResponse;

public class ChangePasswordTask extends AsyncTask<Void, Void, DoneResponse> {

    private UpdatePasswordRequest request;
    private OnChangePasswordListener listener;

    public ChangePasswordTask(UpdatePasswordRequest request, OnChangePasswordListener listener) {
        this.request = request;
        this.listener = listener;
    }

    @Override
    protected DoneResponse doInBackground(Void... voids) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", ValidateToken.getTokenString());
        headers.put("content-type", "application/json");

        if (isCancelled())
            return null;

        BaseResponse resp = Sender.sendPost(Network.BASE_URL + "api/updatepassword", request.toString(), headers);
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
            listener.onPasswordChange(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnChangePasswordListener {
        void onPasswordChange(DoneResponse response);
    }
}
