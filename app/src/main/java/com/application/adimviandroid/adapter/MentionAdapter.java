package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.MentionUserModel;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppUtil;
import com.hendraanggrian.appcompat.widget.SocialArrayAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MentionAdapter extends ArrayAdapter<MentionUserModel> {

    private Context mContext;
    private List<MentionUserModel> mMentionUsers = new ArrayList<>();

    public MentionAdapter(@NonNull Context context, @NonNull List<MentionUserModel> objects) {
        super(context, R.layout.item_mention, objects);
        this.mContext = context;
        this.mMentionUsers = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mentionFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mention, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MentionUserModel item = getItem(position);
        if (item != null) {
            if (position == getCount() - 1) {
                holder.lltBottom.setVisibility(View.VISIBLE);
            } else {
                holder.lltBottom.setVisibility(View.GONE);
            }
            holder.txtUserName.setText(item.name);
            AppUtil.loadImageByUrl(getContext(), holder.userImage, ApiUtil.ImageUrl + item.userAvatar, ImagePlaceHolderType.USERIMAGE);
        }
        return convertView;
    }

    private static class ViewHolder {

        private final ImageView userImage;
        private final TextView txtUserName;
        private final LinearLayout lltBottom;

        public ViewHolder(View itemView) {
            userImage = itemView.findViewById(R.id.imgUser);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            lltBottom = itemView.findViewById(R.id.lltBottom);
        }
    }

    private Filter mentionFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<MentionUserModel> suggestion = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggestion.addAll(mMentionUsers);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (MentionUserModel item: mMentionUsers) {
                    if (item.name.toLowerCase().contains(filterPattern)) {
                        suggestion.add(item);
                    }
                }
            }

            results.values = suggestion;
            results.count = suggestion.size();
            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((MentionUserModel) resultValue).name;
        }
    };
}
