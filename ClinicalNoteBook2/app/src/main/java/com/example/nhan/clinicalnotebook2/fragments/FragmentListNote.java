package com.example.nhan.clinicalnotebook2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nhan.clinicalnotebook2.R;
import com.example.nhan.clinicalnotebook2.adapters.ListNoteAdapter;
import com.example.nhan.clinicalnotebook2.database.RealmHandler;
import com.example.nhan.clinicalnotebook2.events.EventSendDataFolder;
import com.example.nhan.clinicalnotebook2.managers.ScreenManager;
import com.example.nhan.clinicalnotebook2.models.FolderObject;
import com.example.nhan.clinicalnotebook2.models.NoteObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by Nhan on 12/10/2016.
 */

public class FragmentListNote extends Fragment implements FragmentWithSearch{
    private RecyclerView recyclerView;
    private ListNoteAdapter listNoteAdapter;
    private RealmHandler realmHandle;
    private List<NoteObject> noteModelList;
    private FolderObject folderObject;

    public static FragmentListNote create(ScreenManager screenManager) {
        return new FragmentListNote();
    }

    public FragmentListNote() {
        realmHandle = RealmHandler.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_list_note_fragment);
        EventBus.getDefault().register(this);
//        setUpListNote();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.note);

        return view;
    }

    @Subscribe(sticky = true)
    public void setUpListNote(EventSendDataFolder event) {
        folderObject = realmHandle.getFolderFromRealmByName(event.getFolderName());
        noteModelList = realmHandle.getAllNoteListInFolder(folderObject);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        listNoteAdapter = new ListNoteAdapter(noteModelList);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
        recyclerView.setAdapter(listNoteAdapter);
    }

    @Override
    public void doSearch(String searchString) {
        List<NoteObject> foundNoteModelList = realmHandle.findNoteInFolderByName(folderObject, searchString);
        if (this.listNoteAdapter != null) {
            this.listNoteAdapter.reloadData(foundNoteModelList);
        }
    }

    @Override
    public void closeSearch() {
        listNoteAdapter.reloadData(noteModelList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
