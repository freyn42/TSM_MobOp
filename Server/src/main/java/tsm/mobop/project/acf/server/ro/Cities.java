package tsm.mobop.project.acf.server.ro;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

public class Cities implements RemoteObject {

    private ArrayList<City> cityList;

    public Cities(ArrayList<City> cities){
        this.cityList = cities;
    }

    @XmlElement(name = "cities")
    public ArrayList<City> getCityList() {
        return cityList;
    }

    public void setCityList(ArrayList<City> cityList) {
        this.cityList = cityList;
    }
}
