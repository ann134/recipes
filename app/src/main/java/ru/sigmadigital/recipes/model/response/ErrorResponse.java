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

public class ErrorResponse implements Serializable {

    private int code;
    private String description;

    /*public ErrorResponse(int code) {
        this.code = code;
    }

    public ErrorResponse(int code, String description) {
        this.code = code;
        this.description = description;
    }*/

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
