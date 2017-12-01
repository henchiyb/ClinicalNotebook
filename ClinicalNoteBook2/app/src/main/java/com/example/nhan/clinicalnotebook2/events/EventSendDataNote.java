package com.example.nhan.clinicalnotebook2.events;

import com.example.nhan.clinicalnotebook2.managers.FragmentType;
import com.example.nhan.clinicalnotebook2.models.NoteObject;

/**
 * Created by Nhan on 1/3/2017.
 */
public class EventSendDataNote {
    private NoteObject noteObject;
    private FragmentType dataType;

    public NoteObject getNoteObject() {
        return noteObject;
    }

    public EventSendDataNote(NoteObject noteObject, FragmentType dataType) {
        this.noteObject = noteObject;
        this.dataType = dataType;
    }

    public FragmentType getDataType() {
        return dataType;
    }
}
