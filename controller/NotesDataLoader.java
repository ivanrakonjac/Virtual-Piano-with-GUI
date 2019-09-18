package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotesDataLoader {

    private Map<String,Nota> mapaSimbola;
    private Nota[] nizNotaCrne;
    private Nota[] nizNotaBele;

    public NotesDataLoader(){
        mapaSimbola = new HashMap<String,Nota>();
        nizNotaBele = new Nota[35];
        nizNotaCrne = new Nota[25];
    }

    public Map<String, Nota> getMapaSimbola() {
        return mapaSimbola;
    }

    public void ucitavanje () throws FileNotFoundException {
        File file = new File("D:\\Java programi\\TestProjekat\\src\\input\\map.csv");
        Scanner sc = new Scanner(file);

        String pattern = "([^,]*),([^,]*),([^,]*)";
        Pattern r = Pattern.compile(pattern);

        int brojacNotaBele = 0;
        int brojacNotaCrne = 0;

        while (sc.hasNext()){
            String linija = sc.nextLine();

            Matcher m = r.matcher(linija);

            //System.out.println(linija);

            if (m.find( )) {
                //System.out.println("Found value: " + m.group(0) );
                //System.out.println("Found value: " + m.group(1) );
                //System.out.println("Found value: " + m.group(2) );
                //System.out.println("Found value: " + m.group(3) );

                if(m.group(2).charAt(1)=='#') {
                    mapaSimbola.put("" + m.group(1),new Nota(m.group(1),m.group(2), m.group(3),true));
                    nizNotaCrne[brojacNotaCrne]=new Nota(m.group(1),m.group(2), m.group(3),true);
                    brojacNotaCrne++;
                }
                else{
                    mapaSimbola.put("" + m.group(1), new Nota(m.group(1),m.group(2), m.group(3),false));
                    nizNotaBele[brojacNotaBele]=new Nota(m.group(1),m.group(2), m.group(3),false);
                    brojacNotaBele++;
                }

            } else {
                System.out.println("NO MATCH");
            }
        }
        sc.close();
    }

    public Nota[] getNizNotaBele() {
        return nizNotaBele;
    }
    public Nota[] getNizNotaCrne() {
        return nizNotaCrne;
    }

    public Nota getNizNotaCrneElement(int index){
        return nizNotaCrne[index];
    }

    public Nota getNizNotaBeleElement(int index){
        return nizNotaBele[index];
    }

    public char getTasterBela(int index){
        return nizNotaBele[index].getTaster().charAt(0);
    }
    public char getTasterCrna(int index){
        return nizNotaCrne[index].getTaster().charAt(0);
    }

    public Nota getNotaFromMapaSimbola(String s){
        return mapaSimbola.get(s);
    }

    public void printHashMap(){
        for (HashMap.Entry<String, Nota> entry : mapaSimbola.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().getOpis() +" "+entry.getValue().getBroj() + " " + entry.getValue().isPovisena());
        }
    }

    public void printNizNotaCrne(){
        System.out.println("Niz nota: ---------------------------------");
        for (Nota nota:nizNotaCrne) {
            System.out.println(nota.getTaster() + " " + nota.getOpis() + " " + nota.getBroj() + " " + nota.isPovisena());
        }
    }

    public void printNizNotaBele(){
        System.out.println("Niz nota: ---------------------------------");
        for (Nota nota:nizNotaBele) {
            System.out.println(nota.getTaster() + " " + nota.getOpis() + " " + nota.getBroj() + " " + nota.isPovisena());
        }
    }

}
