/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.sigmadigital.recipes.model.request;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dima
 */

public class ProfileRequest extends Request implements Serializable {

    private String name;
    private String city;
    private int gender;
    private Date bdate;

    public ProfileRequest() {
    }

   /* public ProfileRequest(String name, String city, int gender, Date bdate) {
        this.name = name;
        this.city = city;
        this.gender = gender;
        this.bdate = bdate;
    }*/


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setBdate(Date bdate) {
        this.bdate = bdate;
    }

}
