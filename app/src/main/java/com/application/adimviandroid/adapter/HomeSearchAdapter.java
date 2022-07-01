package com.application.adimviandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.adimviandroid.R;
import com.application.adimviandroid.types.HomeSearchType;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import per.wsj.library.AndRatingBar;

public class HomeSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<JSONObject> mArr = new ArrayList<>();
    private OnClickSearchCellLisener mListener;

    public HomeSearchAdapter(Context mContext, List<JSONObject> mArr, OnClickSearchCellLisener mListener) {
        this.mContext = mContext;
        this.mArr = mArr;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new PostViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_post, parent, false));
        } else if (viewType == 1) {
            return new UserViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_user, parent, false));
        } else {
            return new TagViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_tag, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            JSONObject obj = mArr.get(position);
            int id = obj.getInt("id");
            switch (holder.getItemViewType()) {
                case 0:
                    PostViewHolder postViewHolder = (PostViewHolder) holder;
                    AppUtil.loadImageByUrl(mContext, postViewHolder.imgUser, ApiUtil.ImageUrl + obj.getString("avatar"), ImagePlaceHolderType.USERIMAGE);
                    postViewHolder.txtUserName.setText(obj.getString("handle"));
                    postViewHolder.txtPostTitle.setText(obj.getString("title"));
                    postViewHolder.ratingBar.setRating(Float.parseFloat(obj.getString("avgRating")));
                    break;
                case 1:
                    UserViewHolder userViewHolder = (UserViewHolder) holder;
                    AppUtil.loadImageByUrl(mContext, userViewHolder.imgUser, ApiUtil.ImageUrl + obj.getString("avatarblobid"), ImagePlaceHolderType.USERIMAGE);
                    userViewHolder.imgVerify.setVisibility(obj.getInt("userverify") == 1 ? View.VISIBLE : View.GONE);
                    userViewHolder.txtUserName.setText(obj.getString("title"));
                    userViewHolder.txtPostCount.setText(obj.getString("totalPost") + " Posts");
                    userViewHolder.txtSeguindo.setText(obj.getString("totalFollowers") + " seguidores");
                    userViewHolder.txtSiguiendo.setText(obj.getString("totalFollowing") + " siguiendo");
                    break;
                case 2:
                    TagViewHolder tagViewHolder = (TagViewHolder) holder;
                    tagViewHolder.lltUser.setVisibility(View.GONE);
                    tagViewHolder.txtValue.setText(obj.getString("title"));
                    break;
            }
            holder.itemView.setOnClickListener(v -> mListener.onClickSearchCell(mArr.get(position)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return AppUtil.homeSearchType == HomeSearchType.POST ? 0 : (AppUtil.homeSearchType == HomeSearchType.USERNAME ? 1 : 2);
    }

    @Override
    public int getItemCount() {
        return mArr.size();
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgUser;
        public TextView txtValue;
        public LinearLayout lltUser;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            txtValue = itemView.findViewById(R.id.txtValue);
            lltUser = itemView.findViewById(R.id.llt_user);
        }
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgUser;
        public TextView txtUserName, txtPostTitle;
        public AndRatingBar ratingBar;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtPostTitle = itemView.findViewById(R.id.txtPostTitle);
            ratingBar = itemView.findViewById(R.id.ratingPost);
        }
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgUser, imgVerify;
        public TextView txtPostCount, txtSeguindo, txtSiguiendo, txtUserName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgVerify = itemView.findViewById(R.id.imgVerify);
            txtPostCount = itemView.findViewById(R.id.txtPostCount);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtSeguindo = itemView.findViewById(R.id.txtSeguiendo);
            txtSiguiendo = itemView.findViewById(R.id.txtSiguiendo);
        }
    }

    public interface OnClickSearchCellLisener {
        void onClickSearchCell(JSONObject obj);
    }
}
