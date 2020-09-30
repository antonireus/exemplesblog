package sample;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@XmlRootElement(name = "PERSONA")
public class Persona {

    private String nom;
    private String primerCognom;
    private String segonCognom;
    private LocalDate dataNaixement;
    private String document;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrimerCognom() {
        return primerCognom;
    }

    public void setPrimerCognom(String primerCognom) {
        this.primerCognom = primerCognom;
    }

    public String getSegonCognom() {
        return segonCognom;
    }

    public void setSegonCognom(String segonCognom) {
        this.segonCognom = segonCognom;
    }

    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public LocalDate getDataNaixement() {
        return dataNaixement;
    }

    public void setDataNaixement(LocalDate dataNaixement) {
        this.dataNaixement = dataNaixement;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
