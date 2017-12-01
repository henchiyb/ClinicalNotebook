package com.example.nhan.clinicalnotebook2.models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Nhan on 1/3/2017.
 */

public class NoteObject extends RealmObject {
    private String name;
    private String folderName;
    private int ID;
    private RealmList<ContentNoteObject> listContent = new RealmList<>();
    private RealmList<ImagePathObject> listImagePath = new RealmList<>();
    private RealmList<RecordPathObject> listRecordPath = new RealmList<>();


    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public RealmList<ContentNoteObject> getListContent() {
        return listContent;
    }

    public void setListContent(RealmList<ContentNoteObject> listContent) {
        this.listContent = listContent;
    }

    public RealmList<ImagePathObject> getListImagePath() {
        return listImagePath;
    }

    public void setListImagePath(RealmList<ImagePathObject> listImagePath) {
        this.listImagePath = listImagePath;
    }

    public RealmList<RecordPathObject> getListRecordPath() {
        return listRecordPath;
    }

    public void setListRecordPath(RealmList<RecordPathObject> listRecordPath) {
        this.listRecordPath = listRecordPath;
    }
}
