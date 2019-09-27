package ru.sigmadigital.recipes.api.asynctasks;

import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.Sender;
import ru.sigmadigital.recipes.model.User;
import ru.sigmadigital.recipes.model.request.RegistrationRequest;
import ru.sigmadigital.recipes.model.response.BaseResponse;
import ru.sigmadigital.recipes.model.response.TokenResponse;
import ru.sigmadigital.recipes.util.SettingHelper;

public class ValidateToken {

    //применять только внутри асинктаска
    public static TokenResponse getToken() {

        if (SettingHelper.getToken() != null && SettingHelper.getToken().isValidate()) {
            return SettingHelper.getToken();
        } else {
            User user = SettingHelper.getUser();

            RegistrationRequest reqest = new RegistrationRequest();
            if (user != null) {
                reqest.setEmail(user.getEmail());
                reqest.setPassword(user.getPassword());
            }

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-type", "application/json");
            BaseResponse resp = Sender.sendPost(Network.BASE_URL + "api/autorization", reqest.toString(), headers);
            if (resp != null && resp.getCode() == 200) {
                TokenResponse token = TokenResponse.fromJson(resp.getData(), TokenResponse.class);
                if (token != null) {
                    token.setTime(System.currentTimeMillis());
                    SettingHelper.setToken(token);
                    return token;
                }
            } /*else {
                App.showToast(R.string.disconnect);
            }*/
            return null;
        }
    }

    public static String getTokenString(){
        TokenResponse tr = getToken();
        if(tr != null){
            return tr.getToken() != null ? tr.getToken() : "";
        }else{
            return "";
        }
    }

}
