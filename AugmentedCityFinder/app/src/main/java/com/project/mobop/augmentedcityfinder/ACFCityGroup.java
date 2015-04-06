package com.project.mobop.augmentedcityfinder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tom on 05.04.2015.
 */
public class ACFCityGroup {

    private int id;
    private String name;
    private List<ACFCity> cityList = new ArrayList<ACFCity>();
    private Date creationDate, modificationDate;
    private boolean visible;
    private int deviceId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ACFCity> getCityList() {
        return cityList;
    }

    public void setCityList(List<ACFCity> cityList) {
        this.cityList = cityList;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
