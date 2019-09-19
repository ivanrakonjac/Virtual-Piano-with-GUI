package controller;

import gui.Dugme;

import javax.sound.midi.MidiUnavailableException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class Player extends Thread{

    private ArrayList<AkordThread> akordThreads;

    private ArrayList<MuzickiSimbol> listaMuzickihSimbola=null;
    private MidiPlayer midiPlayer = new MidiPlayer();

    private Map<String,Nota> mapaSimbola;
    private Map<Character, Dugme> mapaDirki;

    private volatile boolean running = true;
    private volatile boolean paused = true;
    private volatile boolean stoped = false;
    private final Object pauseLock = new Object();

    private IspisNota ispisNota;

    private volatile int sledecaZaSviranje=0;

    public Player(IspisNota ispisNota) throws MidiUnavailableException {
        akordThreads=new ArrayList<AkordThread>();
        this.ispisNota=ispisNota;
        start();
    }

    public boolean isRunning() {
        return running;
    }

    public void setMapaDirki(Map<Character, Dugme> mapaDirki) {
        this.mapaDirki = mapaDirki;
    }

    public void setMapaSimbola(Map<String, Nota> mapaSimbola) {
        this.mapaSimbola = mapaSimbola;
    }

    public ArrayList<MuzickiSimbol> getListaMuzickihSimbola() {
        return listaMuzickihSimbola;
    }

    public boolean isStoped() {
        return stoped;
    }

    public void setListaMuzickihSimbola(ArrayList<MuzickiSimbol> listaMuzickihSimbola) {
        this.listaMuzickihSimbola = listaMuzickihSimbola;
    }

    public void setAkordThreadsNumber(int maxBrojTonovaAkorda) throws MidiUnavailableException {
        System.out.println("maxBrTonova " + maxBrojTonovaAkorda + " akordThreads.size() " + akordThreads.size());
        if(maxBrojTonovaAkorda>akordThreads.size()){
            int duiznaStara=akordThreads.size();
            for(int i=0;i<maxBrojTonovaAkorda-duiznaStara;i++){
                AkordThread akordThread=new AkordThread();
                akordThreads.add(akordThread);
                System.out.println("Dodao sam nit");
            }
        }
    }


    @Override
    public void run() {
        while (running) {
            synchronized (pauseLock) {
                if (!running) {
                    break;
                }
                if (paused) {
                    try {
                        synchronized (pauseLock) {
                            pauseLock.wait();
                        }
                    } catch (InterruptedException ex) {
                        break;
                    }
                    if (!running) {
                        break;
                    }
                }
            }
            // Your code here
            try {
                if(sledecaZaSviranje>=listaMuzickihSimbola.size()) break;
                MuzickiSimbol ms = listaMuzickihSimbola.get(sledecaZaSviranje);

                if (ms instanceof Nota) {
                    Nota n = (Nota) ms;

                    Dugme d = mapaDirki.get(n.getTaster().charAt(0));
                    if(d!=null) d.getButton().setBackground(Color.red);

                    if (ms.getTrajanje() == Trajanje.CETVRTINA) {
                        midiPlayer.play(Integer.parseInt(n.getBroj()), 400);
                    } else {
                        midiPlayer.play(Integer.parseInt(n.getBroj()), 300);
                    }

                    if(d!=null && n.isPovisena()==true) d.getButton().setBackground(Color.black);
                    else d.getButton().setBackground(Color.white);

                    if(running) ispisNota.removeFirst();
                    sledecaZaSviranje++;

                } else if (ms instanceof Pauza) {
                    if (ms.getTrajanje() == Trajanje.CETVRTINA) {
                        sleep(400);
                        System.out.println("pauza cetvrtina");
                    } else {
                        sleep(200);
                        System.out.println("pauza osmina");
                    }

                    if(running) ispisNota.removeFirst();
                    sledecaZaSviranje++;

                } else if (ms instanceof Akord) {
                    Akord akord = (Akord) ms;
                    ArrayList<Nota> nizNota = akord.getNizNota();

                    for (int i=0;i<nizNota.size();i++) {
                        akordThreads.get(i).setMidBroj(Integer.parseInt(nizNota.get(i).getBroj()));
                        akordThreads.get(i).setDuzina(400);
                    }
                    for (int i=0;i<nizNota.size();i++) {

                        Dugme d = mapaDirki.get(akord.getNota(i).getTaster().charAt(0));
                        if(d!=null) d.getButton().setBackground(Color.red);

                        akordThreads.get(i).resumee();
                    }
                    System.out.println(akord);
                    sleep(400);
                    for (int i=0;i<nizNota.size();i++) {

                        Dugme d = mapaDirki.get(akord.getNota(i).getTaster().charAt(0));
                        if(d!=null && akord.getNota(i).isPovisena()==true) d.getButton().setBackground(Color.black);
                        else d.getButton().setBackground(Color.white);

                        akordThreads.get(i).pause();
                    }

                    if(running) ispisNota.removeFirst();
                    else stopp();

                    sledecaZaSviranje++;
                }
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        stoped=true;
    }

    public void stopp() {
        running = false;
        resumee();
    }

    public void pause() {
        paused = true;

        ispisNota.setRedniBrZaIspis(sledecaZaSviranje + 1);

        if(sledecaZaSviranje+1<listaMuzickihSimbola.size()){
            if(listaMuzickihSimbola.get(sledecaZaSviranje) instanceof Pauza) System.out.println("Zaustavljeno na pauzu");
            else if(listaMuzickihSimbola.get(sledecaZaSviranje) instanceof Akord) System.out.println("Akord treba da se odvirsa");
            else if(listaMuzickihSimbola.get(sledecaZaSviranje+1) instanceof Nota)System.out.println("Sledeca nota koja treba da bude odsvirana je: " + ((Nota) listaMuzickihSimbola.get(sledecaZaSviranje + 1)).getTaster());
        }
    }

    public void resumee() {
        if(running) {
            sledecaZaSviranje = ispisNota.getRedniBrZaIspis();
            if(sledecaZaSviranje<listaMuzickihSimbola.size()){
                if(listaMuzickihSimbola.get(ispisNota.getRedniBrZaIspis()) instanceof Pauza) System.out.println("Zaustavljeno na pauzu");
                else if(listaMuzickihSimbola.get(ispisNota.getRedniBrZaIspis()) instanceof Akord) System.out.println("Zaustavljeno na akordu" + listaMuzickihSimbola.get(ispisNota.getRedniBrZaIspis()));
                else if(listaMuzickihSimbola.get(ispisNota.getRedniBrZaIspis()) instanceof Nota)System.out.println("Sledeca nota koja treba da bude odsvirana na klaviru je: " + ((Nota) listaMuzickihSimbola.get(sledecaZaSviranje)).getTaster());
            }
        }
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

}
