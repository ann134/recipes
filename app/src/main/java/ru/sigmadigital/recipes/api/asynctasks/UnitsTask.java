package ru.sigmadigital.recipes.api.asynctasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.Sender;
import ru.sigmadigital.recipes.model.response.BaseResponse;
import ru.sigmadigital.recipes.model.response.UnitsResponse;


public class UnitsTask extends AsyncTask<Void, Void, List<UnitsResponse>> {

    private OnGetUnitsListener listener;

    public UnitsTask(OnGetUnitsListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<UnitsResponse> doInBackground(Void... voids) {
        Map<String, String> headers = new HashMap<>();

        if (isCancelled())
            return new ArrayList<>();

        BaseResponse resp = Sender.sendGet(Network.BASE_URL + "api/units", headers);
        if (resp != null && resp.getCode() == 200) {
            Type list = new TypeToken<ArrayList<UnitsResponse>>() {
            }.getType();
            return new Gson().fromJson(resp.getData(), list);
        } else {
            if (!isCancelled())
                App.showToast(R.string.disconnect);
        }
        return new ArrayList<>();
    }

    @Override
    protected void onPostExecute(List<UnitsResponse> baseResponse) {
        super.onPostExecute(baseResponse);
        if (listener != null) {
            listener.onGetUnits(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnGetUnitsListener {
        void onGetUnits(List<UnitsResponse> units);
    }
}
