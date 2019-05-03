package com.example.httpcommunicationwithjwt;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Base64;
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

public class HTTPImplementation implements HTTPInterface {

    private Application HTTPactivity;
    private Context HTTPcontext;

    private RequestQueue Requests;
    private String RequestURL;

    private TextView TextviewJWT;
    private TextView UserInfo;

    public HTTPImplementation(Application HTTPactivity, Context HTTPcontext, String RequestURL, TextView TextviewJWT, TextView UserInfo) {

        this.HTTPactivity = HTTPactivity;
        this.HTTPcontext = HTTPcontext;
        this.RequestURL = RequestURL;

        this.Requests = Volley.newRequestQueue(HTTPcontext);

        this.TextviewJWT = TextviewJWT;
        this.UserInfo = UserInfo;
    }

    @Override
    public String login(final String username, final String password) {

        String LoginRequestURL = "http://kite.onn.sh:5000/api/auth/login";

        JSONObject LoginCredentials = new JSONObject();

        try {
            LoginCredentials.put("Username", username);
            LoginCredentials.put("Password", password);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, LoginRequestURL, null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject token = response.getJSONObject("data");

                            setJWT(token.getString("access_token"));
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(HTTPactivity, response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();

                        Toast.makeText(HTTPcontext, error + "", Toast.LENGTH_SHORT).show();
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

        Request success = Requests.add(loginRequest);

        return success.getBodyContentType();
    }

    @Override
    public boolean getUserInfo(String userName) {

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, RequestURL + "/" + userName, null,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject user = response.getJSONObject("data");

                            String userName = user.getString("username");
                            boolean isAdmin = user.getBoolean("is_admin");
                            boolean isMod = user.getBoolean("is_mod");
                            int postCount = user.getInt("post_count");
                            String bio = user.getString("bio");
                            String displayName = user.getString("displayName");

                            setInfo(userName + ", " + Boolean.toString(isAdmin) + ", " + Boolean.toString(isMod) + ", "
                                    + Integer.toString(postCount) + ", " + bio + ", "+ displayName);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(HTTPcontext, response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HTTPcontext, error + "", Toast.LENGTH_SHORT).show();
                    }
                }) {

            // Credit to the people at this source: https://stackoverflow.com/questions/25941658/volley-how-to-send-jsonobject-using-bearer-accesstoken-authentication
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + TextviewJWT.getText().toString());

                return headers;
            }
        };

        Request success = Requests.add(getRequest);

        return false;
    }

    @Override
    public boolean setBio(String userName, String newBio) {

        JSONObject newUser = new JSONObject();

        try {
            newUser.put("bio", newBio);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, RequestURL + "/" + userName, newUser,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(HTTPcontext, response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HTTPcontext, error + "", Toast.LENGTH_SHORT).show();
                    }
                }) {

            // Credit to the people at this source: https://stackoverflow.com/questions/25941658/volley-how-to-send-jsonobject-using-bearer-accesstoken-authentication
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + TextviewJWT.getText().toString());

                return headers;
            }
        };

        Request success = Requests.add(putRequest);

        return false;
    }

    @Override
    public boolean deleteUser(String userName) {

        JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, RequestURL + "/" + userName, null,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(HTTPcontext, response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HTTPcontext, error + "", Toast.LENGTH_SHORT).show();
                    }
                }) {

            // Credit to the people at this source: https://stackoverflow.com/questions/25941658/volley-how-to-send-jsonobject-using-bearer-accesstoken-authentication
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + TextviewJWT.getText().toString());

                return headers;
            }
        };

        Request success = Requests.add(deleteRequest);

        return false;
    }

    public String getJWT() {

        return this.TextviewJWT.getText().toString();
    }

    public void setJWT(String JWT) {

        this.TextviewJWT.setText(JWT);
    }

    public String getInfo() {

        return this.UserInfo.getText().toString();
    }

    public void setInfo(String userInfo) {

        this.UserInfo.setText(userInfo);
    }

    public JSONObject foo() {

        return null;
    }
}
