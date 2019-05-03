package com.team100.kite_master.userdata;

import org.json.JSONException;
import org.json.JSONObject;

public class UserParser {

    //convert JSON object from backend to arraylist of topics
    public User parseUserInfo(JSONObject resp) throws JSONException {
        //get json array of user info
        JSONObject info = resp.getJSONObject("data");
        return new User(
                info.getString("username"),
                info.getString("displayName"),
                info.getString("bio"),
                info.getInt("post_count"),
                info.getBoolean("is_admin"),
                info.getBoolean("is_mod")
        );
    }
}
