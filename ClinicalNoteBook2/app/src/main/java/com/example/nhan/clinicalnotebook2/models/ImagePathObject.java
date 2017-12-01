package com.example.nhan.clinicalnotebook2.models;

import io.realm.RealmObject;

/**
 * Created by Nhan on 1/13/2017.
 */

public class ImagePathObject extends RealmObject {
    private String imagePath;

    public ImagePathObject() {
    }

    public ImagePathObject(String imagePath) {

        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
