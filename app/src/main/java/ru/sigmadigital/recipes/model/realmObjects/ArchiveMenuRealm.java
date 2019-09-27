package ru.sigmadigital.recipes.model.realmObjects;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ArchiveMenuRealm extends RealmObject implements Serializable {

    @PrimaryKey
    private long id;
    private RealmList<PreparedMenuRealm> preparedMenus;

    public ArchiveMenuRealm() {
        preparedMenus = new RealmList<>();
    }

    /*public ArchiveMenuRealm(RealmList<PreparedMenuRealm> preparedMenus) {
        this.preparedMenus = preparedMenus;
    }*/


    public void setPreparedMenus(RealmList<PreparedMenuRealm> preparedMenus) {
        this.preparedMenus = preparedMenus;
    }

    public void addPreparedMenu(PreparedMenuRealm preparedMenu) {
        this.preparedMenus.add(preparedMenu);
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public RealmList<PreparedMenuRealm> getPreparedMenus() {
        return preparedMenus;
    }
}
