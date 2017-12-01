package com.example.nhan.clinicalnotebook2.events;

import com.example.nhan.clinicalnotebook2.managers.FragmentType;

/**
 * Created by Nhan on 12/11/2016.
 */
public class EventOpenListNote {
    private FragmentType fragmentType;

    public EventOpenListNote(FragmentType fragmentType) {
        this.fragmentType = fragmentType;
    }

    public FragmentType getFragmentType() {
        return fragmentType;
    }
}
