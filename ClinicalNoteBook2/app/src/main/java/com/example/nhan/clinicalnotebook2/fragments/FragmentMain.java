package com.example.nhan.clinicalnotebook2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nhan.clinicalnotebook2.R;
import com.example.nhan.clinicalnotebook2.events.EventShowSearchIcon;
import com.example.nhan.clinicalnotebook2.managers.FragmentType;
import com.example.nhan.clinicalnotebook2.managers.ScreenManager;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Nhan on 2/27/2017.
 */

public class FragmentMain extends Fragment {

    private ScreenManager screenManager;
    private AppCompatActivity activity;
    @BindView(R.id.btn_note)Button btnNote;
    @BindView(R.id.btn_image)Button btnImage;
    @BindView(R.id.btn_voice)Button btnVoice;

    @OnClick(R.id.btn_note)
    public void onClickBtnNote(){
        EventBus.getDefault().post(new EventShowSearchIcon());
        screenManager.openFragment(new FragmentListFolder(), true);
        ScreenManager.setCurrentFragment(FragmentType.FOLDER_NOTE);
    }
    @OnClick(R.id.btn_image)
    public void onClickBtnImage(){
        EventBus.getDefault().post(new EventShowSearchIcon());
        screenManager.openFragment(new FragmentListFolder(), true);
        ScreenManager.setCurrentFragment(FragmentType.FOLDER_IMAGE);
    }
    @OnClick(R.id.btn_voice)
    public void onClickBtnVoice(){
        EventBus.getDefault().post(new EventShowSearchIcon());
        screenManager.openFragment(new FragmentListFolder(), true);
        ScreenManager.setCurrentFragment(FragmentType.FOLDER_RECORD);
    }
    public static FragmentMain create(ScreenManager screenManager) {
        return new FragmentMain();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        screenManager = new ScreenManager(getActivity().getSupportFragmentManager(), R.id.container);
        ButterKnife.bind(this, view);
        activity = (AppCompatActivity) getActivity();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ScreenManager.getCurrentFragment() == FragmentType.MAIN) {
            activity.getSupportActionBar().hide();
        }
    }
}
