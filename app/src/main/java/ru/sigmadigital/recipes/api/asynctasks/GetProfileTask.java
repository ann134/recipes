package ru.sigmadigital.recipes.api.asynctasks;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.Sender;
import ru.sigmadigital.recipes.model.response.BaseResponse;
import ru.sigmadigital.recipes.model.response.ProfileResponse;


public class GetProfileTask extends AsyncTask<Void, Void, ProfileResponse> {

    private OnGetProfileListener listener;

    public GetProfileTask(OnGetProfileListener onSetProfileListener) {
        this.listener = onSetProfileListener;
    }

    @Override
    protected ProfileResponse doInBackground(Void... voids) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", ValidateToken.getTokenString());

        if (isCancelled())
            return null;

        BaseResponse resp = Sender.sendGet(Network.BASE_URL + "api/profile", headers);
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
            listener.onGetProfile(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnGetProfileListener {
        void onGetProfile(ProfileResponse response);
    }
}
