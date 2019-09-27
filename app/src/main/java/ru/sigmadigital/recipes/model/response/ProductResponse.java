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

public class ProductResponse extends Response implements Serializable {

    private long id;
    private String name;
    private UnitsResponse units;
    private String count;
    private boolean purchased;

    public ProductResponse(){
    }

    public ProductResponse(String name, UnitsResponse unit, String count) {
        this.name = name;
        this.units = unit;
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UnitsResponse getUnits() {
        return units;
    }

    public void setUnits(UnitsResponse unit) {
        this.units = unit;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}
