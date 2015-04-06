package tsm.mobop.project.acf.server.ro;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

public class Countries implements RemoteObject {

    private ArrayList<Country> countryList;

    public Countries(ArrayList<Country> cities){
        this.countryList = cities;
    }

    @XmlElement(name = "countries")
    public ArrayList<Country> getCountryList() {
        return countryList;
    }

    public void setCountryList(ArrayList<Country> countryList) {
        this.countryList = countryList;
    }
}
