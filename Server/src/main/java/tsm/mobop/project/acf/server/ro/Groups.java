package tsm.mobop.project.acf.server.ro;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

public class Groups implements RemoteObject {

    private ArrayList<Group> groupList;

    public Groups(ArrayList<Group> cities){
        this.groupList = cities;
    }

    @XmlElement(name = "groups")
    public ArrayList<Group> getGroupList() {
        return groupList;
    }
}
