package com.team100.kite_master.messages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.team100.kite_master.MainActivity;
import com.team100.kite_master.R;
import com.team100.kite_master.messages.messages_data_classes.Message;
import com.team100.kite_master.messages.messages_data_classes.MessageLayoutSetup;

import java.util.Arrays;
import java.util.Objects;


public class MessagesFragment extends Fragment implements OutputHandler {

    private String LOCAL_IP_ADDRESS;
    private String[] userdata;
    private String username;

    private ScrollView scrollView;
    private LinearLayout messageList;

    private TextView errorTextView;
    private EditText messageText;
    private Button postButton;

    private WebSocketImplementation implementationWS;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.messages_fragment, container, false);

        userdata = ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.toArray();
        username = userdata[0];
        LOCAL_IP_ADDRESS = ((MainActivity) Objects.requireNonNull(getActivity())).getServerIP();

        //initialize user interface objects
        scrollView = (ScrollView) v.findViewById(R.id.message_scroll_view);
        messageList = (LinearLayout) v.findViewById(R.id.message_linear_layout);

        errorTextView = (TextView) v.findViewById(R.id.error_textView);

        messageText = (EditText) v.findViewById(R.id.message_edit_text);

        postButton = (Button) v.findViewById(R.id.message_button);

        implementationWS = new WebSocketImplementation(this, username, LOCAL_IP_ADDRESS);

        //set on click listener
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageString = messageText.getText().toString();

                // Make sure the string isn't empty
                if (!messageString.equals("")) {

                    // Send the message
                    implementationWS.sendJSONText(messageString);

                    // Clear the message text
                    messageText.setText("");
                } else {

                    Toast.makeText(getActivity(), "Please enter a message" + " ", Toast.LENGTH_LONG).show();
                }
            }
        });

        //set current screen
        ((MainActivity) Objects.requireNonNull(getActivity())).setCurScreen("messages");
        ((MainActivity) Objects.requireNonNull(getActivity())).setDrawerItemSelection(2);


        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle("Messages");
    }

    public void output(final String username, final String txt) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                MessageLayoutSetup layoutSetup = new MessageLayoutSetup(getContext(), getUsername());

                Message msg = new Message(username, txt);
                String messageTime = msg.getMessageTime();
                String messageString = msg.getUsername() + ": " + msg.getText();

                // Create a new LinearLayout object
                LinearLayout timeHolder = layoutSetup.setupMessageHolder(username);
                LinearLayout messageHolder = layoutSetup.setupMessageHolder(username);

                // Create a message, and set it up
                TextView time = layoutSetup.setupTimeTextView(username, messageTime);
                RelativeLayout message = layoutSetup.setupMessage(username, messageString);

                // Add the message to the Linearlayout
                timeHolder.addView(time);
                messageHolder.addView(message);

                // Add the messageHolder to the linearLayout
                messageList.addView(timeHolder);
                messageList.addView(messageHolder);

                // Credit to this source: https://stackoverflow.com/questions/21926644/get-height-and-width-of-a-layout-programmatically
                // Scroll to bottom upon receiving new messages
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });

            }

        });

    }

    public void setErrorText(final String errorText) {


        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                errorTextView.setText(errorText);
            }
        });


    }


    // Getter and setter methods used for JUnit and Mockito testing
    public String getUsername() {

        return this.username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public LinearLayout getMessageView() {

        return this.messageList;
    }

    public void setMessageView(LinearLayout messageView) {

        this.messageList = messageView;
    }

    public String getIPaddress() {

        return this.LOCAL_IP_ADDRESS;
    }

    public void setIPaddress(String LOCAL_IP_ADDRESS) {

        this.LOCAL_IP_ADDRESS = LOCAL_IP_ADDRESS;
    }

    public TextView getErrorTextView() {

        return this.errorTextView;
    }

    public View getView() {

        return this.getView();
    }

    public WebSocketImplementation getWebSocketImplementation() {

        return this.implementationWS;
    }
}