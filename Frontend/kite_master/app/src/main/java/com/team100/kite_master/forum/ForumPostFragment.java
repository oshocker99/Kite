package com.team100.kite_master.forum;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.team100.kite_master.MainActivity;
import com.team100.kite_master.R;
import com.team100.kite_master.forum.forum_data_classes.DateUtil;
import com.team100.kite_master.forum.forum_data_classes.Post;
import com.team100.kite_master.forum.forum_data_classes.Reply;
import com.team100.kite_master.networking.NetworkManager;
import com.team100.kite_master.networking.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ForumPostFragment extends Fragment implements View.OnClickListener {

    //declare global vars
    String postID;

    //declare error layout items
    ProgressBar loadingCircle;
    TextView errMessage;
    Button retry;
    Post currentPost;
    boolean isFavorited;

    //declare layout items
    ScrollView postScrollView;
    TextView postTitleView;
    TextView postTimeView;
    TextView postAuthorView;
    TextView postBodyView;
    MenuItem favorite;
    ListView replyListView;
    EditText replyBox;
    Button replyButton;
    FrameLayout border;
    FrameLayout betweenline;


    private CustomAdapter replyAdapter;
    private String postAuthor;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.forum_post, container, false);
        //receive bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            postID = bundle.getString("selected_post");
        }

        //LOCAL DEBUGGING
        System.out.println(" ");
        System.out.println("POST FRAGMENT:");
        System.out.println("CURRENT POST: " + postID);
        System.out.println(" ");

        //initialize layout elements
        loadingCircle = v.findViewById(R.id.topics_loading);
        errMessage = v.findViewById(R.id.error_message);
        postTitleView = v.findViewById(R.id.single_post_title);
        postTimeView = v.findViewById(R.id.single_post_time);
        postAuthorView = v.findViewById(R.id.single_post_author);
        postBodyView = v.findViewById(R.id.single_post_body);
        postScrollView = v.findViewById(R.id.post_scroll_view);
        retry = v.findViewById(R.id.retry_topics);
        replyListView = v.findViewById(R.id.reply_list_view);
        replyBox = v.findViewById(R.id.reply_box);
        replyButton = v.findViewById(R.id.reply_button);
        border = v.findViewById(R.id.between_replies_textbox);
        betweenline = v.findViewById(R.id.between_text_replies);

        //initialize boolean
        isFavorited = false;

        //set button on click listener
        retry.setOnClickListener(this);
        replyButton.setOnClickListener(this);

        //hide everything until post is gotten
        postScrollView.setVisibility(View.GONE);
        replyBox.setVisibility(View.GONE);
        replyButton.setVisibility(View.GONE);
        border.setVisibility(View.GONE);
        betweenline.setVisibility(View.GONE);



        //show loading circle until topics received
        loadingCircle.setVisibility(View.VISIBLE);

        //initialize custom adapter and set it to list view
        replyAdapter = new CustomAdapter();
        replyListView.setAdapter(replyAdapter);

        //show action menu
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        setHasOptionsMenu(true);

        //request posts
        requestPost(postID);


        //register for context menu if admin or mod
        if (((MainActivity) Objects.requireNonNull(getActivity())).currentUser.isAdmin() || ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.isMod()) {
            System.out.println("REGISTERED");
            registerForContextMenu(replyListView);
        }


        //set current screen
        ((MainActivity) Objects.requireNonNull(getActivity())).setCurScreen("post");
        ((MainActivity) Objects.requireNonNull(getActivity())).setDrawerItemSelection(0);

        //hide error elements
        errMessage.setVisibility(View.GONE);
        retry.setVisibility(View.GONE);
        return v;
    }

    //creates action bar menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_buttons, menu);
        MenuItem refresh = menu.findItem(R.id.menu_refresh);
        favorite = menu.findItem(R.id.menu_post_favorite);
        refresh.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle("Post");
    }


    //handle retry button click
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_topics:
                retry.setVisibility(View.GONE);
                errMessage.setVisibility(View.GONE);
                requestPost(postID);
                loadingCircle.setVisibility(View.VISIBLE);
                break;
            case R.id.reply_button:

                if(replyBox.getText().toString().length() > 0){
                    sendReply(replyBox.getText().toString());
                    replyBox.getText().clear();
                    replyBox.onEditorAction(EditorInfo.IME_ACTION_DONE);
                }
                break;
        }
    }


    //handles click of buttons in the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_post_favorite) {
            if (isFavorited) {
                ((MainActivity) Objects.requireNonNull(getActivity())).removeFavoritePost(currentPost);
                favorite.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_menu_favorite));
                isFavorited = false;
            } else {
                ((MainActivity) Objects.requireNonNull(getActivity())).addFavoritePost(currentPost);
                favorite.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_menu_star_filled));
                isFavorited = true;
            }
        }
        return true;
    }


    //NETWORKING
    //requests topic JSON object from backend
    public void requestPost(String postid) {
        NetworkManager.getInstance().requestPost(postid, new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
                try {
                    ForumParser fp = new ForumParser();
                    setViewElements(fp.parsePost(object));
                } catch (JSONException e) {
                    displayErrorRetry(e.toString());
                }
            }

            @Override
            public void getError(VolleyError err) {
                displayErrorRetry(err.toString());
            }
        });
    }


    public void displayErrorRetry(String err) {
        Toast.makeText(getActivity(), err + " ", Toast.LENGTH_LONG).show();
        loadingCircle.setVisibility(View.GONE);
        errMessage.setText("Connection Error\n Make sure your forum server is running.");
        errMessage.setVisibility(View.VISIBLE);
        retry.setVisibility(View.VISIBLE);
    }


    public void setViewElements(Post p) {
        currentPost = p;
        loadingCircle.setVisibility(View.GONE);
        postTitleView.setText(p.getPostTitle());
        String atAuthor = "@" + p.getPostAuthor();
        postAuthorView.setText(atAuthor);
        postAuthor = p.getPostAuthor();
        DateUtil d = new DateUtil();
        String date = d.getCleanDate(Long.parseLong(p.getPostTime()), "MM/dd/yy hh:mma");
        postTimeView.setText(date);
        postBodyView.setText(p.getPostBody());

        if (((MainActivity) Objects.requireNonNull(getActivity())).getFavoritePostIDList() != null) {
            System.out.println("HERE");
            if (((MainActivity) Objects.requireNonNull(getActivity())).getFavoritePostIDList().contains(p.getPostID())) {
                isFavorited = true;
                favorite.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_menu_star_filled));
            }
        }
        requestReplies(postID);
    }


    //REPLIES

    private ArrayList<Reply> replyList = new ArrayList<>();





    /**
     * MENU
     */

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.reply_list_view) {
            MenuInflater inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
            inflater.inflate(R.menu.post_list_context_menu, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                System.out.println("DELETING: " + replyList.get(info.position).getReplyID());
                deleteReply(replyList.get(info.position).getReplyID());
                SystemClock.sleep(1000);
                loadingCircle.setVisibility(View.VISIBLE);
                requestReplies(postID);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }




    //custom topic adapter class
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return replyList.size();
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
            view = getLayoutInflater().inflate(R.layout.forum_post_reply_card, null);
            // initialize text views
            TextView replyAuthor = view.findViewById(R.id.text_author);
            TextView replyText = view.findViewById(R.id.text_reply);
            TextView replyTime = view.findViewById(R.id.text_time);
            // iterate through list to set topic entries
            replyText.setText(replyList.get(i).getReplyText());
            replyAuthor.setText(replyList.get(i).getReplyAuthor());
            DateUtil d = new DateUtil();
            String timeago = d.getTimeAgo(Long.parseLong(replyList.get(i).getReplyMilis()));
            replyTime.setText(timeago);
            //add images here when support is added
            return view;
        }
    }


    //sets post list array list and notifies adapter to update
    private void setReplyList(ArrayList<Reply> p) {
        //update global topic list
        replyList = new ArrayList<>(p);
        //hide one of the borders if there arent any replies
        if(replyList.size() == 0){
            border.setVisibility(View.GONE);
            betweenline.setVisibility(View.GONE);
        } else {
            border.setVisibility(View.VISIBLE);
            betweenline.setVisibility(View.VISIBLE);
        }
        //sort topic list in alphabetical order
        Collections.sort(replyList);
        //notify adapter to update its list with the new topics
        replyAdapter.notifyDataSetChanged();
        //make sure scroll view is showing
        postScrollView.setVisibility(View.VISIBLE);
        replyBox.setVisibility(View.VISIBLE);
        replyButton.setVisibility(View.VISIBLE);
        border.setVisibility(View.VISIBLE);
        betweenline.setVisibility(View.VISIBLE);
        //hide loading circle
        loadingCircle.setVisibility(View.GONE);
        //show error message if no posts

    }


    //NETWORKING
    //requests topic list JSON object from backend
    public void requestReplies(final String postID) {
        NetworkManager.getInstance().requestReplies(new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
                try {
                    ForumParser fp = new ForumParser();
                    setReplyList(fp.parseReplies(postID, object));
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


    //send reply
    private void sendReply(final String body) {
        String author = ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.getUsername();
        System.out.println("SENDING REPLY: " + postID + " | " + author + " | " + body);
        NetworkManager.getInstance().sendReply(postID, author, body, new VolleyListener<String>() {
            @Override
            public void getResult(String string) {
                requestReplies(postID);
                Toast.makeText(getActivity(), "Reply Sent!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void getError(VolleyError err) {
                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    //requests topic list JSON object from backend
    public void deleteReply(String replyid) {
        NetworkManager.getInstance().deleteReply(replyid, new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
            }

            @Override
            public void getError(VolleyError err) {
                System.out.println("Error Deleting Reply: " + err.toString());
            }
        });
    }

}
