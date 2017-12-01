package com.example.nhan.clinicalnotebook2.models;

import io.realm.RealmObject;

/**
 * Created by Nhan on 3/17/2017.
 */

public class RecordPathObject extends RealmObject {
    private String recordPath;

    public RecordPathObject() {
    }

    public RecordPathObject(String imagePath) {

        this.recordPath = imagePath;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }
}
