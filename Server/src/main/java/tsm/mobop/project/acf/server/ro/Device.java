package tsm.mobop.project.acf.server.ro;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "device")
public class Device implements RemoteObject{
    private long deviceId;
    private Date creationDate;

    public Device(long deviceId, Date creationDate){
        this.deviceId = deviceId;
        this.creationDate = creationDate;
    }

    @XmlElement(name = "id")
    public long getDeviceId() {
        return deviceId;
    }

    @XmlElement(name = "creationDate")
    public Date getCreationDate() {
        return creationDate;
    }
}