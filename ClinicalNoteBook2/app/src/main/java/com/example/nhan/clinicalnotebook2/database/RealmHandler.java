package com.example.nhan.clinicalnotebook2.database;

import com.example.nhan.clinicalnotebook2.models.CacheObject;
import com.example.nhan.clinicalnotebook2.models.ContentNoteObject;
import com.example.nhan.clinicalnotebook2.models.FolderObject;
import com.example.nhan.clinicalnotebook2.models.ImagePathObject;
import com.example.nhan.clinicalnotebook2.models.NoteObject;
import com.example.nhan.clinicalnotebook2.models.RecordPathObject;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;

/**
 * Created by Nhan on 1/3/2017.
 */

public class RealmHandler {
    private static RealmHandler instance;
    private Realm realm;

    public static RealmHandler getInstance() {
        if (instance == null)
            instance = new RealmHandler();
        return instance;
    }

    private RealmHandler(){
        realm = Realm.getDefaultInstance();
    }

    public void addFolderToRealm(FolderObject folderObject){
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(folderObject);
        realm.commitTransaction();
    }
    public void addNoteToFolder(NoteObject noteObject, FolderObject folderObject){
        realm.beginTransaction();
        folderObject.getListNote().add(noteObject);
        realm.commitTransaction();
    }

    public void addFolderNameToNote(NoteObject noteObject, String folderName){
        realm.beginTransaction();
        noteObject.setFolderName(folderName);
        realm.commitTransaction();
    }

    public void removeContentInNote(NoteObject noteObject, int position){
        realm.beginTransaction();
        noteObject.getListContent().remove(position);
        realm.commitTransaction();
    }
    public void addCacheToRealm(CacheObject cacheObject){
        realm.beginTransaction();
        realm.copyToRealm(cacheObject);
        realm.commitTransaction();
    }
    public void addImagePathToCache(CacheObject cacheObject, String imagePath){
        realm.beginTransaction();
        cacheObject.getListImagePathNotSave().add(new ImagePathObject(imagePath));
        realm.commitTransaction();
    }

    public List<FolderObject> getAllFolderInRealm() {
        return realm.where(FolderObject.class).findAll();
    }
    public FolderObject getFolderFromRealmByID(int ID){
        return realm.where(FolderObject.class).equalTo("ID", ID).findFirst();
    }
    public FolderObject getFolderFromRealmByName(String name){
        return realm.where(FolderObject.class).equalTo("name", name).findFirst();
    }

    public void deleteFolderInRealm(FolderObject folder){
        realm.beginTransaction();
        folder.deleteFromRealm();
        realm.commitTransaction();
    }

    public List<NoteObject> getAllNoteInRealm() {
        return realm.where(NoteObject.class).findAll();
    }
    public List<NoteObject> getAllNoteListInFolder(FolderObject folder) {
        return folder.getListNote();
    }
    public List<NoteObject> findNoteInFolderByName(FolderObject folderObject, String searchString) {

        return folderObject.getListNote().where()
                .contains("name", searchString, Case.INSENSITIVE)
                .findAll();
    }
    public List<FolderObject> findFolderByName(String searchString) {
        return realm.where(FolderObject.class)
                .contains("name", searchString, Case.INSENSITIVE)
                .findAll();
    }
    public void editContentNote(NoteObject noteObject, int presentID, String content){
        realm.beginTransaction();
        noteObject.getListContent().get(presentID).setContent(content);
        realm.commitTransaction();
    }
    public void editContentName(NoteObject noteObject, int presentID, String nameContent){
        realm.beginTransaction();
        noteObject.getListContent().get(presentID).setName(nameContent);
        realm.commitTransaction();
    }
    public void editNameNote(NoteObject noteObject, String name){
        realm.beginTransaction();
        noteObject.setName(name);
        realm.commitTransaction();
    }
    public void addImagePathNote(NoteObject noteObject, String imagePath){
        realm.beginTransaction();
        noteObject.getListImagePath().add(new ImagePathObject(imagePath));
        realm.commitTransaction();
    }

    public void addRecordPathNote(NoteObject noteObject, String recordPath){
        realm.beginTransaction();
        noteObject.getListRecordPath().add(new RecordPathObject(recordPath));
        realm.commitTransaction();
    }

    public void addContentToNote(NoteObject noteObject,String nameContent, String content){
        realm.beginTransaction();
        noteObject.getListContent().add(new ContentNoteObject(nameContent, content));
        realm.commitTransaction();

    }

    public void updateContentInNote(NoteObject noteObject, int id, String content){
        realm.beginTransaction();
        noteObject.getListContent().get(id).setContent(content);
        realm.commitTransaction();
    }

    public void deleteNoteFromRealm(NoteObject noteObject) {
        realm.beginTransaction();
        noteObject.deleteFromRealm();
        realm.commitTransaction();
    }

    public List<ImagePathObject> getAllImageInRealm(){
        return realm.where(ImagePathObject.class).findAll();
    }



}
