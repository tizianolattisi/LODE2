package it.unitn.lode2.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * User: tiziano
 * Date: 27/01/15
 * Time: 15:40
 */
public class XMLHelper<T> {

    private final Class klass;

    public XMLHelper(Class klass) {
        this.klass = klass;
    }

    public static <T> XMLHelper<T> build(Class<T> klass){
        XMLHelper<T> helper = new XMLHelper<>(klass);
        return helper;
    }

    public void marshall(T t, File f){
        try {
            createMarshaller().marshal(t, f);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public void marshall(T t, StringWriter sw){
        try {
            createMarshaller().marshal(t, sw);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public T unmarshal(Reader reader){
        try {
            T o = (T) createUnmarshaller().unmarshal(reader);
            return o;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public T unmarshal(File f){
        try {
            T o = (T) createUnmarshaller().unmarshal(f);
            return o;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }



    private Marshaller createMarshaller(){
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

    private Unmarshaller createUnmarshaller(){
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
