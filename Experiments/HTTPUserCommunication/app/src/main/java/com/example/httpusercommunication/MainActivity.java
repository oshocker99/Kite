package com.example.httpusercommunication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView UserInfo;
    private EditText EnterUsername;
    private EditText EnterPassword;
    private EditText EnterBio;
    private Switch AdminBool;
    private Switch ModerBool;

    private Button SetPassword;
    private Button SetBio;
    private Button SetModer;
    private Button SetAdmin;
    private Button SetAll;
    private Button DeleteUser;
    private Button GetUserInfo;

    private String URL = "http://kite.onn.sh/api/user";
    private RequestQueue Requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserInfo = (TextView) findViewById(R.id.UserInfo);
        EnterUsername = (EditText) findViewById(R.id.EnterUsername);
        EnterPassword = (EditText) findViewById(R.id.EnterPassword);
        EnterBio = (EditText) findViewById(R.id.EnterBio);
        AdminBool = (Switch) findViewById(R.id.AdminBool);
        ModerBool = (Switch) findViewById(R.id.ModerBool);

        SetPassword = (Button) findViewById(R.id.SetPassword);
        SetBio = (Button) findViewById(R.id.SetBio);
        SetModer = (Button) findViewById(R.id.SetModer);
        SetAdmin = (Button) findViewById(R.id.SetAdmin);
        SetAll = (Button) findViewById(R.id.SetAll);
        DeleteUser = (Button) findViewById(R.id.DeleteUser);
        GetUserInfo = (Button) findViewById(R.id.GetUserInfo);

        Requests = Volley.newRequestQueue(this);

        GetUserInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String userName = EnterUsername.getText().toString();

                getUserInfo(userName);
            }
        });

        SetPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String userName = EnterUsername.getText().toString();
                String password = EnterPassword.getText().toString();

                setPassword(userName, password);
            }
        });

        SetBio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String userName = EnterUsername.getText().toString();
                String bio = EnterBio.getText().toString();

                setBio(userName, bio);
            }
        });

        SetModer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String userName = EnterUsername.getText().toString();
                boolean isModer = ModerBool.isChecked();

                setModeratorStatus(userName, isModer);
            }
        });

        SetAdmin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String userName = EnterUsername.getText().toString();
                boolean isAdmin = AdminBool.isChecked();

                setAdministratorStatus(userName, isAdmin);
            }
        });

        SetAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String userName = EnterUsername.getText().toString();
                String password = EnterPassword.getText().toString();
                String bio = EnterBio.getText().toString();
                boolean isModer = ModerBool.isChecked();
                boolean isAdmin = AdminBool.isChecked();

                setAll(userName, password, bio, isModer, isAdmin);
            }
        });

        DeleteUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String userName = EnterUsername.getText().toString();

                deleteUser(userName);
            }
        });
    }

    private void getUserInfo(String userName) {

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL + "/" + userName, null,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject user = response.getJSONObject("user");

                            String userName = user.getString("username");
                            boolean isAdmin = user.getBoolean("is_admin");
                            boolean isMod = user.getBoolean("is_mod");
                            int postCount = user.getInt("post_count");
                            String bio = user.getString("bio");

                            UserInfo.setText(userName + "\n" + Boolean.toString(isAdmin) + "\n" + Boolean.toString(isMod) + "\n" + Integer.toString(postCount) + "\n" + bio);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Toast.makeText(getApplication(), response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });

        Requests.add(getRequest);
    }

    private void setModeratorStatus(String userName, boolean isModer) {

        JSONObject newUser = new JSONObject();

        try {
            newUser.put("is_mod", isModer);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, URL + "/" + userName, newUser,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(getApplication(), response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });

        Requests.add(putRequest);
    }

    private void setAdministratorStatus(String userName, boolean isAdmin) {

        JSONObject newUser = new JSONObject();

        try {
            newUser.put("is_admin", isAdmin);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, URL + "/" + userName, newUser,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(getApplication(), response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });

        Requests.add(putRequest);
    }

    private void setPassword(String userName, String newPassword) {

        JSONObject newUser = new JSONObject();

        try {
            newUser.put("password", newPassword);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, URL + "/" + userName, newUser,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(getApplication(), response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });

        Requests.add(putRequest);
    }

    private void setBio(String userName, String newBio) {

        JSONObject newUser = new JSONObject();

        try {
            newUser.put("bio", newBio);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, URL + "/" + userName, newUser,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(getApplication(), response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });

        Requests.add(putRequest);
    }

    private void setAll(String userName, String newPassword, String newBio, boolean isModer, boolean isAdmin) {

        JSONObject newUser = new JSONObject();

        try {
            newUser.put("password", newPassword);
            newUser.put("bio", newBio);
            newUser.put("is_mod", isModer);
            newUser.put("is_admin", isAdmin);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, URL + "/" + userName, newUser,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(getApplication(), response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });

        Requests.add(putRequest);
    }

    private void deleteUser(String userName) {

        JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, URL + "/" + userName, null,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(getApplication(), response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });

        Requests.add(deleteRequest);
    }
}