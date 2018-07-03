package it.uniba.di.sms.carpooling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Passaggio {
    private Map<String,Object> autista;
    private String tipoViaggio;
    private String dataPassaggio;
    private String ora;
    private int postiDisponibili;
    private String giorno;
    private ArrayList<User> passeggeri;


    public Passaggio(Map<String,Object> autista,String data,String ora,String tipo,String giorno,int postiDisponibili) {
        this.autista = autista;
        this.dataPassaggio = data;
        this.ora = ora;
        this.tipoViaggio = tipo;
        this.giorno = giorno;
        this.postiDisponibili = postiDisponibili;
    }

    public Passaggio(){
        //costruttore che servirà per l'on datachange()
    }

    public Map<String,Object>  getAutista() {
        return autista;
    }

    public void setAutista(HashMap<String,Object> autista) {
        this.autista = autista;
    }

    public ArrayList<User> getPasseggeri() {
        return passeggeri;
    }

    public void setPasseggeri(ArrayList<User> passeggeri) {
        this.passeggeri = passeggeri;
    }

    public String getTipoViaggio() {
        return tipoViaggio;
    }

    public void setTipoViaggio(String tipoViaggio) {
        this.tipoViaggio = tipoViaggio;
    }

    public String getGiorno() {
        return giorno;
    }

    public void setGiorno(String giorno) {
        this.giorno = giorno;
    }



    public String getDataPassaggio() {
        return dataPassaggio;
    }
    public String getOra() {
        return ora;
    }

    public int getPostiDisponibili() {
        return postiDisponibili;
    }


    public void setDataPassaggio(String dataPassaggio) {
        this.dataPassaggio = dataPassaggio;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public void setPostiDisponibili(int postiDisponibili) {
        this.postiDisponibili = postiDisponibili;
    }

}



