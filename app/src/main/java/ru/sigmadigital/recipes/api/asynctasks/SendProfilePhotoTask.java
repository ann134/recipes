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

public class SendProfilePhotoTask extends AsyncTask<Void, Void, DoneResponse> {

    private InputStream inputStream;
    private OnSendProfilePhotoListener listener;

    public SendProfilePhotoTask(InputStream inputStream, OnSendProfilePhotoListener listener) {
        this.inputStream = inputStream;
        this.listener = listener;
    }

    @Override
    protected DoneResponse doInBackground(Void... voids) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", ValidateToken.getTokenString());
        headers.put("content-type", "image/*");

        if (isCancelled())
            return null;

        BaseResponse resp = Sender.sendPost(Network.BASE_URL + "api/profile/photo", inputStream, headers);
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
            listener.onSendPhoto(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnSendProfilePhotoListener {
        void onSendPhoto(DoneResponse response);
    }

}
