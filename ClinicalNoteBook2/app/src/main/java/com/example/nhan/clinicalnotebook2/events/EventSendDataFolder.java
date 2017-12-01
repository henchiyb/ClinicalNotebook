package com.example.nhan.clinicalnotebook2.events;

/**
 * Created by Nhan on 12/11/2016.
 */

public class EventSendDataFolder {
    private String folderName;
    public EventSendDataFolder(String folderName) {
        this.folderName = folderName;
    }
    public String getFolderName() {
        return folderName;
    }
}
