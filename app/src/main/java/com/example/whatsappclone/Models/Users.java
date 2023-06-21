package com.example.whatsappclone.Models;

public class Users {
    String profilePic, username, email, password, userId, lastMessage, status, lastMessageTime;

    public Users(String profilePic, String username, String email, String password, String userId, String lastMessage, String status, String lastMessageTime) {
        this.profilePic = profilePic;
        this.username = username;
        this.email = email;
        this.password = password;
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.status = status;
        this.lastMessageTime = lastMessageTime;
    }

    public Users(){}

    public Users(String username, String email, String password) {

        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
