package com.team100.kite_master.forum;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.team100.kite_master.MainActivity;
import com.team100.kite_master.R;
import com.team100.kite_master.networking.NetworkManager;
import com.team100.kite_master.networking.VolleyListener;

import java.util.Objects;

public class ForumNewPostFragment extends Fragment implements View.OnClickListener {

    //declare global vars
    private String topic;

    //declare layout items
    private EditText titleText;
    private EditText bodyText;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.forum_new_post, container, false);
        //receive bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            topic = bundle.getString("new_post_topic");
        }

        //DEBUGGING
        System.out.println(" ");
        System.out.println("NEW POST FRAGMENT:");
        System.out.println("POSTING TO TOPIC: " + topic);
        System.out.println(" ");

        //link layout items
        titleText = v.findViewById(R.id.title_edit_text);
        bodyText = v.findViewById(R.id.body_edit_text);
        Button postButton = v.findViewById(R.id.post_button);


        //set current screen
        ((MainActivity) Objects.requireNonNull(getActivity())).setCurScreen("new_post");
        ((MainActivity) Objects.requireNonNull(getActivity())).setDrawerItemSelection(0);


        //set on click listener
        postButton.setOnClickListener(this);
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set title
        Objects.requireNonNull(getActivity()).setTitle("New Post - " + topic);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.post_button) {
            titleText.onEditorAction(EditorInfo.IME_ACTION_DONE);
            bodyText.onEditorAction(EditorInfo.IME_ACTION_DONE);
            if (titleText.getText().toString().equals("") || bodyText.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please fill out all fields!", Toast.LENGTH_LONG).show();
            } else {
                String username = ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.getUsername();
                sendPost(titleText.getText().toString(), bodyText.getText().toString(), username, topic);
            }
        }
    }

    public void confirmAndClose() {
        Toast.makeText(getActivity(), "Post sent successfully!" + " ", Toast.LENGTH_LONG).show();
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
    }


    //NETWORKING

    //send new post
    private void sendPost(final String title, final String body, final String author, final String topic) {
        NetworkManager.getInstance().sendPost(title, body, topic, new VolleyListener<String>() {
            @Override
            public void getResult(String string) {
                confirmAndClose();
            }

            @Override
            public void getError(VolleyError err) {
                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

}
