package com.team100.kite_master;

import android.os.Bundle;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.team100.kite_master.messages.MessagesFragment;
import com.team100.kite_master.messages.WebSocketImplementation;
import com.team100.kite_master.messages.messages_data_classes.Message;
import com.team100.kite_master.messages.messages_data_classes.MessageLayoutSetup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MessageFragmentAndroidTest {

    // Test variables
    private MainActivity main;
    private MessagesFragment messageFrag;
    private WebSocketImplementation impWS;
    private Message realMessage;
    private MessageLayoutSetup layoutSetup;

    private MessagesFragment mockMessageFrag;
    private WebSocketImplementation mockImpWS;
    private Message mockMessage;



    // Credit to this source: https://stackoverflow.com/questions/46458735/instrumented-unit-class-test-cant-create-handler-inside-thread-that-has-not-c
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    // Setup
    @Before
    public void onCreate() {

        // Regular objects

        messageFrag = new MessagesFragment();
        messageFrag.setUsername("fadmin");
        messageFrag.setIPaddress("kite.onn.sh");

        impWS = new WebSocketImplementation(messageFrag, messageFrag.getUsername(), messageFrag.getIPaddress());

        realMessage = new Message("fadmin", "Text!");
        layoutSetup = new MessageLayoutSetup(messageFrag.getContext(), messageFrag.getUsername());
    }



    // MessageLayoutSetup class tests
    @Test
    public void MessageLayoutSetupInitializationTest() {

        // messageFrag.setUsername("UserOne");

        // layoutSetup = new MessageLayoutSetup(messageFrag.getContext(), messageFrag.getUsername());

        // Make sure that the instance variables of layoutSetup and messageFrag are the same.
        // Assert.assertNotNull(messageFrag.getContext());
        // Assert.assertNotNull(layoutSetup.getContext());
        // assertEquals(layoutSetup.getContext(), messageFrag.getContext());
        // assertEquals(layoutSetup.getUsername(), messageFrag.getUsername());
    }



    /*

    @Test
    public void SetupMessageTextViewTest() {

        // layoutSetup = new MessageLayoutSetup(messageFrag.getContext(), messageFrag.getUsername());

        String message = "A very important message!";

        TextView textView = layoutSetup.setupMessageTextView(message); // WHY IS CONTEXT NULL???

        assertEquals(message, textView.getText()); // Check that the message is correct

    }

    @Test
    public void SetupTimeTextView() {

        TextView textView;

        String username = "UserOne";
        String timeText = "12:07 AM";

        textView = layoutSetup.setupTimeTextView(username, timeText);

        assertEquals(timeText, textView.getText()); // Check that the time is correct
    }

    @Test
    public void SetupRelativeLayoutTest() {

        RelativeLayout relativeLayout;

        String username = "UserOne";

        relativeLayout = layoutSetup.setupRelativeLayout(username);
    }

    */
}
