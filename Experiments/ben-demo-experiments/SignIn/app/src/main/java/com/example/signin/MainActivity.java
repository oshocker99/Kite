package com.example.signin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView LoginTitle;
    private EditText EnterUsername;
    private EditText EnterPassword;
    private Button SignInButton;
    private TextView LoginResult;

    private String URL = "http://kite.onn.sh/api/auth/login";
    private RequestQueue Requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginTitle = (TextView) findViewById(R.id.Login);
        EnterUsername = (EditText) findViewById(R.id.EnterUsername);
        EnterPassword = (EditText) findViewById(R.id.EnterPassword);
        SignInButton = (Button) findViewById(R.id.SignIn);
        LoginResult = (TextView) findViewById(R.id.LoginStatus);

        Requests = Volley.newRequestQueue(this);

        SignInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String username = EnterUsername.getText().toString();
                String password = EnterPassword.getText().toString();

                login(username, password);
            }
        });
    }

    private void login(final String username, final String password) {

        JSONObject LoginCredentials = new JSONObject();

        try {
            LoginCredentials.put("Username", username);
            LoginCredentials.put("Password", password);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, URL, null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject token = response.getJSONObject("data");

                            String JWT = token.getString("access_token");

                            LoginResult.setText(JWT);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplication(), response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //try {

                            // JSONObject TokenError = error.;
                            // String LoginProblem = TokenError.getString("detail");
                            // LoginResult.setText(LoginProblem);

                            // NetworkResponse NetResponse = error.networkResponse;
                            // String LoginProblem = new String();
                            // LoginResult.setText(LoginProblem);

                            // JSONObject TokenError = new JSONObject(error.toString());
                            // String LoginProblem = TokenError.toString();
                            // LoginResult.setText(LoginProblem);

                            // String TokenError = new String(error.networkResponse.data, "utf-8"); // String TokenError = new String(error.networkResponse.toString());
                            // JSONObject data = new JSONObject(TokenError.trim());
                            // JSONObject detail; //= data.getJSONObject("detail");

                            // String LoginProblem = data.toString();
                            // String LoginProblem = detail.toString();
                            // LoginResult.setText(LoginProblem);

                            // JSONArray errors = data.getJSONArray("errors");
                            // JSONObject jsonMessage = errors.getJSONObject(0);
                            // String message = jsonMessage.getString("message");

                            LoginResult.setText("Username or password incorrect?");

                        //}
                        //catch (UnsupportedEncodingException e) {
                           // e.printStackTrace();
                        //}
                        //catch (JSONException e) {
                            //e.printStackTrace();
                        //}

                        Toast.makeText(MainActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                })

        {
            // Credit to the people at this source: https://stackoverflow.com/questions/44000212/how-to-send-authorization-header-in-android-using-volley-library
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String credentials = username + ":" + password;
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("Authorization", "Basic " + base64EncodedCredentials);

                return headers;
            }
        };

        Requests.add(loginRequest);
    }
}
