package it.unitn.lode2.xml.prefs;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 17/02/15
 * Time: 18:37
 */
@XmlRootElement(name = "GROUP_OF_PROPERTIES")
public class XMLGroupOfProperties {

    private List<XMLProperty> properties = new ArrayList<>();

    public XMLGroupOfProperties() {
    }

    @XmlElement(name = "PROPERTY")
    public List<XMLProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<XMLProperty> properties) {
        this.properties = properties;
    }
}
