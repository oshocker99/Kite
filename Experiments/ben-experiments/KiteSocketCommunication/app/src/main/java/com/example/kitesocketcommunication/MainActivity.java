package com.example.kitesocketcommunication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Button stopButton;
    private EditText inputText;
    private Button sendButton;
    private EditText usernameText;
    private TextView outputText;

    private OkHttpClient client;
    private Request request;
    WebSocket websocket;

    String startUsername = "CoolDude34";

    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private class KiteWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {

            sendJSONText(usernameText.getText().toString() + " has joined the chat");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {

            receiveJSONText(text);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {

            sendJSONText(usernameText.getText().toString() + " has left the chat");
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        inputText = (EditText) findViewById(R.id.messageText);
        sendButton = (Button) findViewById(R.id.sendButton);
        usernameText = (EditText) findViewById(R.id.usernameText);
        outputText = (TextView) findViewById(R.id.outputText);

        client = new OkHttpClient.Builder().readTimeout(3, TimeUnit.SECONDS).build();
        request = new Request.Builder().url("http://chat.kite.onn.sh").build();
        // request = new Request.Builder().url("ws://echo.websocket.org").build();

        websocket = client.newWebSocket(request, new KiteWebSocketListener());

        usernameText.setText(startUsername);

        /*

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                client = new OkHttpClient.Builder().readTimeout(3, TimeUnit.SECONDS).build();
                request = new Request.Builder().url("http://chat.kite.onn.sh").build();
                websocket = client.newWebSocket(request, new KiteWebSocketListener());
            }
        });

        */

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                client.dispatcher().executorService().shutdown();

                websocket.close(NORMAL_CLOSURE_STATUS, null);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendJSONText(inputText.getText().toString());
            }
        });
    }

    private void sendJSONText(String TextString) {

        JsonObject JsonText = new JsonObject();

        JsonText.addProperty("username", usernameText.getText().toString());
        JsonText.addProperty("text", TextString);

        websocket.send(JsonText.toString());
    }

    private void receiveJSONText(String TextString) {

        JsonParser parser = new JsonParser();

        JsonObject JsonText = (JsonObject) parser.parse(TextString);

        JsonElement jsonUsername = JsonText.get("username");
        JsonElement jsonText = JsonText.get("text");

        String stringUsername = jsonUsername.getAsString();
        String stringText = jsonText.getAsString();

        output(stringUsername + ": " + stringText);
    }

    private void output(final String txt) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                outputText.setText(outputText.getText().toString() + "\n" + txt);
            }
        });
    }
}
