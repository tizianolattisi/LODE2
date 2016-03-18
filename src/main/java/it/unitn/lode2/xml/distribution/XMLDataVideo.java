package it.unitn.lode2.xml.distribution;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by tiziano on 26/05/15.
 */
public class XMLDataVideo {

    private String nome;
    private Long totaltime;
    private Long startime;

    public XMLDataVideo() {
    }

    @XmlElement(name = "nome")
    public String getNome() {
        return nome;
    }

    @XmlElement(name = "totaltime")
    public Long getTotaltime() {
        return totaltime;
    }

    @XmlElement(name = "startime")
    public Long getStartime() {
        return startime;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTotaltime(Long totaltime) {
        this.totaltime = totaltime;
    }

    public void setStartime(Long startime) {
        this.startime = startime;
    }
}
