package com.team100.kite_master.forum;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.team100.kite_master.MainActivity;
import com.team100.kite_master.R;
import com.team100.kite_master.forum.forum_data_classes.DateUtil;
import com.team100.kite_master.forum.forum_data_classes.Post;
import com.team100.kite_master.networking.NetworkManager;
import com.team100.kite_master.networking.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;


public class ForumPostListFragment extends Fragment implements View.OnClickListener {

    //declare global vars
    private String[] userdata;
    private String topic;
    ArrayList<Post> postList = new ArrayList<>();
    CustomAdapter topicAdapter;

    //declare error layout items
    ProgressBar loadingCircle;
    TextView errMessage;
    Button retryTopics;

    //view item declaration
    ListView postListView;
    FloatingActionButton newPostFab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.forum_post_list, container, false);
        //receive bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userdata = bundle.getStringArray("user_data");
            topic = bundle.getString("selected_topic");
        }

        //DEBUGGING
        System.out.println(" ");
        System.out.println("POST LIST FRAGMENT:");
        System.out.println("CURRENT TOPIC: " + topic);
        System.out.println("USER: " + Arrays.toString(userdata));
        System.out.println(" ");

        //link view items
        postListView = v.findViewById(R.id.list_view);
        loadingCircle = v.findViewById(R.id.topics_loading);
        errMessage = v.findViewById(R.id.error_message);
        newPostFab = v.findViewById(R.id.new_post_fab);
        retryTopics = v.findViewById(R.id.retry_topics);


        //register for context menu if admin or mod
        if (((MainActivity) Objects.requireNonNull(getActivity())).currentUser.isAdmin() || ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.isMod()) {
            registerForContextMenu(postListView);
        }


        //set on click listeners
        newPostFab.setOnClickListener(this);
        retryTopics.setOnClickListener(this);


        //show loading circle until topics received
        loadingCircle.setVisibility(View.VISIBLE);
        //hide error layout elements
        errMessage.setVisibility(View.GONE);
        retryTopics.setVisibility(View.GONE);
        newPostFab.show();

        //initialize custom adapter and set it to list view
        topicAdapter = new CustomAdapter();
        postListView.setAdapter(topicAdapter);

        //show action bar buttons
        setHasOptionsMenu(true);

        //set current screen and nav drawer check
        ((MainActivity) Objects.requireNonNull(getActivity())).setCurScreen("post_list");
        ((MainActivity) Objects.requireNonNull(getActivity())).setDrawerItemSelection(0);

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle(topic);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).show();

        //set on click listener for menu items
        postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPost(postList.get(position).getPostID());
                Animation animation1 = new AlphaAnimation(0.3f, 4.0f);
                animation1.setDuration(4000);
                view.startAnimation(animation1);
            }
        });

        //hides FAB when scrolling
        postListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 1 && newPostFab.isShown()) {
                    newPostFab.hide();
                } else {
                    newPostFab.show();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        //request the list of posts for the selected topic
        postListView.setVisibility(View.GONE);
        requestPostList(topic);
    }


    //creates action bar menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_buttons, menu);
        MenuItem favorite = menu.findItem(R.id.menu_post_favorite);
        favorite.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //handles fragment on click listeners
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_topics:
                retryTopics.setVisibility(View.GONE);
                errMessage.setVisibility(View.GONE);
                postListView.setVisibility(View.GONE);
                requestPostList(topic);
                loadingCircle.setVisibility(View.VISIBLE);
                break;
            case R.id.new_post_fab:
                openNewPost();
                break;
        }
    }

    //handles clicks of the refresh button in the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            loadingCircle.setVisibility(View.VISIBLE);
            postListView.setVisibility(View.GONE);
            requestPostList(topic);
        }
        return true;
    }


    /**
     * MENU
     */

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.list_view) {
            MenuInflater inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
            inflater.inflate(R.menu.post_list_context_menu, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                System.out.println("DELETING: " + postList.get(info.position).getPostID());
                deletePost(postList.get(info.position).getPostID());
                SystemClock.sleep(1000);
                loadingCircle.setVisibility(View.VISIBLE);
                postListView.setVisibility(View.GONE);
                requestPostList(topic);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    //custom topic adapter class
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return postList.size();
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
            topicTitle.setText(postList.get(i).getPostTitle());
            topicAuthor.setText(postList.get(i).getPostAuthor());
            DateUtil d = new DateUtil();
            String timeago = d.getTimeAgo(Long.parseLong(postList.get(i).getPostTime()));
            topicTime.setText(timeago);
            //add images here when support is added
            return view;
        }
    }

    //sets post list array list and notifies adapter to update
    @SuppressLint("SetTextI18n")
    private void setPostList(ArrayList<Post> p) {
        //update global topic list
        postList = new ArrayList<>(p);
        //sort topic list in alphabetical order
        Collections.sort(postList);
        //notify adapter to update its list with the new topics
        topicAdapter.notifyDataSetChanged();
        //hide loading circle
        loadingCircle.setVisibility(View.GONE);
        postListView.setVisibility(View.VISIBLE);
        //show error message if no posts
        if (postList.size() == 0) {
            errMessage.setText("There are no posts in this topic");
            errMessage.setVisibility(View.VISIBLE);
        } else {
            errMessage.setVisibility(View.GONE);
        }
    }

    //switch to new post fragment when fab is clicked
    public void openNewPost() {
        Fragment fragment = new ForumNewPostFragment();
        Bundle bundle = new Bundle();
        bundle.putString("new_post_topic", topic);
        fragment.setArguments(bundle);
        FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.replace(R.id.content_frame, fragment).addToBackStack("tag");
        ft.commit();
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

    //NETWORKING
    //requests topic list JSON object from backend
    public void requestPostList(String topic) {
        NetworkManager.getInstance().requestPostList(topic, new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
                try {
                    ForumParser fp = new ForumParser();
                    setPostList(fp.parsePostList(object));
                } catch (JSONException e) {
                    System.out.println("Post List Error");
                }
            }

            @Override
            public void getError(VolleyError err) {
                System.out.println("Post List Error");
            }
        });
    }

    //delete post from database
    public void deletePost(String postid) {
        NetworkManager.getInstance().deletePost(postid, new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
            }

            @Override
            public void getError(VolleyError err) {
                System.out.println("Error Deleting Post: " + err.toString());
            }
        });
    }


}
