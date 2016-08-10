package com.codepath.apps.allotweets.ui.timeline;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.allotweets.R;
import com.codepath.apps.allotweets.model.Message;
import com.codepath.apps.allotweets.model.TwitterUser;
import com.codepath.apps.allotweets.ui.base.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Messages Adapter
 * <p/>
 * Created by ALLO on 9/8/16.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    public static final String TAG_LOG = MessagesAdapter.class.getCanonicalName();

    public interface OnMessagesAdapterListener {
        void didSelectUser(TwitterUser user);
    }

    private ArrayList<Message> mMessages;
    private OnMessagesAdapterListener mListener;

    public MessagesAdapter(ArrayList<Message> messages, OnMessagesAdapterListener listener) {
        this.mMessages = messages;
        this.mListener = listener;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.configureViewWithMessage(mMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return mMessages != null ? mMessages.size() : 0;
    }

    public void notifyDataSetChanged(ArrayList<Message> messages) {
        this.mMessages = new ArrayList<>(messages);
        notifyDataSetChanged();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        protected Message message;

        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_user)
        TextView tvUser;

        @BindView(R.id.tv_text)
        TextView tvText;

        @BindView(R.id.tv_date)
        TextView tvDate;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) mListener.didSelectUser(message.getSender());
                }
            });
        }

        public void configureViewWithMessage(Message message) {
            this.message = message;

            Glide.with(ivAvatar.getContext())
                    .load(message.getSender().getProfileImageUrl())
                    .placeholder(R.drawable.ic_twitter)
                    .bitmapTransform(new CropCircleTransformation(ivAvatar.getContext()))
                    .into(ivAvatar);

            tvName.setText(message.getSender().getName());
            tvUser.setText(message.getSender().getScreennameForDisplay());
            tvText.setText(message.getText());
            tvDate.setText(message.getFormattedCreatedAtDate());
        }
    }
}
