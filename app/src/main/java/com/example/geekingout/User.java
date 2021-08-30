package com.example.geekingout;

import android.location.Location;
import android.net.Uri;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String name;
    private String [] intrests;
    private String about;
    //private Location location;
    private String proffession;
    private Uri profileImage;


    public User(String name, String [] intrests, String about,String proffession,Uri profileImage){
        this.name = name;
        this.intrests=intrests;
        this.about=about;
        //this.location=location;
        this.proffession=proffession;
        this.profileImage=profileImage;
    }
}
