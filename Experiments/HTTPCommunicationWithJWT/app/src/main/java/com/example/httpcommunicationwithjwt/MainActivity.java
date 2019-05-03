package com.example.httpcommunicationwithjwt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button SignInButton;

    private TextView LoginJWT;
    private TextView UserInfo;

    private EditText EnterUsername;
    private EditText EnterPassword;
    private EditText EnterBio;

    private Button SetBio;
    private Button DeleteUser;
    private Button GetUserInfo;

    private HTTPInterface Implementation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignInButton = (Button) findViewById(R.id.SignIn);

        LoginJWT = (TextView) findViewById(R.id.LoginStatus);
        UserInfo = (TextView) findViewById(R.id.UserInfo);

        EnterUsername = (EditText) findViewById(R.id.EnterUsername);
        EnterPassword = (EditText) findViewById(R.id.EnterPassword);
        EnterBio = (EditText) findViewById(R.id.EnterBio);

        SetBio = (Button) findViewById(R.id.SetBio);
        DeleteUser = (Button) findViewById(R.id.DeleteUser);
        GetUserInfo = (Button) findViewById(R.id.GetUserInfo);

        Implementation = new HTTPImplementation(getApplication(), getApplicationContext(), "http://kite.onn.sh:5000/api/v3/users", LoginJWT, UserInfo);

        SignInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String username = EnterUsername.getText().toString();
                String password = EnterPassword.getText().toString();

                Implementation.login(username, password);
            }
        });

        GetUserInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String userName = EnterUsername.getText().toString();

                Implementation.getUserInfo(userName);
            }
        });

        SetBio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String userName = EnterUsername.getText().toString();
                String bio = EnterBio.getText().toString();

                Implementation.setBio(userName, bio);
            }
        });

        DeleteUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String userName = EnterUsername.getText().toString();

                Implementation.deleteUser(userName);
            }
        });
    }
}