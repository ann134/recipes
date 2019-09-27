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

public class BaseResponse implements Serializable {

    private String data;
    private int code;

    public BaseResponse(String data, int code) {
        this.data = data;
        this.code = code;
    }

    public BaseResponse(int code) {
        this.code = code;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

}
