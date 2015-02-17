package it.unitn.lode2.xml.prefs;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: tiziano
 * Date: 17/02/15
 * Time: 18:42
 */
@XmlRootElement(name = "PROPERY")
public class XMLProperty {

    private String name;
    private String value;

    public XMLProperty() {
    }

    public XMLProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @XmlAttribute(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name="VALUE")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
