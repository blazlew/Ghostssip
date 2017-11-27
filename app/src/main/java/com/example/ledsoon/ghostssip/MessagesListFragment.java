package com.example.ledsoon.ghostssip;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class MessagesListFragment extends Fragment {

    private RecyclerView messagesListRecyclerView;

    public MessagesListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages_list, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        messagesListRecyclerView = view.findViewById(R.id.messagesListRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messagesListRecyclerView.setLayoutManager(linearLayoutManager);

        MessagesAdapter messagesAdapter = new MessagesAdapter(createRandomNewsList(30));

        messagesListRecyclerView.setAdapter(messagesAdapter);
    }

    private List<SingleMessage> createRandomNewsList(int size) {
        List<SingleMessage> result = new ArrayList<SingleMessage>();
        for (int i = 1; i <= size; i++) {
            SingleMessage singleMessage = new SingleMessage();
            singleMessage.author = getString(R.string.author) + i;
            singleMessage.content = getString(R.string.message) + i;
            singleMessage.isLiked = false;
            singleMessage.isDisliked = false;
            result.add(singleMessage);
        }
        return result;
    }
}
