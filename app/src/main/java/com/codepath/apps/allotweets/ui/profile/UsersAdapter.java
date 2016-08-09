package com.codepath.apps.allotweets.ui.profile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Tweet;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.ui.base.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Users Adapter
 * <p/>
 * Created by ALLO on 8/8/16.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private static final String TAG_LOG = UsersAdapter.class.getCanonicalName();

    public interface OnUsersAdapterListener {
        void didSelectUser(TwitterUser twitterUser);
    }

    private ArrayList<TwitterUser> mUsers;
    private OnUsersAdapterListener mListener;

    public UsersAdapter(ArrayList<TwitterUser> users, OnUsersAdapterListener listener) {
        this.mUsers = users;
        this.mListener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.configureViewWithTwitterUser(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers != null ? mUsers.size() : 0;
    }

    public void notifyDataSetChanged(ArrayList<TwitterUser> users) {
        this.mUsers = new ArrayList<>(users);
        notifyDataSetChanged();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        protected TwitterUser user;

        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_screenname)
        TextView tvScreenname;

        @BindView(R.id.tv_description)
        TextView tvDescription;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) mListener.didSelectUser(user);
                }
            });

            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) mListener.didSelectUser(user);
                }
            });
        }

        public void configureViewWithTwitterUser(TwitterUser user) {
            this.user = user;
            Glide.with(ivAvatar.getContext())
                    .load(user.getProfileImageUrl())
                    .placeholder(R.drawable.ic_twitter)
                    .into(ivAvatar);

            tvName.setText(user.getName());
            tvScreenname.setText(user.getScreennameForDisplay());
            tvDescription.setText(user.getDescription());
        }
    }
}
