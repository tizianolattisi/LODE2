package it.unitn.lode2.xml.prefs;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 17/02/15
 * Time: 15:55
 */
@XmlRootElement(name = "PREFERENCES")
public class XMLLodePrefs {

    private List<XMLSection> sections = new ArrayList<>();

    @XmlElement(name = "SECTION")
    public List<XMLSection> getSections() {
        return sections;
    }

    public void setSections(List<XMLSection> sections) {
        this.sections = sections;
    }
}
