package com.project.mobop.augmentedcityfinder;

import java.util.Comparator;

/**
 * Created by tom on 07.04.2015.
 */
public class ACFCountry implements Comparable{

    private int id;
    private String name;
    private String code;
    private int continentId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getContinentId() {
        return continentId;
    }

    public void setContinentId(int continentId) {
        this.continentId = continentId;
    }

    @Override
    public int compareTo(Object another) {
        return name.compareTo(((ACFCountry)another).getName());
    }

    @Override
    public boolean equals(Object another) {
        if(!(another instanceof ACFCountry)){
            return false;
        }
        return id == ((ACFCountry) another).getId();
    }
}
