package com.example.forumpost;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView PostTitle;
    private TextView PostDate;
    private TextView PostDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PostTitle = (TextView) findViewById(R.id.PostTitle);
        PostDate = (TextView) findViewById(R.id.PostDate);
        PostDescription = (TextView) findViewById(R.id.PostDescription);

        RequestQueue PostRequests = Volley.newRequestQueue(this);

        // jsonParse material from video
        String URL = "https://api.myjson.com/bins/kp9wz";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("employees");


                            JSONObject employee = jsonArray.getJSONObject(0);

                            String firstName = employee.getString("firstname");
                            int age = employee.getInt("age");
                            String mail = employee.getString("mail");

                            PostTitle.setText(firstName);
                            PostDate.setText(Integer.toString(age));
                            PostDescription.setText(mail);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        PostRequests.add(request);
    }
}
