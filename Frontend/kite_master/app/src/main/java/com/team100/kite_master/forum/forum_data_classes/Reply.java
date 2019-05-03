package com.team100.kite_master.forum.forum_data_classes;

import android.support.annotation.NonNull;

public class Reply implements Comparable<Reply> {


    private String replyID;
    private String replyText;
    private String replyAuthor;
    private String replyPostID;
    private String replyDate;

    //constructor for topics
    public Reply(String id, String body, String author, String postID, String date) {
        replyID = id;
        replyText = body;
        replyAuthor = author;
        replyPostID = postID;
        replyDate = date;
    }

    //returns reply id (unformatted name)
    public String getReplyID() {
        return replyID;
    }

    //returns topic name (formatted)
    public String getReplyText() {
        return replyText;
    }

    //returns description of topic
    public String getReplyAuthor() {
        return "@" + replyAuthor;
    }

    //returns description of topic
    public String getReplyPostID() {
        return replyPostID;
    }

    //returns description of topic
    public String getReplyDate() {
        return replyDate;
    }

    //returns description of topic
    public String getReplyMilis() {
        String[] timearr = replyDate.split(" ");
        return timearr[0];
    }
    //overrides compare to to allow alphabetical sorting of the topic list
    @Override
    public int compareTo(Reply compareply) {
        return Integer.compare(Integer.parseInt(compareply.getReplyMilis()), Integer.parseInt(this.getReplyMilis()));
    }

    //returns a string containing the topic id and description
    @NonNull
    @Override
    public String toString() {
        return "|" + replyPostID + " -> " + replyText + "->" + replyAuthor + "->" + replyDate;
    }

}