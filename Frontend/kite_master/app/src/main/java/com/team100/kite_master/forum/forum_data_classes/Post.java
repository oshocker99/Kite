package com.team100.kite_master.forum.forum_data_classes;

import android.support.annotation.NonNull;

public class Post implements Comparable<Post> {

    private String postTitle;
    private String postTopic;
    private String postAuthor;
    private String postBody;
    private String postID;
    private String postDate;
    private boolean postEdited;

    //constructor for topics
    public Post(String id, String title, String body, String author, Boolean edited, String topic, String date) {
        postTitle = title;
        postTopic = topic;
        postAuthor = author;
        postBody = body;
        postID = id;
        postDate = date;
        postEdited = edited;
    }

    //returns topic id (unformatted name)
    public String getPostTitle() {
        return postTitle;
    }

    //returns topic name (formatted)
    public String getPostTopic() {
        return postTopic;
    }

    //returns description of topic
    public String getPostBody() {
        return postBody;
    }

    //returns description of topic
    public String getPostAuthor() {
        return postAuthor;
    }

    //returns description of topic
    public String getPostID() {
        return postID;
    }

    //returns description of topic
    public String getPostDate() {
        return postDate;
    }

    //returns description of topic
    public String getPostTime() {
        String[] timearr = postDate.split(" ");
        return timearr[0];
    }

    //returns description of topic
    public Boolean getPostEdited() {
        return postEdited;
    }

    //overrides compare to to allow alphabetical sorting of the topic list
    @Override
    public int compareTo(Post comparepost) {
        return Integer.compare(Integer.parseInt(comparepost.getPostTime()), Integer.parseInt(this.getPostTime()));
    }

    //returns a string containing the topic id and description
    @NonNull
    @Override
    public String toString() {
        return "|" + postTitle + " -> " + postTopic + "->" + postAuthor + "->" + postBody;
    }

}
