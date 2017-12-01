package com.example.nhan.clinicalnotebook2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.example.nhan.clinicalnotebook2.R;
import com.example.nhan.clinicalnotebook2.adapters.ListFolderAdapter;
import com.example.nhan.clinicalnotebook2.database.RealmHandler;
import com.example.nhan.clinicalnotebook2.events.EventOpenListNote;
import com.example.nhan.clinicalnotebook2.events.EventSendDataFolder;
import com.example.nhan.clinicalnotebook2.managers.FragmentType;
import com.example.nhan.clinicalnotebook2.managers.ScreenManager;
import com.example.nhan.clinicalnotebook2.models.FolderObject;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Nhan on 12/10/2016.
 */

public class FragmentListFolder extends Fragment implements FragmentWithSearch, View.OnClickListener {

    private RecyclerView recyclerView;
    private ListFolderAdapter adapter;
    private GridLayoutManager layoutManager;
    private RealmHandler realmHandle;
    private List<FolderObject> folderObjectList = RealmHandler.getInstance().getAllFolderInRealm();
    public static FragmentListFolder create(ScreenManager screenManager) {
        return new FragmentListFolder();
    }

    public FragmentListFolder() {
        realmHandle = RealmHandler.getInstance();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        layoutManager = new GridLayoutManager(this.getContext(),
                3,
                GridLayout.VERTICAL,
                false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (ScreenManager.getCurrentFragment() == FragmentType.FOLDER_NOTE)
            activity.getSupportActionBar().setTitle("Note Folder");
        else if (ScreenManager.getCurrentFragment() == FragmentType.FOLDER_IMAGE)
            activity.getSupportActionBar().setTitle("Image Folder");
        else if (ScreenManager.getCurrentFragment() == FragmentType.FOLDER_RECORD)
            activity.getSupportActionBar().setTitle("Record Folder");
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_list_folder_fragment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ListFolderAdapter(folderObjectList);
        adapter.setOnItemClickListener(this);
        recyclerView.swapAdapter(adapter, true);
        adapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void doSearch(String searchString) {
        List<FolderObject> foundFolderList = realmHandle.findFolderByName(searchString);
        if (this.adapter != null) {
            this.adapter.reloadData(foundFolderList);
        }
    }


    @Override
    public void closeSearch() {
        this.adapter.reloadData(folderObjectList);
    }


    @Override
    public void onClick(View view) {
        FolderObject folderModel = (FolderObject) view.getTag();
        EventBus.getDefault().postSticky(new EventSendDataFolder(folderModel.getName()));
        if(ScreenManager.getCurrentFragment() == FragmentType.FOLDER_NOTE) {
            EventBus.getDefault().post(new EventOpenListNote(FragmentType.LIST_NOTE));
        } else if (ScreenManager.getCurrentFragment() == FragmentType.FOLDER_IMAGE){
            EventBus.getDefault().post(new EventOpenListNote(FragmentType.LIST_NOTE_ONLY_IMAGE));
        } else if (ScreenManager.getCurrentFragment() == FragmentType.FOLDER_RECORD){
            EventBus.getDefault().post(new EventOpenListNote(FragmentType.LIST_NOTE_ONLY_RECORD));
        }


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
