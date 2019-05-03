package com.team100.kite_master.userdata;

public class User {

    public String username;
    private String displayname;
    private String bio;
    private int postCount;
    private boolean isAdministrator;
    private boolean isModerator;


    public User(String username, String displayname, String bio, int postCount, boolean isAdmin, boolean isMod) {
        this.username = username;
        this.displayname = displayname;
        this.bio = bio;
        this.postCount = postCount;
        isAdministrator = isAdmin;
        isModerator = isMod;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public void setAdministrator(boolean administrator) {
        isAdministrator = administrator;
    }

    public void setModerator(boolean moderator) {
        isModerator = moderator;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayname() {
        return displayname;
    }

    public String getBio() {
        return bio;
    }

    public int getPostCount() {
        return postCount;
    }

    public boolean isAdmin() {
        return isAdministrator;
    }

    public boolean isMod() {
        return isModerator;
    }


    public void printUserDetails(){
        System.out.println(
                "[Username: " + username + "\n" +
                        "DisplayName: " + displayname + "\n" +
                        "Bio: " + bio + "\n" +
                        "Postcount: " + postCount + "\n" +
                        "isMod: " + isModerator + "\n" +
                        "isAdmin: " + isAdministrator + "]");

    }


    public String[] toArray(){
        String[] c = new String[6];
        c[0] = username;
        c[1] = displayname;
        c[2] = bio;
        c[3] = Integer.toString(postCount);
        c[4] = Boolean.toString(isModerator);
        c[5] = Boolean.toString(isAdministrator);
        return c;
    }


}
