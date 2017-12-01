package com.example.nhan.clinicalnotebook2.events;

import com.example.nhan.clinicalnotebook2.models.NoteObject;

/**
 * Created by Nhan on 1/11/2017.
 */

public class EventOpenListImage {
    private NoteObject noteObject;

    public NoteObject getNoteObject() {
        return noteObject;
    }

    public EventOpenListImage(NoteObject noteObject) {
        this.noteObject = noteObject;
    }

    public EventOpenListImage() {

    }
}
