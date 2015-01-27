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
public class Groups {

    @XmlElement(name = "slidesGroup")
    private List<SlidesGroup> slidesGroups = new ArrayList<>();

    public void addSlidesGroup(SlidesGroup sg){
        slidesGroups.add(sg);
    }
}
