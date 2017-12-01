package com.example.nhan.clinicalnotebook2.models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Nhan on 1/12/2017.
 */

public class CacheObject extends RealmObject {
    private RealmList<ImagePathObject> listImagePathNotSave = new RealmList<>();

    public RealmList<ImagePathObject> getListImagePathNotSave() {
        return listImagePathNotSave;
    }

    public void setListImagePathNotSave(RealmList<ImagePathObject> listImagePathNotSave) {
        this.listImagePathNotSave = listImagePathNotSave;
    }
}
