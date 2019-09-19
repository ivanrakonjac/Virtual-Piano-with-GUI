package controller;

import javax.sound.midi.MidiUnavailableException;
import java.util.ArrayList;
import java.util.ListIterator;

public class Akord extends MuzickiSimbol {

    public static int maxDuzinaAkorda=0;

    private ArrayList<Nota> nizNota;
    private ArrayList<Boolean> potvrdaPritiskanja;

    public ArrayList<Nota> getNizNota() {
        return nizNota;
    }

    public Akord(){
        nizNota = new ArrayList<>();
        potvrdaPritiskanja = new ArrayList<Boolean>();
    }

    public Akord(ArrayList<Nota> nizNotaNovi){
        this.nizNota = new ArrayList<Nota>();
        for (Nota n:nizNotaNovi) {
            nizNota.add(n);
        }
        this.trajanje=Trajanje.OSMINA;
        potvrdaPritiskanja = new ArrayList<Boolean>();
    }

    public void addNota(Nota n){
        nizNota.add(n);
        potvrdaPritiskanja.add(false);
        if(nizNota.size()>maxDuzinaAkorda) maxDuzinaAkorda=nizNota.size();
    }

    public static int getMaxDuzinaAkorda() {
        return maxDuzinaAkorda;
    }

    public Nota getNota(int index){
        return nizNota.get(index);
    }

    public int getBrNota(){
        return nizNota.size();
    }

    @Override
    public String toString() {
        String note = "";

        for (Nota n:nizNota) {
            note = note + n.getTaster();
        }

        return "Akord{" +
                "nizNota=" + note +
                '}';
    }

    public String toStringForTxt(){
        String note = "";

        for (Nota n:nizNota) {
            note = note + n.getTaster();
        }

        return "["+note+"]";
    }

    public void setPritisnuto(String s){
        int index=0;

        for(int i=0;i<nizNota.size();i++){
            if(nizNota.get(i).getTaster()==s) {
                index = i;
                break;
            }
        }
        potvrdaPritiskanja.set(index,true);
    }

    public boolean svePritisnuto(){
        for (Boolean b:potvrdaPritiskanja) {
            if(!b) return b;
        }
        return true;
    }


}
