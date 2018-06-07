package it.uniba.di.sms.carpooling;

import java.util.ArrayList;

public class Passaggio {
    private User autista;
    private String dataPassaggio;
    private String ora;
    private int postiDisponibili;
    private ArrayList<User> passeggeri;


    public Passaggio(User autista, String dataPassaggio, String ora, int postiDisponibili) {
        this.autista = autista;
        this.dataPassaggio = dataPassaggio;
        this.ora = ora;
        this.postiDisponibili = postiDisponibili;
    }


    public User getAutista() {
        return autista;
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


}



