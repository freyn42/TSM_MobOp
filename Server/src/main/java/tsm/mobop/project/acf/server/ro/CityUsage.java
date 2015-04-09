package tsm.mobop.project.acf.server.ro;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "cityUsage")
public class CityUsage implements RemoteObject{
    private long cityUsageId;
    private long userLocationId;
    private long cityId;

    public CityUsage(){}

    public CityUsage(long cityUsageId, long userLocationId, long cityId){
        this.cityUsageId = cityUsageId;
        this.userLocationId = userLocationId;
        this.cityId = cityId;
    }

    @XmlElement(name="cityUsageId")
    public long getCityUsageId() {
        return cityUsageId;
    }

    public void setCityUsageId(long cityUsageId) {
        this.cityUsageId = cityUsageId;
    }

    @XmlElement(name="userLocationId")
    public long getUserLocationId() {
        return userLocationId;
    }

    public void setUserLocationId(long userLocationId) {
        this.userLocationId = userLocationId;
    }

    @XmlElement(name="cityId")
    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }
}