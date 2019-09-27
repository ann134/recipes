/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.sigmadigital.recipes.model.response;

import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;

import ru.sigmadigital.recipes.util.GsonHelper;

/**
 * @author dima
 */

public class Response implements Serializable {

    private ErrorResponse error = null;

    public void setError(ErrorResponse error) {
        this.error = error;
    }

    public ErrorResponse getError() {
        return error;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static <T extends Response> T fromJson(String json, Class<T> classOfT) {
        try {
            return GsonHelper.getGson().fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends Response> T fromJson(String json, Type type) {
        try {
            return GsonHelper.getGson().fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
