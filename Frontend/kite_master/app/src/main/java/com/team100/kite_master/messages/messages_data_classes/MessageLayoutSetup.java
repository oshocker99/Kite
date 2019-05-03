package com.team100.kite_master.messages.messages_data_classes;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team100.kite_master.R;

public class MessageLayoutSetup {

    private Context context;
    private String username;

    public MessageLayoutSetup(Context context, String username) {

        this.context = context;
        this.username = username;
    }

    public LinearLayout setupMessageHolder(String username) {

        int width;
        int height;

        LinearLayout messageHolder;
        LinearLayout.LayoutParams layoutParams;

        messageHolder = new LinearLayout(this.context);

        width = LinearLayout.LayoutParams.MATCH_PARENT;
        // width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.WRAP_CONTENT;
        // height = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParams = new LinearLayout.LayoutParams(width, height);

        // Position the messages that you yourself send to the right
        // Position the messages of other users to the left
        if (username == this.username) {

            messageHolder.setGravity(Gravity.RIGHT);
        }
        else {

            messageHolder.setGravity(Gravity.LEFT);
        }

        messageHolder.setLayoutParams(layoutParams);
        messageHolder.requestLayout();

        return messageHolder;
    }

    public RelativeLayout setupMessage(String username, String messageString) {

        RelativeLayout messageLayout;
        TextView messageText;

        messageText = setupMessageTextView(messageString);

        messageLayout = setupRelativeLayout(username);
        messageLayout.addView(messageText);

        return messageLayout;
    }

    public RelativeLayout setupRelativeLayout(String username) {

        final int DISTANCE_FROM_CLOSE_EDGE = 30;
        final int DISTANCE_FROM_FAR_EDGE = 240;

        int width;
        int height;

        TextView messageText;
        RelativeLayout.LayoutParams relativeParams;

        RelativeLayout messageLayout = new RelativeLayout(this.context);

        // Credit to this source: https://stackoverflow.com/questions/18844418/add-margin-programmatically-to-relativelayout
        // Set parameters of relativeLayout object
        width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        relativeParams = new RelativeLayout.LayoutParams(width, height);
        relativeParams.setMargins(DISTANCE_FROM_CLOSE_EDGE, 0, DISTANCE_FROM_CLOSE_EDGE, 0);

        // Position the messages that you yourself send to the right
        // Position the messages of other users to the left
        if (username == this.username) {

            relativeParams.setMarginStart(DISTANCE_FROM_FAR_EDGE);
            relativeParams.setMarginEnd(DISTANCE_FROM_CLOSE_EDGE);

            messageLayout.setBackgroundResource(R.drawable.message_layout_this_user);
        }
        else {

            relativeParams.setMarginStart(DISTANCE_FROM_CLOSE_EDGE);
            relativeParams.setMarginEnd(DISTANCE_FROM_FAR_EDGE);

            messageLayout.setBackgroundResource(R.drawable.message_layout);
        }

        messageLayout.setLayoutParams(relativeParams);
        messageLayout.requestLayout();
        messageLayout.setGravity(Gravity.BOTTOM);

        return messageLayout;
    }

    public TextView setupTimeTextView(String username, String messageTime) {

        final int DISTANCE_FROM_CLOSE_EDGE = 30;
        final int DISTANCE_FROM_FAR_EDGE = 240;
        final int BLACK_COLOR = 0xff000000;

        int width;
        int height;

        TextView messageText;
        LinearLayout.LayoutParams layoutParams;

        messageText = new TextView(this.context);
        messageText.setText(messageTime);
        messageText.setTextColor(BLACK_COLOR);
        messageText.setPadding(DISTANCE_FROM_CLOSE_EDGE, DISTANCE_FROM_CLOSE_EDGE, DISTANCE_FROM_CLOSE_EDGE, 0);

        width = LinearLayout.LayoutParams.WRAP_CONTENT;
        height = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParams = new LinearLayout.LayoutParams(width, height);

        if (username == this.username) {

            layoutParams.setMarginStart(DISTANCE_FROM_FAR_EDGE);
            layoutParams.setMarginEnd(DISTANCE_FROM_CLOSE_EDGE);
        }
        else {

            layoutParams.setMarginStart(DISTANCE_FROM_CLOSE_EDGE);
            layoutParams.setMarginEnd(DISTANCE_FROM_FAR_EDGE);
        }

        messageText.setLayoutParams(layoutParams);
        messageText.setGravity(Gravity.TOP);

        return messageText;
    }

    public TextView setupMessageTextView(String messageString) {

        final int DISTANCE_FROM_CLOSE_EDGE = 30;
        final int BLACK_COLOR = 0xff000000;

        int width;
        int height;

        TextView messageText;
        LinearLayout.LayoutParams layoutParams;

        messageText = new TextView(this.context);
        messageText.setText(messageString);
        messageText.setTextColor(BLACK_COLOR);
        messageText.setPadding(DISTANCE_FROM_CLOSE_EDGE, DISTANCE_FROM_CLOSE_EDGE, DISTANCE_FROM_CLOSE_EDGE, DISTANCE_FROM_CLOSE_EDGE);

        width = LinearLayout.LayoutParams.WRAP_CONTENT;
        height = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParams = new LinearLayout.LayoutParams(width, height);

        messageText.setLayoutParams(layoutParams);

        return messageText;
    }



    // Getter methods
    public Context getContext() {

        return this.context;
    }

    public String getUsername() {

        return this.username;
    }
}
