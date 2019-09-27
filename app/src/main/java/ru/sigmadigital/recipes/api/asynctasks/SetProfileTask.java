package ru.sigmadigital.recipes.api.asynctasks;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.Sender;
import ru.sigmadigital.recipes.model.request.ProfileRequest;
import ru.sigmadigital.recipes.model.response.BaseResponse;
import ru.sigmadigital.recipes.model.response.ProfileResponse;


public class SetProfileTask extends AsyncTask<Void, Void, ProfileResponse> {

    private ProfileRequest request;
    private OnSetProfileListener listener;

    public SetProfileTask(ProfileRequest profile, OnSetProfileListener onSetProfileListener) {
        this.request = profile;
        this.listener = onSetProfileListener;
    }

    @Override
    protected ProfileResponse doInBackground(Void... voids) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", ValidateToken.getTokenString());
        headers.put("content-type", "application/json");

        if (isCancelled())
            return null;

        BaseResponse resp = Sender.sendPost(Network.BASE_URL + "api/profile", request.toString(), headers);
        if (resp != null && resp.getCode() == 200) {
            return ProfileResponse.fromJson(resp.getData(), ProfileResponse.class);
        } else {
            if (!isCancelled())
                App.showToast(R.string.disconnect);
        }
        return null;
    }

    @Override
    protected void onPostExecute(ProfileResponse baseResponse) {
        super.onPostExecute(baseResponse);
        if (listener != null && !isCancelled()) {
            listener.onSetProfile(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnSetProfileListener {
        void onSetProfile(ProfileResponse response);
    }
}
