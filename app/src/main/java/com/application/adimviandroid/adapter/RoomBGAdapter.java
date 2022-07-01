package com.application.adimviandroid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.RoomBGModel;

import java.util.List;

public class RoomBGAdapter extends RecyclerView.Adapter<RoomBGAdapter.ViewHolder> {

    private Context mContext;
    private RoomBGListener mListener;
    private List<RoomBGModel> mModels;

    public RoomBGAdapter(Context context, List<RoomBGModel> models, RoomBGListener listener) {
        this.mContext = context;
        this.mModels = models;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_room_bg, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomBGModel model = mModels.get(position);
        if (position == mModels.size() - 1) {
            holder.lltTrail.setVisibility(View.VISIBLE);
        } else {
            holder.lltTrail.setVisibility(View.GONE);
        }
        if (model.isSelected) {
            holder.crdBG.setCardBackgroundColor(mContext.getColor(R.color.mainGreen));
        } else {
            holder.crdBG.setCardBackgroundColor(Color.parseColor("#00000000"));
        }
        holder.imgBG.setImageResource(model.bgResID);
        holder.itemView.setOnClickListener(v -> mListener.onClickRoomBTCell(position));
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public interface RoomBGListener{
        void onClickRoomBTCell(int index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView crdBG;
        public ImageView imgBG;
        public LinearLayout lltTrail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            crdBG = itemView.findViewById(R.id.crdBG);
            imgBG = itemView.findViewById(R.id.imgBG);
            lltTrail = itemView.findViewById(R.id.lltTrail);
        }
    }
}
