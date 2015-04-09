package tsm.mobop.project.acf.server.ro;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

public class City{
    private long cityId;
    private String name;
    private double latitude;
    private double longitude;
    private long countryId;
    private long deviceId;

    public City(){

    }

    public City(long cityId, String cityName, double latitude, double longitude, long countryId, long deviceId){
        this.cityId = cityId;
        this.name = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryId = countryId;
        this.deviceId = deviceId;
    }

    @XmlElement(name = "id")
    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "latitude")
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @XmlElement(name = "longitude")
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @XmlElement(name = "countryId")
    public long getCountryId() {
        return countryId;
    }

    public void setCountryId(long countryId) {
        this.countryId = countryId;
    }

    @XmlElement(name = "deviceId")
    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }
}