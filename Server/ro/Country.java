package tsm.mobop.project.acf.server.ro;

import javax.xml.bind.annotation.XmlElement;

public class Country{
    private long countryId;
    private String name;
    private String code;
    private long continentId;

    public Country(long countryId, String name, String code, long continentId) {
        this.countryId = countryId;
        this.name = name;
        this.code = code;
        this.continentId = continentId;
    }

    @XmlElement(name = "id")
    public long getCountryId() {
        return countryId;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    @XmlElement(name = "code")
    public String getCode() {
        return code;
    }

    @XmlElement(name = "continentId")
    public long getContinentId() {
        return continentId;
    }
}