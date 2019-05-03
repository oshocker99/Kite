package com.team100.kite_master.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.team100.kite_master.MainActivity;
import com.team100.kite_master.R;
import com.team100.kite_master.networking.NetworkManager;
import com.team100.kite_master.networking.VolleyListener;

import org.json.JSONObject;

import java.util.Objects;


public class SettingsFragment extends Fragment implements View.OnClickListener {

    //text fields
    private EditText new_pass;
    private EditText username_delete;
    private EditText username_change_permissions;
    private EditText topic_new_name;
    private EditText topic_new_desc;
    //buttons
    private CheckBox modCheckBox;
    private CheckBox adminCheckBox;

    private Button change_pass_button;
    private Button delete_user_button;
    private Button update_permissions_button;
    private Button new_topic_button;

    private TextView title_text;


    private String username;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment, container, false);


        new_pass = v.findViewById(R.id.update_password);
        username_delete = v.findViewById(R.id.delete_user);
        username_change_permissions = v.findViewById(R.id.change_user_permissions);
        topic_new_name = v.findViewById(R.id.new_topic_name);
        topic_new_desc = v.findViewById(R.id.new_topic_desc);

        modCheckBox = v.findViewById(R.id.mod_check_box);
        adminCheckBox = v.findViewById(R.id.admin_check_box);

        change_pass_button = v.findViewById(R.id.new_pass_button);
        delete_user_button = v .findViewById(R.id.delete_user_button);
        update_permissions_button = v.findViewById(R.id.update_user_button);
        new_topic_button = v.findViewById(R.id.add_topic_button);

        title_text = v.findViewById(R.id.settings_title);

        change_pass_button.setOnClickListener(this);
        delete_user_button.setOnClickListener(this);
        update_permissions_button.setOnClickListener(this);
        new_topic_button.setOnClickListener(this);

        username = ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.getUsername();


        //hide and show based on access level
        if(!((MainActivity) Objects.requireNonNull(getActivity())).currentUser.isAdmin()){
            username_delete.setVisibility(View.GONE);
            username_change_permissions.setVisibility(View.GONE);
            topic_new_name.setVisibility(View.GONE);
            topic_new_desc.setVisibility(View.GONE);
            modCheckBox.setVisibility(View.GONE);
            adminCheckBox.setVisibility(View.GONE);
            delete_user_button.setVisibility(View.GONE);
            update_permissions_button.setVisibility(View.GONE);
            new_topic_button.setVisibility(View.GONE);
            title_text.setText("Change Password");
        }




        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle("Settings");

        //set current screen
        ((MainActivity) Objects.requireNonNull(getActivity())).setCurScreen("settings");
        ((MainActivity) Objects.requireNonNull(getActivity())).setDrawerItemSelection(4);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_pass_button:
                if(new_pass.getText().toString().length() > 7){
                    changePassword(new_pass.getText().toString());
                    new_pass.setText("");
                } else {
                    Toast.makeText(getActivity(), "Enter a password at least 8 characters long", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.update_user_button:
                if(username_change_permissions.getText().toString().length() > 0){
                    updateUser(username_change_permissions.getText().toString(), modCheckBox.isChecked(), adminCheckBox.isChecked());
                    username_change_permissions.setText("");
                    modCheckBox.setChecked(false);
                    adminCheckBox.setChecked(false);
                } else {
                    Toast.makeText(getActivity(), "Enter a username to update", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.delete_user_button:
                if(username_delete.getText().toString().length() > 0){
                    deleteUser(username_delete.getText().toString());
                    username_delete.setText("");
                } else {
                    Toast.makeText(getActivity(), "Enter a username to delete", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.add_topic_button:
                if(topic_new_name.getText().toString().length() > 0 && topic_new_desc.getText().toString().length() > 0){
                    addTopic(topic_new_name.getText().toString(), topic_new_desc.getText().toString());
                    topic_new_name.setText("");
                    topic_new_desc.setText("");
                } else {
                    Toast.makeText(getActivity(), "Enter a topic name and description", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }





    //NETWORKING

    //update password of current user
    public void changePassword(String password) {
        NetworkManager.getInstance().updatePassword(username,password, new VolleyListener<String>() {
            @Override
            public void getResult(String object) {
                Toast.makeText(getActivity(), "Password Updated", Toast.LENGTH_LONG).show();
            }

            @Override
            public void getError(VolleyError err) {
                Toast.makeText(getActivity(), "Error Updating Password", Toast.LENGTH_LONG).show();
            }
        });
    }


    //delete any user
    public void deleteUser(String username) {
        NetworkManager.getInstance().deleteUser(username, new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
                Toast.makeText(getActivity(), "User Deleted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void getError(VolleyError err) {
                Toast.makeText(getActivity(), "User Deleted", Toast.LENGTH_LONG).show();
            }
        });
    }

    //update any user
    public void updateUser(String username, boolean ismod, boolean isadmin) {
        NetworkManager.getInstance().updateUser(username,ismod,isadmin, new VolleyListener<String>() {
            @Override
            public void getResult(String object) {
                Toast.makeText(getActivity(), "User Updated", Toast.LENGTH_LONG).show();
            }

            @Override
            public void getError(VolleyError err) {
                Toast.makeText(getActivity(), "Error Updating User", Toast.LENGTH_LONG).show();
            }
        });
    }


    //add a new topic
    public void addTopic(final String name, String descriprion) {
        NetworkManager.getInstance().addTopic(name, descriprion, new VolleyListener<String>() {
            @Override
            public void getResult(String object) {
                Toast.makeText(getActivity(), "Topic " + name + " Added", Toast.LENGTH_LONG).show();
            }

            @Override
            public void getError(VolleyError err) {
                Toast.makeText(getActivity(), "Error Adding Topic", Toast.LENGTH_LONG).show();
            }
        });
    }





}