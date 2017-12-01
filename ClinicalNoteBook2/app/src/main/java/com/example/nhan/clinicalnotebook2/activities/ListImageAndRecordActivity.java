package com.example.nhan.clinicalnotebook2.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.nhan.clinicalnotebook2.R;
import com.example.nhan.clinicalnotebook2.adapters.ListImageAndRecordAdapter;
import com.example.nhan.clinicalnotebook2.events.EventOpenListImage;
import com.example.nhan.clinicalnotebook2.events.EventSendDataNote;
import com.example.nhan.clinicalnotebook2.events.EventShowSliderImage;
import com.example.nhan.clinicalnotebook2.fragments.SlideshowDialogFragment;
import com.example.nhan.clinicalnotebook2.models.ImagePathObject;
import com.example.nhan.clinicalnotebook2.models.NoteObject;
import com.example.nhan.clinicalnotebook2.models.RecordPathObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListImageAndRecordActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.rv_list_image)RecyclerView recyclerView;
    @BindView(R.id.tv_list_image_activity_info)TextView tvListImageInfo;

    public static ActivityType activityType;
    private NoteObject noteObject;
    private List<ImagePathObject> listImagePath = new ArrayList<>();
    private List<RecordPathObject> listRecordPath = new ArrayList<>();
    private ListImageAndRecordAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_image);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }
    @Subscribe(sticky = true)
    public void getDataEditNote(EventSendDataNote event){
        noteObject = event.getNoteObject();
        listImagePath = noteObject.getListImagePath();
        listRecordPath = noteObject.getListRecordPath();
        if (activityType == ActivityType.LIST_IMAGE){
            adapter = new ListImageAndRecordAdapter(listRecordPath, listImagePath,  this);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(this);
            if(listImagePath == null || listImagePath.size() == 0){
                tvListImageInfo.setVisibility(View.VISIBLE);
            } else
                tvListImageInfo.setVisibility(View.GONE);
        } else if (activityType == ActivityType.LIST_RECORD){
            adapter = new ListImageAndRecordAdapter(listRecordPath, listImagePath,  this);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(this);
            if(listRecordPath == null || listRecordPath.size() == 0){
                tvListImageInfo.setVisibility(View.VISIBLE);
                tvListImageInfo.setText(R.string.no_record);
            } else {
                tvListImageInfo.setVisibility(View.GONE);
            }
        }

    }
    @Subscribe(sticky = true)
    public void receiveDataFromNote(EventOpenListImage event){
        this.noteObject = event.getNoteObject();
        if (noteObject != null) {
            listImagePath = noteObject.getListImagePath();
            listRecordPath = noteObject.getListRecordPath();
        }
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        adapter = new ListImageAndRecordAdapter(listRecordPath, listImagePath,  this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        if(listImagePath == null || listImagePath.size() == 0){
            tvListImageInfo.setVisibility(View.VISIBLE);
        } else
            tvListImageInfo.setVisibility(View.GONE);
    }

    @Subscribe(sticky = true)

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        if (activityType == ActivityType.LIST_IMAGE){
            showSlideImage(view);
        } else if (activityType == ActivityType.LIST_RECORD) {
            playAudio(view);
        }
    }

    private void showSlideImage(View view){
        EventBus.getDefault().postSticky(new EventShowSliderImage(listImagePath,(int) view.getTag()));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        newFragment.show(ft, "slideshow");
    }

    private void playAudio(View view){
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(listRecordPath.get((int)view.getTag()).getRecordPath());
        intent.setDataAndType(Uri.fromFile(file), "audio/*");
        startActivity(Intent.createChooser(intent, null));
    }
}
