package com.team100.kite_master.favorites;

import com.android.volley.VolleyError;
import com.team100.kite_master.forum.ForumParser;
import com.team100.kite_master.forum.forum_data_classes.Post;
import com.team100.kite_master.networking.NetworkManager;
import com.team100.kite_master.networking.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class FavoriteStorageHandler {

    private ArrayList<Post> favList = new ArrayList<>();

    public String favIDtoString(ArrayList<Post> plist) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < plist.size(); i++) {
            sb.append(plist.get(i).getPostID());
            sb.append("%");
        }
        return sb.toString();
    }


    public ArrayList<String> stringToFavID(String s) {

        String[] sa = s.split("%");
        return new ArrayList<>(Arrays.asList(sa));
    }


    public ArrayList<Post> getAllFavPosts(ArrayList<String> postIDS) {

        for (int i = 0; i < postIDS.size(); i++) requestPost(postIDS.get(i));
        return favList;
    }

    //requests topic JSON object from backend
    private void requestPost(String postid) {
        NetworkManager.getInstance().requestPost(postid, new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
                try {
                    ForumParser fp = new ForumParser();
                    favList.add(fp.parsePost(object));
                } catch (JSONException e) {
                    System.out.println("PARSE ERROR");
                }
            }

            @Override
            public void getError(VolleyError err) {
                System.out.println("Network Error");
            }
        });
    }


}
