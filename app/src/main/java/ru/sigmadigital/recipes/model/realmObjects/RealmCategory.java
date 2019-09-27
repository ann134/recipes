package ru.sigmadigital.recipes.model.realmObjects;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.sigmadigital.recipes.model.response.CategoryResponse;


public class RealmCategory extends RealmObject implements Serializable {

    @PrimaryKey
    private long id;
    private RealmCategory parent;
    private String name;
    private String action;

    public RealmCategory() {
    }

    RealmCategory(CategoryResponse categoryResponse) {
        this.id = categoryResponse.getId();
        if (categoryResponse.getParent() == null) {
            this.parent = null;
        } else {
            this.parent = new RealmCategory(categoryResponse.getParent());
        }
        this.name = categoryResponse.getName();
        this.action = categoryResponse.getAction();
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setParent(RealmCategory parent) {
        this.parent = parent;
    }

    public RealmCategory getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    CategoryResponse toCategory() {
        CategoryResponse category = new CategoryResponse();
        category.setId(this.getId());
        category.setAction(this.getAction());
        category.setName(this.getName());
        if (this.getParent() != null) {
            category.setParent(this.getParent().toCategory());
        }
        return category;

    }
}
