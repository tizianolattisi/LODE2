package it.unitn.lode2.xml.distribution;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by tiziano on 26/05/15.
 */
public class XMLDataInfo {

    private String corso;
    private String titolo;
    private String professore;
    private String dynamic_url;

    public XMLDataInfo() {
    }

    @XmlElement(name = "corso")
    public String getCorso() {
        return corso;
    }

    @XmlElement(name = "titolo")
    public String getTitolo() {
        return titolo;
    }

    @XmlElement(name = "professore")
    public String getProfessore() {
        return professore;
    }

    @XmlElement(name = "dinamic_url") // XXX: correggere?
    public String getDynamic_url() {
        return dynamic_url;
    }

    public void setCorso(String corso) {
        this.corso = corso;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public void setProfessore(String professore) {
        this.professore = professore;
    }

    public void setDynamic_url(String dynamic_url) {
        this.dynamic_url = dynamic_url;
    }
}
