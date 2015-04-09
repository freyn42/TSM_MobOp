package tsm.mobop.project.acf.server.ro;

import javax.xml.bind.annotation.XmlElement;

public class Continent{
    private long continentId;
    private String name;

    public Continent(long continentId, String name){
        this.continentId = continentId;
        this.name = name;
    }

    @XmlElement(name = "id")
    public long getContinentId() {
        return continentId;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }
}