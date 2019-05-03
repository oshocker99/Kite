package com.team100.kite_master;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.team100.kite_master.favorites.FavoriteStorageHandler;
import com.team100.kite_master.forum.ForumTopicListFragment;
import com.team100.kite_master.forum.forum_data_classes.Post;
import com.team100.kite_master.login.LoginFragment;
import com.team100.kite_master.login.SaveSharedPreference;
import com.team100.kite_master.messages.MessagesFragment;
import com.team100.kite_master.networking.NetworkManager;
import com.team100.kite_master.favorites.FavoritesFragment;
import com.team100.kite_master.networking.VolleyListener;
import com.team100.kite_master.search.SearchFragment;
import com.team100.kite_master.settings.SettingsFragment;
import com.team100.kite_master.userdata.User;
import com.team100.kite_master.userdata.UserParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //global variables
    public String cur_screen;
    public User currentUser;
    private String server_ip;
    private ArrayList<Post> favoritePostList = new ArrayList<>();
    private ArrayList<String> favoritePostIDList = new ArrayList<>();
    private FavoriteStorageHandler fsh = new FavoriteStorageHandler();

    //global layout elements
    public DrawerLayout drawer;
    public Toolbar toolbar;
    public NavigationView navigationView;


    //on create method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate network manager
        if (SaveSharedPreference.getHostIp(MainActivity.this).length() != 0) {
            NetworkManager.getInstance(this).setUrl(SaveSharedPreference.getHostIp(MainActivity.this));
            NetworkManager.getInstance(this).setUserdata(SaveSharedPreference.getUserName(MainActivity.this), SaveSharedPreference.getPass(MainActivity.this));
        } else {
            NetworkManager.getInstance(this);
        }


        //setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //set keybaord pan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        //setup drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //set navigation view
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //instantiate user with blank fields
        currentUser = new User("", "", "", 0, false, false);

        //login - if they were previously logged in auto login, else require log in
        if (SaveSharedPreference.getUserName(MainActivity.this).length() == 0) {
            displayLoginScreen();
        } else {
            updateJWT();
        }
    }


    //back button goes to forum unless it is on forum, then it closes app
    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (cur_screen.equals("login") || cur_screen.equals("topic_list")) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        } else if (count > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    //handles selecting other fragments from nav drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }


    public void setDrawerItemSelection(int sel) {
        navigationView.getMenu().getItem(sel).setChecked(true);
    }

    //loads login screen
    private void displayLoginScreen() {
        cur_screen = "login";
        Fragment fragment = new LoginFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    private void logIn() {
        currentUser.setUsername(SaveSharedPreference.getUserName(MainActivity.this));
        server_ip = SaveSharedPreference.getHostIp(MainActivity.this);
        if (SaveSharedPreference.getFavoritesList(MainActivity.this).length() != 0) {
            favoritePostIDList = fsh.stringToFavID(SaveSharedPreference.getFavoritesList(MainActivity.this));
            favoritePostList = fsh.getAllFavPosts(favoritePostIDList);
        }


        displaySelectedScreen(R.id.nav_forum);
    }


    //NETWORK
    private void updateJWT() {
        NetworkManager.getInstance().login(SaveSharedPreference.getUserName(MainActivity.this), SaveSharedPreference.getPass(MainActivity.this), new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
                logIn();
            }

            @Override
            public void getError(VolleyError err) {
                logout();
            }
        });
    }


    public void logout() {
        SaveSharedPreference.setHostIp(MainActivity.this, "");
        SaveSharedPreference.setUserName(MainActivity.this, "");
        SaveSharedPreference.setFavoritesList(MainActivity.this, "");
        displayLoginScreen();
    }

    public void lockDrawer(Boolean lock) {
        if (lock) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }


    //getters and setters
    public void setServerIP(String ip) {
        server_ip = ip;
    }

    public String getServerIP() {
        return server_ip;
    }

    public void setSavedContextData(String username, String pass, String ip) {
        SaveSharedPreference.setUserName(MainActivity.this, username);
        SaveSharedPreference.setHostIp(MainActivity.this, ip);
        SaveSharedPreference.setPass(MainActivity.this, pass);
    }

    public String[] getUserData(){
        String[] u = new String[2];
        u[0] = SaveSharedPreference.getUserName(MainActivity.this);
        u[1] = SaveSharedPreference.getPass(MainActivity.this);
        return u;
    }


    public void setNavDrawerData(String username, String displayname) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navDisplayname = headerView.findViewById(R.id.nav_display_name);
        navDisplayname.setText(displayname);
        TextView navUsername = headerView.findViewById(R.id.nav_user_name);
        String atUsername = "@" + username;
        navUsername.setText(atUsername);
    }

    public void setCurScreen(String screenID) {
        cur_screen = screenID;
    }


    public void addFavoritePost(Post f) {
        if (favoritePostList != null) {
            if (!favoritePostIDList.contains(f.getPostID())) {
                favoritePostList.add(f);
                favoritePostIDList.add(f.getPostID());
                SaveSharedPreference.setFavoritesList(MainActivity.this, fsh.favIDtoString(favoritePostList));
            }
        } else {
            favoritePostList.add(f);
            favoritePostIDList.add(f.getPostID());
            SaveSharedPreference.setFavoritesList(MainActivity.this, fsh.favIDtoString(favoritePostList));
        }
    }


    public void removeFavoritePost(Post f) {
        System.out.println("PRE REMOVED LIST: " + favoritePostList.toString());
        if (favoritePostList != null) {
            if (favoritePostIDList.contains(f.getPostID())) {
                System.out.println("REMOVING");

                for (int i = 0; i < favoritePostList.size(); i++) {
                    if (favoritePostList.get(i).getPostID().equals(f.getPostID())) {
                        favoritePostList.remove(i);
                    }
                }

                for (int i = 0; i < favoritePostIDList.size(); i++) {
                    if (favoritePostIDList.get(i).equals(f.getPostID())) {
                        favoritePostIDList.remove(i);
                    }
                }
                System.out.println("REMOVED LIST: " + favoritePostList.toString());
                SaveSharedPreference.setFavoritesList(MainActivity.this, fsh.favIDtoString(favoritePostList));
            }
        }
    }


    public ArrayList<Post> getFavoritePostList() {
        return favoritePostList;
    }

    public ArrayList<String> getFavoritePostIDList() {
        return favoritePostIDList;
    }


    private void displaySelectedScreen(int itemId) {
        //creating fragment object
        Fragment fragment = null;
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_forum:
                cur_screen = "topic_list";
                fragment = new ForumTopicListFragment();
                break;
            case R.id.nav_search:
                cur_screen = "search";
                fragment = new SearchFragment();
                break;
            case R.id.nav_messages:
                cur_screen = "messages";
                fragment = new MessagesFragment();
                break;
            case R.id.nav_favorites:
                cur_screen = "favorites";
                fragment = new FavoritesFragment();
                break;
            case R.id.nav_settings:
                cur_screen = "settings";
                fragment = new SettingsFragment();
                break;
            case R.id.nav_logout:
                cur_screen = "login";
                logout();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            Bundle bundle = new Bundle();
            bundle.putStringArray("userData", currentUser.toArray());
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment).addToBackStack("tag");
            ft.commit();
        }
        drawer.closeDrawer(GravityCompat.START);
    }


}
