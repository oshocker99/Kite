package com.example.httpcommunicationwithjwt;

public interface HTTPInterface {

    // HTTP communication methods
    String login(final String username, final String password);

    boolean getUserInfo(String userName);

    boolean setBio(String userName, String newBio);

    boolean deleteUser(String userName);



    // Setter and getter methods
    String getJWT();

    void setJWT(String JWT);

    String getInfo();

    void setInfo(String userInfo);
}
