package com.example.sputnicjitsi.experts;

public class AvailableExperts {
    private int groupUserId;
    private int groupId;
    private int userId;
    private String createdAt;
    private String updatedAt;
    private User user;

    public AvailableExperts(int userId, User user){
        this.user = user;
        this.userId = userId;
    }
    public int getGroupUserId() {
        return groupUserId;
    }

    public void setGroupUserId(int groupUserId) {
        this.groupUserId = groupUserId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public String getFullName(){
        return user.getLastName() + " " + user.getFirstName() + " " + user.getPatronymicName();
    }

    //    public int getUserIcon() {
//        return userIcon;
//    }
//
//    public void setUserIcon(int userIcon) {
//        this.userIcon = userIcon;
//    }
}
