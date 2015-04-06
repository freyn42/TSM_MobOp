package tsm.mobop.project.acf.server.ro;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

public class Continents implements RemoteObject {

    private ArrayList<Continent> continentList;

    public Continents(ArrayList<Continent> cities){
        this.continentList = cities;
    }

    @XmlElement(name = "continents")
    public ArrayList<Continent> getContinentList() {
        return continentList;
    }

    public void setContinentList(ArrayList<Continent> continentList) {
        this.continentList = continentList;
    }
}
