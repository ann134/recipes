package ru.sigmadigital.recipes.model.realmObjects;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.sigmadigital.recipes.model.response.ProductResponse;

public class RealmProduct extends RealmObject implements Serializable {

    @PrimaryKey
    private long id;
    private String name;
    private RealmUnit unit;
    private String count;
    private boolean purchased;

    public RealmProduct() {
    }

    RealmProduct(ProductResponse product) {
        this.id = product.getId();
        this.name = product.getName();
        this.unit = new RealmUnit(product.getUnits());
        this.count = product.getCount();
        this.purchased = product.isPurchased();
    }

    public RealmUnit getUnit() {
        return unit;
    }

    public void setUnit(RealmUnit unit) {
        this.unit = unit;
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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    private boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    ProductResponse toProduct() {
        ProductResponse product = new ProductResponse();
        product.setId(this.getId());
        product.setCount(this.getCount());
        product.setName(this.getName());
        product.setUnits(this.getUnit().toUnits());
        product.setPurchased(this.isPurchased());
        return product;
    }
}