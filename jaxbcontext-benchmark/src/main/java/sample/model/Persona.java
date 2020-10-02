package sample.model;

import sample.adapter.LocalDateAdapter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@XmlRootElement
public class Persona {

    private String id;
    private String nom;
    private String primerCognom;
    private String segonCognom;
    private LocalDate dataNaixement;
    private String document;
    private Adreça adreça;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Adreça getAdreça() {
        return adreça;
    }

    public void setAdreça(Adreça adreça) {
        this.adreça = adreça;
    }
}
