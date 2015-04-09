package tsm.mobop.project.acf.server.ro;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "userLocation")
public class UserLocation implements RemoteObject{
    private long userLocationId;
    private long deviceId;
    private Date creationDate;
    private double latitude;
    private double longitude;

    public UserLocation(){};

    public UserLocation(long userLocationId, long deviceId, Date creationDate, double latitude, double longitude){
        this.userLocationId = userLocationId;
        this.deviceId = deviceId;
        this.creationDate = creationDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @XmlElement(name = "deviceId")
    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId=deviceId;
    }

    @XmlElement(name = "creationDate")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate=creationDate;
    }

    @XmlElement(name = "latitude")
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude=latitude;
    }

    @XmlElement(name = "longitude")
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude=longitude;
    }

    @XmlElement(name = "userLocationId")
    public long getUserLocationId() {
        return userLocationId;
    }

    public void setUserLocationId(long userLocationId) {
        this.userLocationId = userLocationId;
    }
}