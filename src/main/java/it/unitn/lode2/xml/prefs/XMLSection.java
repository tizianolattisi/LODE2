package it.unitn.lode2.xml.prefs;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 17/02/15
 * Time: 15:56
 */
@XmlRootElement(name = "SECTION")
public class XMLSection {

    private String name;

    private XMLGroupOfProperties groupOfProperties;

    public XMLSection() {
    }

    public XMLSection(String name) {
        this.name = name;
    }

    @XmlAttribute(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "GROUP_OF_PROPERTIES")
    public XMLGroupOfProperties getGroupOfProperties() {
        return groupOfProperties;
    }

    public void setGroupOfProperties(XMLGroupOfProperties groupOfProperties) {
        this.groupOfProperties = groupOfProperties;
    }
}
