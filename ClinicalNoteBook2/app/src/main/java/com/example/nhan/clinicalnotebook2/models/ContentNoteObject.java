package com.example.nhan.clinicalnotebook2.models;

import io.realm.RealmObject;

/**
 * Created by Nhan on 1/13/2017.
 */

public class ContentNoteObject extends RealmObject {
    private String name;
    private String content;

    public ContentNoteObject() {
    }

    public ContentNoteObject(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
