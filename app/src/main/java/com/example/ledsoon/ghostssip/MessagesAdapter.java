package com.example.ledsoon.ghostssip;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

/**
 * Created by ledsoon on 26.11.17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {
    private List<SingleMessage> messagesList;

    public MessagesAdapter(List<SingleMessage> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.message_card_layout, viewGroup, false);

        return new MessagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessagesViewHolder messagesViewHolder, int position) {
        SingleMessage singleMessage = messagesList.get(position);
        messagesViewHolder.authorTextView.setText(singleMessage.author);
        messagesViewHolder.messageTextView.setText(singleMessage.content);
        if(singleMessage.isLiked) {
            messagesViewHolder.likeButton.setLiked(true);
        }else {
            messagesViewHolder.likeButton.setLiked(false);
        }
        if(singleMessage.isDisliked) {
            messagesViewHolder.dislikeButton.setLiked(true);
        }else {
            messagesViewHolder.dislikeButton.setLiked(false);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    static class MessagesViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView, messageTextView;
        LikeButton likeButton, dislikeButton;

        MessagesViewHolder(View v) {
            super(v);
            authorTextView = v.findViewById(R.id.authorTextView);
            messageTextView = v.findViewById(R.id.messageTextView);
            likeButton = v.findViewById(R.id.likeButton);
            dislikeButton = v.findViewById(R.id.dislikeButton);
            likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    dislikeButton.setLiked(false);
                }
                @Override
                public void unLiked(LikeButton likeButton) {
                }
            });
            dislikeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton dislikeButton) {
                    likeButton.setLiked(false);
                }
                @Override
                public void unLiked(LikeButton dislikeButton) {
                }
            });
        }
    }
}
