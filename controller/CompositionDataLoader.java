package controller;

import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;

public class CompositionDataLoader{

    private String filePath;

    private ArrayList<MuzickiSimbol> listaMuzickihSimbola;
    private Map<String,Nota> mapaSimbola;
    private boolean ucitana=false;

    private MidiPlayer midiPlayer= new MidiPlayer();

    private File file;
    private  Scanner sc;

    public ArrayList<MuzickiSimbol> getListaMuzickihSimbola() {
        return listaMuzickihSimbola;
    }

    public Map<String, Nota> getMapaSimbola() {
        return mapaSimbola;
    }

    public CompositionDataLoader(Map<String,Nota> mapaSimbola) throws MidiUnavailableException {
        this.filePath = null;
        listaMuzickihSimbola = new ArrayList<>();
        this.mapaSimbola = mapaSimbola;
    }

    public void ucitavanje() throws FileNotFoundException {
        if(ucitana){

            while(listaMuzickihSimbola.size()!=0){
                listaMuzickihSimbola.remove(0);
            }

            file=null;
            sc=null;
        }

        file = new File(filePath);
        sc = new Scanner(file);
        while (sc.hasNext()){
            String linija = sc.nextLine();

            obradaLinije(linija);

            System.out.println(linija);
        }
        sc.close();

        ispisiListuMuzickihSimbola();

        ucitana = true;
    }

    public void obradaLinije(String linija){

        int i=0;
        while(i<linija.length()) {
            if(linija.charAt(i)=='[' && linija.charAt(i+2)!=' '){
                i++;
                Akord akord = new Akord();
                akord.setTrajanje(Trajanje.CETVRTINA);
                while(linija.charAt(i)!=']'){
                    Nota izMape = mapaSimbola.get("" + linija.charAt(i));

                    Nota notaNova = new Nota(izMape.getTaster(),izMape.getOpis(),izMape.getBroj(),izMape.isPovisena());
                    akord.addNota(notaNova);
                    i++;
                }
                listaMuzickihSimbola.add(akord);
            }
            else if(linija.charAt(i)=='[' && linija.charAt(i+2)==' '){
                i++;
                while(linija.charAt(i)!=']'){
                    if(linija.charAt(i)!=' ') {
                        Nota izMape = mapaSimbola.get("" + linija.charAt(i));

                        Nota notaNova = new Nota(izMape.getTaster(), izMape.getOpis(), izMape.getBroj(), izMape.isPovisena());
                        notaNova.setTrajanje(Trajanje.OSMINA);

                        listaMuzickihSimbola.add(notaNova);
                    }
                    i++;
                }
            }
            else  if(linija.charAt(i)==' '){
                Pauza pauza = new Pauza();
                pauza.setTrajanje(Trajanje.OSMINA);
                listaMuzickihSimbola.add(pauza);
            }
            else  if(linija.charAt(i)=='|'){
                Pauza pauza = new Pauza();
                pauza.setTrajanje(Trajanje.CETVRTINA);
                listaMuzickihSimbola.add(pauza);
            }
            else {
                Nota izMape = mapaSimbola.get("" + linija.charAt(i));

                Nota notaNova = new Nota(izMape.getTaster(),izMape.getOpis(),izMape.getBroj(),izMape.isPovisena());
                notaNova.setTrajanje(Trajanje.CETVRTINA);

                listaMuzickihSimbola.add(notaNova);
            }
            ++i;
        }

    }

    public void ispisiListuMuzickihSimbola(){
        ListIterator iterator = listaMuzickihSimbola.listIterator();

        while (iterator.hasNext())
        {
            System.out.println(iterator.next());
        }
    }

    public boolean isUcitana() {
        return ucitana;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


}
