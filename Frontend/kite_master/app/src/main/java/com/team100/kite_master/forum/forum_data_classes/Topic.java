package com.team100.kite_master.forum.forum_data_classes;

import android.support.annotation.NonNull;

public class Topic implements Comparable<Topic>{

    private String topicName;
    private String topicDescription;
    private String topicID;

    //constructor for topics
    public Topic(String name, String description) {
        topicID = name;
        topicName = formatName(name);
        topicDescription = description;

    }
    //returns topic id (unformatted name)
    public String getTopicID() { return topicID; }

    //returns topic name (formatted)
    public String getName() { return topicName; }

    //returns description of topic
    public String getDescription() {
        return topicDescription;
    }

    //overrides compare to to allow alphabetical sorting of the topic list
    @Override
    public int compareTo(Topic comparetopic) {
        return this.getName().compareTo(comparetopic.getName());
    }

    //returns a string containing the topic id and description
    @NonNull
    @Override
    public String toString(){
        return "|" + topicID+" -> "+topicDescription+"|";
    }

    //formats the name so only the first letter of each word is uppercase
    private String formatName(String name) {
        String str = name.toLowerCase();
        StringBuilder s = new StringBuilder(str.length());
        String words[] = str.split(" ");
        for (String word : words) {
            s.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return s.toString();
    }

}
