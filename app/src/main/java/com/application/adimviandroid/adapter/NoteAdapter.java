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
import com.application.adimviandroid.models.NoteModel;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private Context mContext;
    private List<NoteModel> mNotes = new ArrayList<>();
    private NoteCellListener mListener;

    public NoteAdapter(Context mContext, List<NoteModel> mNotes, NoteCellListener mListener) {
        this.mContext = mContext;
        this.mNotes = mNotes;
        this.mListener = mListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteModel note = mNotes.get(position);
        holder.txtTitle.setText(note.title);
        holder.txtDesc.setText(note.description);
        holder.itemView.setOnClickListener(v -> mListener.onClickNoteCell(note));
        holder.imgDelete.setOnClickListener(v -> mListener.onClickDeleteNote(note.noteID, position));
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitle, txtDesc;
        public ImageView imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }

    public interface NoteCellListener {
        void onClickNoteCell(NoteModel note);
        void onClickDeleteNote(int notID, int position);
    }
}
