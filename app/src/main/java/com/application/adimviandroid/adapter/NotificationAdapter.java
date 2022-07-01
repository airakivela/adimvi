package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.types.NotificationType;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<JSONObject> mList = new ArrayList<>();
    private OnClickNotificationCellListener mListener;

    public NotificationAdapter(Context mContext, List<JSONObject> mList, OnClickNotificationCellListener mListener) {
        this.mContext = mContext;
        this.mList = mList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject object = mList.get(position);
        try {
            holder.imgNotification.setImageResource(NotificationType.getValFromKey(object.getString("type")));
            int readStatus = 0;
            try {
                readStatus = object.getInt("readStatus");
            } catch (JSONException e) {
                readStatus = 0;
            }
            holder.imgNotiSeenMarker.setVisibility(readStatus == 1 ? View.GONE : View.VISIBLE);
            holder.txtNotiUser.setText(object.getString("msg"));
            holder.txtNotiTime.setText(object.getString("created"));
            holder.txtNotiTitle.setText(object.getString("title"));
            holder.imgVerify.setVisibility(object.getInt("verify") == 1 ? View.VISIBLE : View.GONE);
            holder.txtUserName.setText(object.getString("username"));
            if (object.getString("avatarblobid").isEmpty()) {
                holder.imgUser.setImageResource(R.drawable.ic_user_placehoder);
            } else {
                AppUtil.loadImageByUrl(mContext, holder.imgUser, ApiUtil.ImageUrl + object.getString("avatarblobid"), ImagePlaceHolderType.USERIMAGE);
            }
            holder.itemView.setOnClickListener(v -> mListener.onClickNotificationCell(object));
            holder.imgUser.setOnClickListener(v -> {
                try {
                    mListener.onClickUserProfile(object.getInt("eventuserID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgNotification, imgNotiSeenMarker, imgUser, imgVerify;
        public TextView txtNotiUser, txtNotiTitle, txtNotiTime, txtUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNotification = itemView.findViewById(R.id.imgNotification);
            imgNotiSeenMarker = itemView.findViewById(R.id.imgSeenMarker);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgVerify = itemView.findViewById(R.id.imgVerify);
            txtNotiUser = itemView.findViewById(R.id.txtNotificaitonUser);
            txtNotiTitle = itemView.findViewById(R.id.txtNotificationTitle);
            txtNotiTime = itemView.findViewById(R.id.txtNotificationTime);
            txtUserName = itemView.findViewById(R.id.txtUserName);
        }
    }

    public interface OnClickNotificationCellListener {
        void onClickNotificationCell(JSONObject object);
        void onClickUserProfile(int userID);
    }
}
