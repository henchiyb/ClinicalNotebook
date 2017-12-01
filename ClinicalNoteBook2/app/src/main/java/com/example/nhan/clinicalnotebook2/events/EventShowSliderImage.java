package com.example.nhan.clinicalnotebook2.events;

import com.example.nhan.clinicalnotebook2.models.ImagePathObject;

import java.util.List;

/**
 * Created by Nhan on 1/12/2017.
 */

public class EventShowSliderImage {
    private List<ImagePathObject> listImagePaths;
    private int position;

    public EventShowSliderImage(List<ImagePathObject> listImagePaths, int position) {
        this.listImagePaths = listImagePaths;
        this.position = position;
    }

    public List<ImagePathObject> getListImagePaths() {
        return listImagePaths;
    }

    public int getPosition() {
        return position;
    }
}
