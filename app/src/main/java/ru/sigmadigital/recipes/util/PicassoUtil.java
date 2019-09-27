package ru.sigmadigital.recipes.util;

import android.annotation.SuppressLint;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.annotations.EverythingIsNonNull;
import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.model.response.TokenResponse;

public class PicassoUtil {

    @SuppressLint("StaticFieldLeak")
    private static Picasso picasso;

    public static Picasso getPicasso() {
        if (picasso == null)
            picasso = picasso();
        return picasso;
    }


    public static void deletePicasso() {
        if (picasso != null){
            picasso.shutdown();
            picasso = picasso();
        }
    }

    private PicassoUtil() {
    }

    private static Picasso picasso() {
        OkHttpClient client = null;

        try {
            String token = "";
            TokenResponse tr = SettingHelper.getToken();
            if (tr != null) {
                token = tr.getToken();
            }
            final String stk = token;
            client = new OkHttpClient.Builder()

                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(@EverythingIsNonNull Chain chain) throws IOException {
                            Request newRequest = chain.request().newBuilder()
                                    .addHeader("token", stk)
                                    .build();
                            return chain.proceed(newRequest);
                        }


                    })
                    .build();
            return new Picasso.Builder(App.getAppContext())
                    .downloader(new OkHttp3Downloader(client))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Picasso.get();
    }
}
