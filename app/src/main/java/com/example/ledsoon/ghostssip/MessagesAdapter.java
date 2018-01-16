package com.example.ledsoon.ghostssip;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ledsoon on 26.11.17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private RequestQueue requestQueue;
    private List<SingleMessage> messagesList;
    private final String serverBaseURL = "http://192.168.0.175";
    private final int MESSAGE_TIME_MODIFIER = 6;
    private SharedPreferences.Editor userVotesEditor;

    public MessagesAdapter(List<SingleMessage> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        requestQueue = Volley.newRequestQueue(viewGroup.getContext());
        userVotesEditor = viewGroup.getContext().getSharedPreferences("USER_VOTES", Context.MODE_PRIVATE).edit();
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.message_card_layout, viewGroup, false);

        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder messageViewHolder, int position) {
        SingleMessage singleMessage = messagesList.get(position);
        messageViewHolder.authorTextView.setText(singleMessage.author);
        messageViewHolder.messageTextView.setText(singleMessage.content);
        if(singleMessage.isLiked) {
            messageViewHolder.likeButton.setLiked(true);
        }else {
            messageViewHolder.likeButton.setLiked(false);
        }
        if(singleMessage.isDisliked) {
            messageViewHolder.dislikeButton.setLiked(true);
        }else {
            messageViewHolder.dislikeButton.setLiked(false);
        }
        messageViewHolder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if(messageViewHolder.dislikeButton.isLiked()) {
                    messageViewHolder.dislikeButton.setLiked(false);
                    modifyExpiryDateOfMessageOnRemoteDatabase(singleMessage.id, 2 * MESSAGE_TIME_MODIFIER);
                }else {
                    modifyExpiryDateOfMessageOnRemoteDatabase(singleMessage.id, MESSAGE_TIME_MODIFIER);
                }
                userVotesEditor.putString(String.valueOf(singleMessage.id), "liked");
            }
            @Override
            public void unLiked(LikeButton likeButton) {
                modifyExpiryDateOfMessageOnRemoteDatabase(singleMessage.id, -MESSAGE_TIME_MODIFIER);
                userVotesEditor.remove(String.valueOf(singleMessage.id));
            }
        });
        messageViewHolder.dislikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton dislikeButton) {
                if(messageViewHolder.likeButton.isLiked()) {
                    messageViewHolder.likeButton.setLiked(false);
                    modifyExpiryDateOfMessageOnRemoteDatabase(singleMessage.id, -2 * MESSAGE_TIME_MODIFIER);
                }else {
                    modifyExpiryDateOfMessageOnRemoteDatabase(singleMessage.id, -MESSAGE_TIME_MODIFIER);
                }
                userVotesEditor.putString(String.valueOf(singleMessage.id), "disliked");
            }
            @Override
            public void unLiked(LikeButton dislikeButton) {
                modifyExpiryDateOfMessageOnRemoteDatabase(singleMessage.id, MESSAGE_TIME_MODIFIER);
                userVotesEditor.remove(String.valueOf(singleMessage.id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView, messageTextView;
        LikeButton likeButton, dislikeButton;

        MessageViewHolder(View v) {
            super(v);
            authorTextView = v.findViewById(R.id.authorTextView);
            messageTextView = v.findViewById(R.id.messageTextView);
            likeButton = v.findViewById(R.id.likeButton);
            dislikeButton = v.findViewById(R.id.dislikeButton);
        }
    }

    private void modifyExpiryDateOfMessageOnRemoteDatabase(int id, int numberOfHoursToAdd) {
        Map <String, String> messageBody = new HashMap<>();
        messageBody.put("id", String.valueOf(id));
        messageBody.put("numberOfHoursToAdd", String.valueOf(numberOfHoursToAdd));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverBaseURL.concat("/ghostssip/modify_expiry_date_of_message.php"),
                response -> {
                    if(response.equals("success")) {
                        userVotesEditor.apply();
                    }
                },
                error -> {}){
            @Override
            protected Map<String, String> getParams(){
                return messageBody;
            }

        };
        requestQueue.add(stringRequest);
    }
}
