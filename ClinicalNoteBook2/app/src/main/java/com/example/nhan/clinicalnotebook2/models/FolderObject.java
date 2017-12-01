package com.example.nhan.clinicalnotebook2.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nhan on 1/3/2017.
 */

public class FolderObject extends RealmObject {
    @PrimaryKey private String name;
    private int ID;
    private RealmList<NoteObject> listNote = new RealmList<>();

    public FolderObject() {
    }

    public FolderObject(String name, int ID, RealmList<NoteObject> listNote) {
        this.name = name;
        this.ID = ID;
        this.listNote = listNote;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<NoteObject> getListNote() {
        return listNote;
    }

    public void setListNote(RealmList<NoteObject> listNote) {
        this.listNote = listNote;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
