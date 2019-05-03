package com.example.httpcommunicationwithjwt;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    /*

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    */

    private MainActivity main;
    private RequestQueue request;
    private Response.Listener<JSONObject> listener;

    private JsonObjectRequest getRequest;

    @Before
    public void setup() {

        // main = new MainActivity();
        main = mock(MainActivity.class);

        request = mock(RequestQueue.class);



        listener = mock(Response.Listener.class);



        String RequestURL = "http://kite.onn.sh/api/v3/users";
        String username = "fadmin";

        final String JWT = ""; // FIXME

        getRequest = new JsonObjectRequest(Request.Method.GET, RequestURL + "/" + username, null,

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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Toast.makeText(getApplication(), response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {

            // Credit to the people at this source: https://stackoverflow.com/questions/25941658/volley-how-to-send-jsonobject-using-bearer-accesstoken-authentication
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + JWT);

                return headers;
            }
        };
    }


}