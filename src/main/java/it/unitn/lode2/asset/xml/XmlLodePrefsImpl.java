package it.unitn.lode2.asset.xml;

import it.unitn.lode2.Constants;
import it.unitn.lode2.asset.AbstractLodePrefs;
import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.LodePrefs;
import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.prefs.XMLLodePrefs;
import it.unitn.lode2.xml.prefs.XMLProperty;
import it.unitn.lode2.xml.prefs.XMLSection;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tiziano on 05/05/15.
 */
public class XmlLodePrefsImpl extends AbstractLodePrefs implements LodePrefs {

    String filePath;
    XMLLodePrefs prefs;

    public XmlLodePrefsImpl(String path) {
        filePath = path;
        File f = new File(filePath);
        if( f.exists() ) {
            prefs = XMLHelper.build(XMLLodePrefs.class).unmarshal(f);
        } else {
            prefs = new XMLLodePrefs();
        }
    }

    @Override
    public List<Course> lastUsedCourses() {
        XMLProperty lastUsed = null;
        XMLProperty lastUsedMinus2 = null;
        XMLProperty lastUsedMinus3 = null;
        for( XMLSection section: prefs.getSections() ){
            if( "LAST USED COURSE".equals(section.getName()) ){
                for(XMLProperty property: section.getGroupOfProperties().getProperties() ){
                    if( "Last used course".equals(property.getName()) ){
                        lastUsed = property;
                    } else if( "Last used course-2".equals(property.getName()) ){
                        lastUsedMinus2 = property;
                    } else if( "Last used course-3".equals(property.getName()) ){
                        lastUsedMinus3 = property;
                    }
                }
                break;
            }
        }
        return Arrays.asList(new XmlCourseImpl(lastUsed.getValue()),
                new XmlCourseImpl(lastUsedMinus2.getValue()),
                new XmlCourseImpl(lastUsedMinus3.getValue()));
    }

    @Override
    public void setLastUsedCourse(Course course) {
        ArrayList<Course> courses = new ArrayList(lastUsedCourses());
        if( !course.path().equals(courses.get(0).path()) ){
            if( !course.path().equals(courses.get(1).path()) ){
                courses.remove(2);
            } else {
                courses.remove(1);
            }
            courses.add(0, course);
            for( XMLSection section: prefs.getSections() ) {
                if ("LAST USED COURSE".equals(section.getName())) {
                    section.getGroupOfProperties().setProperties(Arrays.asList(new XMLProperty("Last used course", courses.get(0).path()),
                            new XMLProperty("Last used course-2", courses.get(1).path()),
                            new XMLProperty("Last used course-3", courses.get(2).path())));
                }
            }
        }


    }

    @Override
    public void save() {
        XMLHelper.build(XMLLodePrefs.class).marshall(prefs, new File(filePath));
    }


}
