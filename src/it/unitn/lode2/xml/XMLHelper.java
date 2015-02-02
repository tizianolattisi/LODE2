package it.unitn.lode2.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * User: tiziano
 * Date: 27/01/15
 * Time: 15:40
 */
public class XMLHelper {

    public static Marshaller createMarshaller(Class klass){
        try {
            JAXBContext context = JAXBContext.newInstance(klass);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            return marshaller;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Unmarshaller createUnmarshaller(Class klass){
        try {
            JAXBContext context = JAXBContext.newInstance(klass);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

}
