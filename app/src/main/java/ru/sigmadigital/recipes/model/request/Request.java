/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.sigmadigital.recipes.model.request;

import com.google.gson.Gson;

import java.io.Serializable;

import ru.sigmadigital.recipes.util.GsonHelper;

/**
 * @author dima
 */

public class Request implements Serializable {

    public Request() {
    }

    @Override
    public String toString() {
        return GsonHelper.getGson().toJson(this);
    }


    public static <T extends Request> T fromJson(String json, Class<T> classOfT) {
        try {
            return new Gson().fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
