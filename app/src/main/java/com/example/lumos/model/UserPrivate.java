package com.example.lumos.model;

public class UserPrivate {
    private String Id;
    private String Email;
    private String UserName;
    private String FirstName;
    private String LastName;
    private String ImageURL;
    private String Status;
    private String Search;

    public UserPrivate() {}
    public UserPrivate(String Id, String Email, String UserName, String FirstName, String LastName, String ImageURL, String Status, String Search){
        this.Id = Id;
        this.Email = Email;
        this.UserName = UserName;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.ImageURL = ImageURL;
        this.Status = Status;
        this.Search = Search;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String ImageURL) {
        this.ImageURL = ImageURL;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getSearch() {
        return Search;
    }

    public void setSearch(String Search) {
        this.Search = Search;
    }
}
