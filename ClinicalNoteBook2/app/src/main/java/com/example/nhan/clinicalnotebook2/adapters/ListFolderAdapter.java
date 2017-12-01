package com.example.nhan.clinicalnotebook2.adapters;

import android.content.Context;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nhan.clinicalnotebook2.R;
import com.example.nhan.clinicalnotebook2.activities.MainActivity;
import com.example.nhan.clinicalnotebook2.database.RealmHandler;
import com.example.nhan.clinicalnotebook2.managers.FragmentType;
import com.example.nhan.clinicalnotebook2.managers.ScreenManager;
import com.example.nhan.clinicalnotebook2.models.FolderObject;

import java.util.List;



/**
 * Created by Nhan on 12/10/2016.
 */

public class ListFolderAdapter extends RecyclerView.Adapter<ListFolderAdapter.ListFolderViewHolder> {

    private List<FolderObject> listFolder;
    private View.OnClickListener onItemClickListener;
    private RealmHandler realmHandle = RealmHandler.getInstance();
    private ActionMode actionMode;
    private Context context;

    public ListFolderAdapter(List<FolderObject> listFolder) {
        this.listFolder = listFolder;
    }

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void reloadData(List<FolderObject> folderObjects) {
        this.listFolder = folderObjects;
        this.notifyDataSetChanged();
    }
    @Override
    public ListFolderAdapter.ListFolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_list_folder, parent, false);
        context = parent.getContext();
        return new ListFolderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListFolderAdapter.ListFolderViewHolder holder, int position) {
        holder.itemView.setOnClickListener(onItemClickListener);
        holder.setData(listFolder.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                final int position = holder.getAdapterPosition();
                if (actionMode != null) return false;
                actionMode = ((MainActivity) context).startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.context_menu, menu);
                        mode.setTitle(listFolder.get(position).getName());
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
                                    realmHandle.deleteFolderInRealm(listFolder.get(position));
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

    }

    @Override
    public int getItemCount() {
        return listFolder.size();
    }

    public class ListFolderViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNameFolder;
        public ListFolderViewHolder(View itemView) {
            super(itemView);
            tvNameFolder = (TextView) itemView.findViewById(R.id.folder_name);
        }

        public void setData(FolderObject folder){
            tvNameFolder.setText(folder.getName());
            itemView.setTag(folder);
        }
    }
}
