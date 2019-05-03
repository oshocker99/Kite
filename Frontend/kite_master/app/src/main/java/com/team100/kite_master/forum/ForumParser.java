package com.team100.kite_master.forum;


import com.team100.kite_master.forum.forum_data_classes.Post;
import com.team100.kite_master.forum.forum_data_classes.Reply;
import com.team100.kite_master.forum.forum_data_classes.Topic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ForumParser {

    //convert JSON object from backend to arraylist of topics
    public Post parsePost(JSONObject resp) throws JSONException {
        //get json array of posts
        JSONObject jdata = resp.getJSONObject("data");
        JSONObject jpost = jdata.getJSONObject("post");
        //create new post object from data
        //hide loading circle
        return new Post(jpost.getString("id"),
                jpost.getString("title"),
                jpost.getString("body"),
                jpost.getString("author"),
                jpost.getBoolean("edited"),
                jpost.getString("topic_name"),
                jpost.getString("date"));
    }


    //convert JSON object from backend to arraylist of topics
    ArrayList<Topic> parseTopics(JSONObject resp) throws JSONException {
        //create output list
        ArrayList<Topic> tops = new ArrayList<>();
        //get JSON array of topics
        JSONArray topics = resp.getJSONObject("data").getJSONArray("topics");
        //for each element in the array create a new topic object and add it to the array list
        for (int i = 0; i < topics.length(); i++) {
            Topic t = new Topic(topics.getJSONObject(i).getString("name"), topics.getJSONObject(i).getString("descript"));
            tops.add(t);
        }
        return tops;

    }


    //convert JSON object from backend to arraylist of topics
    ArrayList<Post> parsePostList(JSONObject resp) throws JSONException {
        //create output list
        ArrayList<Post> receivedPosts = new ArrayList<>();
        //get json array of posts
        JSONObject jdata = resp.getJSONObject("data");
        JSONObject jtopic = jdata.getJSONObject("topic");
        JSONArray jposts = jtopic.getJSONArray("posts");
        //for each element in the array create a new topic object and add it to the array list
        for (int i = 0; i < jposts.length(); i++) {
            JSONObject curPost = jposts.getJSONObject(i);
            Post p = new Post(
                    curPost.getString("id"),
                    curPost.getString("title"),
                    curPost.getString("body"),
                    ("@" + curPost.getString("author")),
                    curPost.getBoolean("edited"),
                    curPost.getString("topic_name"),
                    curPost.getString("date"));
            receivedPosts.add(p);
        }
        return receivedPosts;
    }


    //convert JSON object from backend to arraylist of topics
    public ArrayList<Post> parseAllPostList(JSONObject resp) throws JSONException {
        //create output list
        ArrayList<Post> receivedPosts = new ArrayList<>();
        //get json array of posts
        JSONObject jdata = resp.getJSONObject("data");
        JSONArray jposts = jdata.getJSONArray("posts");
        //for each element in the array create a new topic object and add it to the array list
        for (int i = 0; i < jposts.length(); i++) {
            JSONObject curPost = jposts.getJSONObject(i);
            Post p = new Post(
                    curPost.getString("id"),
                    curPost.getString("title"),
                    curPost.getString("body"),
                    ("@" + curPost.getString("author")),
                    curPost.getBoolean("edited"),
                    curPost.getString("topic_name"),
                    curPost.getString("date"));
            receivedPosts.add(p);
        }
        return receivedPosts;
    }


    //convert JSON object from backend to arraylist of replies
    ArrayList<Reply> parseReplies(String postid, JSONObject resp) throws JSONException {
        //create output list
        ArrayList<Reply> receivedReplies = new ArrayList<>();
        //get json array of posts
        JSONObject jdata = resp.getJSONObject("data");
        JSONArray jreplies = jdata.getJSONArray("replies");
        //for each element in the array create a new topic object and add it to the array list
        for (int i = 0; i < jreplies.length(); i++) {
            JSONObject curReply = jreplies.getJSONObject(i);
            if (curReply.getString("post_id").equals(postid)) {

                Reply r = new Reply(
                        curReply.getString("id"),
                        curReply.getString("body"),
                        curReply.getString("author"),
                        curReply.getString("post_id"),
                        curReply.getString("date")
                );
                receivedReplies.add(r);

            }
        }
        return receivedReplies;
    }


}
