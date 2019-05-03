package com.team100.kite_master.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.team100.kite_master.MainActivity;
import com.team100.kite_master.R;
import com.team100.kite_master.forum.ForumTopicListFragment;
import com.team100.kite_master.networking.NetworkManager;
import com.team100.kite_master.networking.VolleyListener;

import org.json.JSONObject;

import java.util.Objects;


public class LoginFragment extends Fragment implements View.OnClickListener {

    //layout elements
    private Button loginButton;
    private Button registerButton;
    private EditText loginUsername;
    private EditText loginPassword;

    //true if ip connects to an actual kite server
    private boolean successfulIP;

    //server ip address
    private String server_ip;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_screen, container, false);

        //instantiate layout elements
        loginUsername = v.findViewById(R.id.login_username);
        loginPassword = v.findViewById(R.id.login_password);
        loginButton = v.findViewById(R.id.login_button);
        registerButton = v.findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set title
        Objects.requireNonNull(getActivity()).setTitle("Login");
        //hide action bar
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).hide();
        //start with unconnected ip
        successfulIP = false;

        showLoginScreen();
    }


    //handle button clicks
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_button) {
            if (successfulIP) {
                System.out.println("SET IP TO: " + server_ip);
                loginPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);
                loginUsername.onEditorAction(EditorInfo.IME_ACTION_DONE);
                login(loginUsername.getText().toString(), loginPassword.getText().toString());
            } else {
                checkIP(loginUsername.getText().toString());
                loginUsername.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        } else if (v.getId() == R.id.register_button){
            loginPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);
            loginUsername.onEditorAction(EditorInfo.IME_ACTION_DONE);
            if(loginPassword.getText().toString().length()> 8){
                register(loginUsername.getText().toString(), loginPassword.getText().toString());

            } else {
                loginPassword.setText("");
                Toast.makeText(getActivity(), "8+ characters please", Toast.LENGTH_LONG).show();
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private void showLoginScreen() {
        //lock drawer
        ((MainActivity) Objects.requireNonNull(getActivity())).lockDrawer(true);
        //if ip is kite's then show the login, otherwise show the set ip
        if (successfulIP) {
            loginUsername.getText().clear();
            loginUsername.setHint("Username");
            loginUsername.setVisibility(View.VISIBLE);
            loginPassword.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            loginButton.setText("Login");
        } else {
            loginUsername.setHint("IP Address (ex. 10.0.1.1)");
            loginUsername.setVisibility(View.VISIBLE);
            loginPassword.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            loginButton.setText("Set IP");
        }
    }


    public void checkIP(String ip) {
        NetworkManager.getInstance().testIP(ip, new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
                System.out.println("OUTPUT: " + object.toString());
                Toast.makeText(getActivity(), "Connected!" + " ", Toast.LENGTH_LONG).show();
                successfulIP = true;
                server_ip = loginUsername.getText().toString();
                ((MainActivity) Objects.requireNonNull(getActivity())).setServerIP(server_ip);
                showLoginScreen();
            }

            @Override
            public void getError(VolleyError err) {
                System.out.println(err.toString());
                Toast.makeText(getActivity(), "Network Error, Try Again?", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void moveToForumFrag(String username, String pass, String ip) {
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setUsername(username);
        //unlock drawer
        ((MainActivity) Objects.requireNonNull(getActivity())).lockDrawer(false);
        //set current username and ip
        ((MainActivity) Objects.requireNonNull(getActivity())).setSavedContextData(username, pass, ip);
        //launch forum fragment
        Fragment fragment = new ForumTopicListFragment();
        FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }


    //NETWORK
    private void login(final String username, final String password) {
        NetworkManager.getInstance().login(username, password, new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
                Toast.makeText(getActivity(), "Logging In!" + " ", Toast.LENGTH_LONG).show();
                moveToForumFrag(username, password, server_ip);
            }

            @Override
            public void getError(VolleyError err) {
                if (err.toString().equals("com.android.volley.AuthFailureError")) {
                    Toast.makeText(getActivity(), "Incorrect Password", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
                }
                loginPassword.getText().clear();
            }
        });
    }


    //NETWORK
    private void register(final String username, final String password) {
        NetworkManager.getInstance().createUser(username, password, new VolleyListener<String>() {
            @Override
            public void getResult(String object) {
                Toast.makeText(getActivity(), "Registered!" + " ", Toast.LENGTH_LONG).show();
                moveToForumFrag(username, password, server_ip);
            }

            @Override
            public void getError(VolleyError err) {
                if (err.networkResponse.statusCode == 400) {
                    Toast.makeText(getActivity(), "This user already exists!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
                }
                loginPassword.getText().clear();
            }
        });
    }
}