package com.appdeveloperblog.photoapp.api.users.model;

import java.util.List;

public class UserResponseModel {
    private String firstName;
    private String lastName;
    private String email;
    private List<AlbumsResponseModel> albumsResponseModelList;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<AlbumsResponseModel> getAlbumsResponseModelList() {
        return albumsResponseModelList;
    }

    public void setAlbumsResponseModelList(List<AlbumsResponseModel> albumsResponseModelList) {
        this.albumsResponseModelList = albumsResponseModelList;
    }
}
