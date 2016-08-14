package com.example.lj.asrttstest.info;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by lj on 16/6/10.
 */
public class ContactInfo {

    private String firstName;
    private String lastName;

    public HashMap<String, String> phoneNumberTable;
    public HashMap<String, String> phoneTypeTable;

    private int id;

    public ContactInfo(){
        phoneNumberTable = new HashMap<>();
        phoneTypeTable = new HashMap<>();
        phoneTypeTable.put("3", "work");
        phoneTypeTable.put("1", "home");
        phoneTypeTable.put("2", "mobile");
        firstName = "";
        lastName = "";
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setId(int _id) {
        this.id = _id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String toString(){
        return "Name: " + firstName + " " + lastName;
    }
}
