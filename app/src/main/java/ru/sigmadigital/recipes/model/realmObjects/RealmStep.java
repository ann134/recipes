package ru.sigmadigital.recipes.model.realmObjects;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.sigmadigital.recipes.model.response.StepResponse;

public class RealmStep extends RealmObject implements Serializable {

    @PrimaryKey
    private long id;
    private String action;
    private int position;

    public RealmStep() {
    }

    RealmStep(StepResponse step) {
        this.id = step.getId();
        this.action = step.getAction();
        this.position = step.getPosition();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    StepResponse toStep() {
        StepResponse step = new StepResponse(this.getAction(), this.getPosition());
        step.setId(this.getId());
        return step;
    }
}

