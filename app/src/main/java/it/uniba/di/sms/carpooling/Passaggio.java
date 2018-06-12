package it.uniba.di.sms.carpooling;

import java.util.ArrayList;

public class Passaggio {
    private String id;
    private String dataPassaggio;
    private String ora;
    private int postiDisponibili;
    private ArrayList<User> passeggeri;


    public Passaggio(int postiDisponibili) {
        this.postiDisponibili = postiDisponibili;
    }



    public String getId() {
        return id;
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

    public void setId(String id) {
        this.id = id;
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



