package it.unitn.lode2.xml.slides;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 27/01/15
 * Time: 10:47
 */
@XmlRootElement(name = "GROUPS")
public class XMLLodeSlidesGroups {

    @XmlElement(name = "slidesGroup")
    private List<XMLLodeSlidesGroupsGroup> XMLLodeSlidesGroupsGroups = new ArrayList<>();

    public void addSlidesGroup(XMLLodeSlidesGroupsGroup sg){
        XMLLodeSlidesGroupsGroups.add(sg);
    }

    public List<XMLLodeSlidesGroupsGroup> getXMLLodeSlidesGroupsGroups() {
        return XMLLodeSlidesGroupsGroups;
    }
}
