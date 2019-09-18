package controller;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Snimac{

    private boolean ukljucen=false;
    private boolean zavrsio = false;

    private ArrayList<MuzickiSimbol> listaUnetihSimbola=new ArrayList<MuzickiSimbol>();

    private ArrayList<Nota> listaNota=new ArrayList<Nota>();
    private boolean akord = false;

    private long osmina=400;
    private long cetvrtina=800;

    private volatile long zadnjiUnos=0;

    public boolean isUkljucen() {
        return ukljucen;
    }

    public void setUkljucen(boolean ukljucen) {
        this.ukljucen = ukljucen;
    }

    public boolean isZavrsio() {
        return zavrsio;
    }

    public void setOsmina(long osmina) {
        this.osmina = osmina;
    }

    public void setCetvrtina(long cetvrtina) {
        this.cetvrtina = cetvrtina;
    }

    public void setZavrsio(boolean zavrsi) {
        this.zavrsio = zavrsi;
        if(akord==true) dodajAkord();
    }

    public ArrayList<MuzickiSimbol> getListaUnetihSimbola() {
        return listaUnetihSimbola;
    }

    public void prepapreForNew(){
        listaUnetihSimbola.removeAll(listaUnetihSimbola);
        listaNota.removeAll(listaNota);
        zavrsio=false;
    }

    public long checkTime(){
        long sadasnjeVreme = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(sadasnjeVreme-zadnjiUnos);
    }

    public void dodajPauzu(Trajanje trajanje){
        listaUnetihSimbola.add(new Pauza(trajanje));
    }

    public void dodajAkord(){
        System.out.println("Dodat akord");
        if(listaNota.size()==1){
            listaUnetihSimbola.add(listaNota.get(0));
        }else{
            listaUnetihSimbola.add(new Akord(listaNota));
        }
        listaNota.removeAll(listaNota);
        akord=false;
    }

    public void dodaj(long cnt,long time,Nota nota){
        long vreme = checkTime();
        zadnjiUnos=time;

        if(vreme<=osmina){
            System.out.println("Dodato u akord");
            listaNota.add(nota);
            akord=true;
        }
        else if(vreme>osmina && vreme<cetvrtina){
            System.out.println("dodata osmina");
            dodajPauzu(Trajanje.OSMINA);
            if(akord==true) dodajAkord();
            nota.setTrajanje(Trajanje.OSMINA);
            listaUnetihSimbola.add(nota);
        }
        else if(vreme>=cetvrtina){
            System.out.println("Dodato u cetvtina");
            dodajPauzu(Trajanje.CETVRTINA);
            if (akord==true) dodajAkord();
            nota.setTrajanje(Trajanje.CETVRTINA);
            listaUnetihSimbola.add(nota);
        }
    }
}
