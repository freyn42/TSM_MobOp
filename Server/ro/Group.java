package tsm.mobop.project.acf.server.ro;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Group{
    private long groupId;
    private long deviceId;
    private String name;
    private Date creationDate;
    private Date modificationDate;
    private ArrayList<Long> members;

    public Group(){}

    public Group(long groupId, long deviceId, String name, Date creationDate, Date modificationDate, Long[] members) {
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.name = name;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.members = new ArrayList<Long>(Arrays.asList(members));
    }

    @XmlElement(name = "id")
    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId){
        this.groupId=groupId;
    }

    @XmlElement(name = "deviceId")
    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId){
        this.deviceId = deviceId;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    @XmlElement(name = "creationDate")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(){
        this.creationDate=creationDate;
    }

    @XmlElement(name = "modificationDate")
    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate){
        this.modificationDate=modificationDate;
    }

    @XmlElement(name = "members")
    public ArrayList<Long> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Long> members){
        this.members=members;
    }
}