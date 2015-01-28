package com.example.firegnu.torquewrenchdemo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by firegnu on 15-1-26.
 */
public class DataHolder {
    //username
    private static String userName;
    public static String getUserName() {
        return userName;
    }
    public static void setUserName(String userName) {
        DataHolder.userName = userName;
    }
    //userpassword
    private static String userPassword;
    public static String getUserPassword() {
        return userPassword;
    }
    public static void setUserPassword(String userPassword) {
        DataHolder.userPassword = userPassword;
    }
    //user id
    private static int userId;
    public static int getUserId() {
        return userId;
    }
    public static void setUserId(int userId) {
        DataHolder.userId = userId;
    }

    //user type
    private static int userType;
    public static int getUserType() {
        return userType;
    }
    public static void setUserType(int userType) {
        DataHolder.userType = userType;
    }

    //user profile avatar
    private static String userAvatar;
    public static String getUserAvatar() {
        return userAvatar;
    }
    public static void setUserAvatar(String userAvatar) {
        DataHolder.userAvatar = userAvatar;
    }

    //login success?
    private static Boolean bLogin = false;
    public static Boolean getbLogin() {
        return bLogin;
    }
    public static void setbLogin(Boolean bLogin) {
        DataHolder.bLogin = bLogin;
    }
    //
    //set server ip address
    private static String serverAddress = "";
    public static String getServerAddress() {
        return serverAddress;
    }
    public static void setServerAddress(String serverAddress) {
        DataHolder.serverAddress = serverAddress;
    }
}

