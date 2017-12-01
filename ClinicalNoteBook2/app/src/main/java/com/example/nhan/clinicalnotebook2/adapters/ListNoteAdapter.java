package com.example.nhan.clinicalnotebook2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.nhan.clinicalnotebook2.R;
import com.example.nhan.clinicalnotebook2.activities.ActivityType;
import com.example.nhan.clinicalnotebook2.activities.ListImageAndRecordActivity;
import com.example.nhan.clinicalnotebook2.activities.MainActivity;
import com.example.nhan.clinicalnotebook2.activities.NoteActivity;
import com.example.nhan.clinicalnotebook2.database.RealmHandler;
import com.example.nhan.clinicalnotebook2.events.EventSendDataNote;
import com.example.nhan.clinicalnotebook2.managers.FragmentType;
import com.example.nhan.clinicalnotebook2.managers.ScreenManager;
import com.example.nhan.clinicalnotebook2.models.NoteObject;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class ListNoteAdapter extends RecyclerView.Adapter<ListNoteAdapter.RecyclerViewHolder> {
    private Animation animation;
    private Context context;
    private Intent intent;
    private List<NoteObject> listNote;
    private ActionMode actionMode;
    private RealmHandler realmHandle;

    public ListNoteAdapter(List<NoteObject> listNote) {
        this.listNote = listNote;
    }
    public void reloadData(List<NoteObject> noteObjectList) {
        this.listNote = noteObjectList;
        this.notifyDataSetChanged();
    }
    @Override
    public ListNoteAdapter.RecyclerViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        realmHandle = RealmHandler.getInstance();
        context = parent.getContext();

        animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.zoom_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationUtils.loadAnimation(parent.getContext(), R.anim.zoom_out);
                context.startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        LayoutInflater inflater = LayoutInflater.from(context);
        //
        final View view = inflater.inflate(R.layout.item_list_note, parent, false);
        //
        final RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ScreenManager.getCurrentFragment() == FragmentType.LIST_NOTE){
                    intent = new Intent(context, NoteActivity.class);
                    int position = recyclerViewHolder.getAdapterPosition();
                    NoteActivity.activityType = ActivityType.EDIT_NOTE;
                    EventBus.getDefault().postSticky(new EventSendDataNote(listNote.get(position), FragmentType.LIST_NOTE));
                } else if (ScreenManager.getCurrentFragment() == FragmentType.LIST_NOTE_ONLY_IMAGE){
                    intent = new Intent(context, ListImageAndRecordActivity.class);
                    int position = recyclerViewHolder.getAdapterPosition();
                    ListImageAndRecordActivity.activityType = ActivityType.LIST_IMAGE;
                    EventBus.getDefault().postSticky(new EventSendDataNote(listNote.get(position), FragmentType.LIST_NOTE_ONLY_IMAGE));
                } else if (ScreenManager.getCurrentFragment() == FragmentType.LIST_NOTE_ONLY_RECORD){
                    intent = new Intent(context, ListImageAndRecordActivity.class);
                    int position = recyclerViewHolder.getAdapterPosition();
                    ListImageAndRecordActivity.activityType = ActivityType.LIST_RECORD;
                    EventBus.getDefault().postSticky(new EventSendDataNote(listNote.get(position), FragmentType.LIST_NOTE_ONLY_RECORD));
                }
                view.startAnimation(animation);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                final int position = recyclerViewHolder.getAdapterPosition();
                if (actionMode != null) return false;
                actionMode = ((MainActivity) context).startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.context_menu, menu);
                        mode.setTitle(listNote.get(position).getName());
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.context_menu_delete:
                                FragmentType currentFragment = ScreenManager.getCurrentFragment();
                                if (currentFragment == FragmentType.LIST_NOTE) {
                                    realmHandle.deleteNoteFromRealm(listNote.get(position));
                                }
                                notifyItemRemoved(position);
                                mode.finish();
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        actionMode = null;
                    }
                });
                return true;
            }
        });
        return recyclerViewHolder;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ListNoteAdapter.RecyclerViewHolder holder, int position) {
        NoteObject noteModel = listNote.get(position);
        holder.itemTitle.setText(noteModel.getName());
        holder.itemImageNumber.setText(String.format("Image: %d", noteModel.getListImagePath().size()));
        holder.itemRecordNumber.setText(String.format("Record: %d", noteModel.getListRecordPath().size()));
    }

    @Override
    public int getItemCount() {
        return listNote.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle, itemImageNumber, itemRecordNumber;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemImageNumber = (TextView) itemView.findViewById(R.id.item_image_number);
            itemRecordNumber = (TextView) itemView.findViewById(R.id.item_record_number);
        }
    }
}
