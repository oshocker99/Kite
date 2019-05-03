package com.team100.kite_master.favorites;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.team100.kite_master.MainActivity;
import com.team100.kite_master.R;
import com.team100.kite_master.forum.ForumPostFragment;
import com.team100.kite_master.forum.forum_data_classes.DateUtil;
import com.team100.kite_master.forum.forum_data_classes.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class FavoritesFragment extends Fragment implements View.OnClickListener {

    ProgressBar loadingCircle;
    TextView errMessage;
    Button retryTopics;

    //view item declaration
    ArrayList<Post> favPostList = new ArrayList<>();
    ListView postListView;
    CustomAdapter favoritePostAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favorites_fragment, container, false);

        //link view items
        postListView = v.findViewById(R.id.list_view);
        loadingCircle = v.findViewById(R.id.topics_loading);
        errMessage = v.findViewById(R.id.error_message);
        retryTopics = v.findViewById(R.id.retry_topics);

        //set on click listeners
        retryTopics.setOnClickListener(this);


        //show loading circle until topics received
        loadingCircle.setVisibility(View.VISIBLE);
        //hide error layout elements
        errMessage.setVisibility(View.GONE);
        retryTopics.setVisibility(View.GONE);

        //initialize custom adapter and set it to list view
        favoritePostAdapter = new CustomAdapter();
        postListView.setAdapter(favoritePostAdapter);

        //show action bar buttons
        setHasOptionsMenu(true);

        setPostList();

        //set current screen
        ((MainActivity) Objects.requireNonNull(getActivity())).setCurScreen("favorites");
        ((MainActivity) Objects.requireNonNull(getActivity())).setDrawerItemSelection(3);

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle("Favorites");

        //set on click listener for menu items
        postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPost(favPostList.get(position).getPostID());
                Animation animation1 = new AlphaAnimation(0.3f, 4.0f);
                animation1.setDuration(4000);
                view.startAnimation(animation1);
            }
        });
        setPostList();
    }


    //handles fragment on click listeners
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry_topics) {
            retryTopics.setVisibility(View.GONE);
            errMessage.setVisibility(View.GONE);
            setPostList();
            loadingCircle.setVisibility(View.VISIBLE);
        }
    }


    //custom topic adapter class
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (favPostList == null) {
                return 0;
            } else {
                return favPostList.size();
            }
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.forum_post_list_card, null);
            // initialize text views
            TextView topicTitle = view.findViewById(R.id.text_reply);
            TextView topicAuthor = view.findViewById(R.id.text_author);
            TextView topicTime = view.findViewById(R.id.text_time);
            // iterate through list to set topic entries
            topicTitle.setText(favPostList.get(i).getPostTitle());
            topicAuthor.setText(favPostList.get(i).getPostAuthor());
            DateUtil d = new DateUtil();
            String timeago = d.getTimeAgo(Long.parseLong(favPostList.get(i).getPostTime()));
            topicTime.setText(timeago);
            //add images here when support is added
            return view;
        }
    }

    //sets post list array list and notifies adapter to update
    @SuppressLint("SetTextI18n")
    private void setPostList() {
        favPostList = ((MainActivity) Objects.requireNonNull(getActivity())).getFavoritePostList();
        System.out.println(favPostList.toString());
        //sort topic list in alphabetical order
        if (favPostList != null && favPostList.size() != 0) {
            System.out.println("UPDATING LIST");
            Collections.sort(favPostList);
            //notify adapter to update its list with the new topics
            favoritePostAdapter.notifyDataSetChanged();
            //hide loading circle
            loadingCircle.setVisibility(View.GONE);
            errMessage.setVisibility(View.GONE);
        } else {
            loadingCircle.setVisibility(View.GONE);
            errMessage.setText("You have not favorited any posts");
            errMessage.setVisibility(View.VISIBLE);
        }

    }


    //switch to post fragment when one is clicked
    public void openPost(String postID) {
        Fragment fragment = new ForumPostFragment();
        Bundle bundle = new Bundle();
        bundle.putString("selected_post", postID);
        fragment.setArguments(bundle);
        FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment).addToBackStack("tag");
        ft.commit();
    }

}