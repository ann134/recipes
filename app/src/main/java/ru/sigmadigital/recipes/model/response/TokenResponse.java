/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.sigmadigital.recipes.model.response;

import java.io.Serializable;


/**
 * @author dima
 */


public class TokenResponse extends Response implements Serializable {

    private String token;
    private long time;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public boolean isValidate() {
        long currentTime = System.currentTimeMillis();
        long diff = (currentTime - time) / 1000 / 60 / 60;
        return diff < 4;
    }
}
