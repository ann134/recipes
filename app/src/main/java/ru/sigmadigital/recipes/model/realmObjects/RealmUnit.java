package ru.sigmadigital.recipes.model.realmObjects;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.sigmadigital.recipes.model.response.UnitsResponse;

public class RealmUnit extends RealmObject implements Serializable {

    @PrimaryKey
    private long id;
    private String name;

    public RealmUnit() {
    }

    public RealmUnit(UnitsResponse unit) {
        if (unit != null) {
            this.id = unit.getId();
            this.name = unit.getName();
        }
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

    public UnitsResponse toUnits() {
        UnitsResponse rsp = new UnitsResponse();
        rsp.setId(id);
        rsp.setName(name);
        return rsp;
    }
}