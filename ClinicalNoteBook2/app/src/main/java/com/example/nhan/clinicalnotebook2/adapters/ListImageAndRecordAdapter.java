package com.example.nhan.clinicalnotebook2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nhan.clinicalnotebook2.R;
import com.example.nhan.clinicalnotebook2.activities.ActivityType;
import com.example.nhan.clinicalnotebook2.activities.ListImageAndRecordActivity;
import com.example.nhan.clinicalnotebook2.models.ImagePathObject;
import com.example.nhan.clinicalnotebook2.models.RecordPathObject;
import com.example.nhan.clinicalnotebook2.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nhan on 1/11/2017.
 */

public class ListImageAndRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ImagePathObject> listImagePath;
    private List<RecordPathObject> listRecordPath;
    private Context context;
    private View.OnClickListener onItemClickListener;
    private ActivityType activityType = ListImageAndRecordActivity.activityType;

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ListImageAndRecordAdapter(List<RecordPathObject> listRecordPath,
                                     List<ImagePathObject> listImagePath,
                                     Context context) {
        this.listImagePath = listImagePath;
        this.listRecordPath = listRecordPath;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (activityType == ActivityType.LIST_IMAGE) {
            View view = inflater.inflate(R.layout.item_list_image, parent, false);
            return new ListImageViewHolder(view);
        } else if (activityType == ActivityType.LIST_RECORD) {
            View view = inflater.inflate(R.layout.item_list_record, parent, false);
            return new ListRecordViewHolder(view);
        } else {
            Log.d("null", "onCreateViewHolder: null");
            return null;
        }
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (activityType == ActivityType.LIST_IMAGE) {
            ListImageViewHolder viewHolder = (ListImageViewHolder) holder;
            viewHolder.setData(listImagePath.get(position).getImagePath(), position);
            viewHolder.itemView.setOnClickListener(onItemClickListener);
        } else if (activityType == ActivityType.LIST_RECORD) {
            ListRecordViewHolder viewHolder = (ListRecordViewHolder) holder;
            viewHolder.setData("Record " + position, position);
            viewHolder.itemView.setOnClickListener(onItemClickListener);
        }

    }

    @Override
    public int getItemCount() {
        if (activityType == ActivityType.LIST_IMAGE)
            return listImagePath.size();
        else return listRecordPath.size();
    }

    class ListImageViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_image_list_item)
        ImageView thumbnail;
        public ListImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(String imagePath, int position){
            thumbnail.setImageBitmap(Utils.decodeImageFile(imagePath));
            itemView.setTag(position);
        }
    }

    class ListRecordViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.record_name)
        TextView recordName;
        public ListRecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(String name, int position){
            recordName.setText(name);
            itemView.setTag(position);
        }
    }


}
