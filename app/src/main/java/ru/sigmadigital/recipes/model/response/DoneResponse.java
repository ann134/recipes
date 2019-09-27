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


public class DoneResponse extends Response implements Serializable {

    private boolean result = false;

    /*public DoneResponse() {
    }

    public DoneResponse(boolean result) {
        this.result = result;
    }*/

    public boolean isResult() {
        return result;
    }
}
