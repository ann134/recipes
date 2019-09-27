/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.sigmadigital.recipes.model.request;

import java.io.Serializable;

/**
 * @author dima
 */

public class RegistrationRequest extends Request implements Serializable {

    private String email;
    private String password;
    private String name;


    public RegistrationRequest() {
    }

    /*public RegistrationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }*/

   /* public RegistrationRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }*/


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
