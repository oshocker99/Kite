package com.team100.kite_master.forum;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.team100.kite_master.MainActivity;
import com.team100.kite_master.R;
import com.team100.kite_master.forum.forum_data_classes.Topic;
import com.team100.kite_master.networking.NetworkManager;
import com.team100.kite_master.networking.VolleyListener;
import com.team100.kite_master.userdata.User;
import com.team100.kite_master.userdata.UserParser;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;


public class ForumTopicListFragment extends Fragment implements View.OnClickListener {

    //declare global variables
    private String[] userdata;

    private ProgressBar loadingCircle;
    private TextView errMessage;
    private Button retryTopics;
    private ListView topicListView;

    //declare data structures
    private ArrayList<Topic> topicList = new ArrayList<>();
    private CustomAdapter topicAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.forum_topic_list, container, false);


        //DEBUGGING
        System.out.println(" ");
        System.out.println("POST LIST FRAGMENT:");
        System.out.println("USER: " + Arrays.toString(userdata));
        System.out.println(" ");

        //initialize layout items
        //declare layout items
        topicListView = v.findViewById(R.id.list_view);
        loadingCircle = v.findViewById(R.id.topics_loading);
        errMessage = v.findViewById(R.id.error_message);
        retryTopics = v.findViewById(R.id.retry_topics);

        //request topics from the backend
        requestTopics();

        //fill user data
        requestUser(((MainActivity) Objects.requireNonNull(getActivity())).currentUser.getUsername());

        //show loading circle until topics received
        loadingCircle.setVisibility(View.VISIBLE);
        //hide error elements
        errMessage.setVisibility(View.GONE);
        retryTopics.setVisibility(View.GONE);
        //set button on click listener
        retryTopics.setOnClickListener(this);
        //returns value of whatever list item is clicked
        topicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openTopic(topicList.get(position).getTopicID());
            }
        });

        //initialize custom adapter and set it to list view
        topicAdapter = new CustomAdapter();
        topicListView.setAdapter(topicAdapter);

        //show the action bar and buttons
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        setHasOptionsMenu(true);


        //set current screen
        ((MainActivity) Objects.requireNonNull(getActivity())).setCurScreen("topic_list");
        ((MainActivity) Objects.requireNonNull(getActivity())).setDrawerItemSelection(0);






        return v;
    }

    //creates options menu in action bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_buttons, menu);
        MenuItem favorite = menu.findItem(R.id.menu_post_favorite);
        favorite.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set title
        Objects.requireNonNull(getActivity()).setTitle("Forum");
        System.out.println("IS HE Admin: " + ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.isAdmin());
        //register for context menu if admin or mod
    }

    //fragment on click handler
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry_topics) {
            retryTopics.setVisibility(View.GONE);
            errMessage.setVisibility(View.GONE);
            requestTopics();
            loadingCircle.setVisibility(View.VISIBLE);
        }
    }

    //handles click of buttons in the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            loadingCircle.setVisibility(View.VISIBLE);
            requestTopics();
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
                System.out.println("DELETING: " + topicList.get(info.position).getTopicID());
                deleteTopic(topicList.get(info.position).getTopicID());
                SystemClock.sleep(1000);
                loadingCircle.setVisibility(View.VISIBLE);
                requestTopics();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    //custom topic adapter class
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return topicList.size();
        }

        @Override
        public Object getItem(int i) {
            return topicList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.forum_topic_list_item, null);
            // initialize text views
            TextView topicTitle = view.findViewById(R.id.text_reply);
            TextView topicDescription = view.findViewById(R.id.text_description);
            // iterate through list to set topic entries
            topicTitle.setText(topicList.get(i).getName());
            topicDescription.setText(topicList.get(i).getDescription());
            return view;
        }
    }

    //sets topic list and updates the list view
    public void setTopicList(ArrayList<Topic> t) {
        //update global topic list
        topicList = new ArrayList<>(t);
        //sort topic list
        Collections.sort(topicList);
        //notify adapter to update its list with the new topics
        topicAdapter.notifyDataSetChanged();
        //hide loading circle
        loadingCircle.setVisibility(View.GONE);
    }


    //switch to new fragment after list item is selected
    public void openTopic(String topic) {
        Fragment fragment = new ForumPostListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("selected_topic", topic);
        fragment.setArguments(bundle);
        FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment).addToBackStack("tag");
        ft.commit();
    }


    //sets user info in main activity and the drawer labels are updated
    public void setUserInfo(User us) {
        //set all the data fields for current user
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setUsername(us.getUsername());
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setAdministrator(us.isAdmin());
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setModerator(us.isMod());
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setPostCount(us.getPostCount());
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setBio(us.getBio());
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setDisplayname(us.getDisplayname());
        //set nav drawer data
        ((MainActivity) Objects.requireNonNull(getActivity())).setNavDrawerData(us.getUsername(), us.getDisplayname());
        //set correct userdata array
        userdata = ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.toArray();

        if (((MainActivity) Objects.requireNonNull(getActivity())).currentUser.isAdmin()) {
            registerForContextMenu(topicListView);
        }
    }

    //NETWORKING
    //requests topic JSON object from backend
    public void requestTopics() {
        NetworkManager.getInstance().requestTopics(new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
                try {
                    ForumParser f = new ForumParser();
                    setTopicList(f.parseTopics(object));
                } catch (JSONException e) {
                    System.out.println("Topic Parse Error");
                }
            }
            @Override
            public void getError(VolleyError err) {
                System.out.println("Drawer User Error");
            }
        });
    }


    //get user data on first load
    public void requestUser(String username) {
        NetworkManager.getInstance().requestUserData(username, new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
                try {
                    UserParser up = new UserParser();
                    setUserInfo(up.parseUserInfo(object));
                } catch (JSONException e) {
                    System.out.println("Drawer User Error");
                }
            }
            @Override
            public void getError(VolleyError err) {
                System.out.println("Drawer User Error");
            }
        });
    }


    //requests topic list JSON object from backend
    public void deleteTopic(String topicid) {
        NetworkManager.getInstance().deleteTopic(topicid, new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
            }

            @Override
            public void getError(VolleyError err) {
                System.out.println("Error Deleting Topic: " + err.toString());
            }
        });
    }
}
