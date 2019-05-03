package com.team100.kite_master.search;

import com.team100.kite_master.forum.forum_data_classes.Post;


import java.util.ArrayList;

class SearchHelper {


    ArrayList<Post> search(String searchtext, ArrayList<Post> postlist) {
        ArrayList<Post> results = new ArrayList<>();
        if(searchtext.length() < 1){
            return results;
        }

        //user search
        if(searchtext.charAt(0) == '@'){
            //title search
            for (int i = 0; i < postlist.size(); i++) {
                if (postlist.get(i).getPostAuthor().toLowerCase().contains(searchtext.toLowerCase())) {
                    results.add(postlist.get(i));
                }
            }
        } else {
            //title search
            for (int i = 0; i < postlist.size(); i++) {
                if (postlist.get(i).getPostTitle().toLowerCase().contains(searchtext.toLowerCase())) {
                    results.add(postlist.get(i));
                }
            }
        }



        return results;
    }










}
