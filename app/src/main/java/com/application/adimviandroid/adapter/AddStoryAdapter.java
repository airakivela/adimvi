package com.application.adimviandroid.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.AddStoryModel;
import java.util.ArrayList;
import java.util.List;

public class AddStoryAdapter extends RecyclerView.Adapter<AddStoryAdapter.ViewHolder> {

    private Context mContext;
    private List<AddStoryModel> mStories = new ArrayList<>();
    private OnAddStoryItemListener mListener;


    public AddStoryAdapter(Context context, List<AddStoryModel> mStories, OnAddStoryItemListener mListener) {
        this.mContext = context;
        this.mStories = mStories;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_story_add, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddStoryModel model = mStories.get(position);
        holder.txtStoryNumber.setText((position + 1) + "/" + mStories.size());
        holder.edtContent.setText(model.content);
        if (position == 0) {
            holder.edtContent.setTextSize(24);
            holder.edtContent.setHint("Edita tu título...");
            holder.edtContent.setTypeface(null, Typeface.BOLD);
            holder.edtContent.setLines(3);
            holder.edtContent.setMaxLines(3);
        } else {
            holder.edtContent.setTextSize(20);
            holder.edtContent.setTypeface(null, Typeface.NORMAL);
            holder.edtContent.setHint("Empieza a escibir...");
            holder.edtContent.setLines(7);
            holder.edtContent.setMaxLines(7);
        }
        if (model.imgFile != null) {
            holder.imgStory.setImageURI(Uri.fromFile(model.imgFile));
        } else {
            holder.imgStory.setImageResource(R.drawable.img_story_default);
        }

        holder.edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mStories.get(holder.getBindingAdapterPosition()).content = s.toString();
            }
        });
        holder.imgCamera.setOnClickListener(v -> mListener.onClickCamera());
    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }

    public interface OnAddStoryItemListener {
        void onClickCamera();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgStory, imgCamera;
        public EditText edtContent;
        public TextView txtStoryNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStory = itemView.findViewById(R.id.imgStory);
            imgCamera = itemView.findViewById(R.id.imgCamera);
            edtContent = itemView.findViewById(R.id.edtContent);
            txtStoryNumber = itemView.findViewById(R.id.txtStoryNumber);
        }
    }
}
