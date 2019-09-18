package controller;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class IspisNota {

    private ArrayList<MuzickiSimbol> listaMuzickihSimbola;
    private JPanel panel;
    private JLabel[] nizLabela;
    private ArrayList<JLabel> listaLabela = new ArrayList<JLabel>();

    private int redniBr; // Za unos
    private boolean poslednja=true;
    private boolean ucitana = false;
    private int redniBrZaIspis=0;

    public IspisNota(JPanel panel){
        this.panel=panel;
        this.nizLabela = new JLabel[10];
    }

    public int getRedniBrZaIspis() {
        return redniBrZaIspis;
    }

    public void setRedniBrZaIspis(int redniBrZaIspis) {
        this.redniBrZaIspis = redniBrZaIspis;
    }

    public void setListaMuzickihSimbola(ArrayList<MuzickiSimbol> listaMuzickihSimbola) {
        this.listaMuzickihSimbola = listaMuzickihSimbola;
    }

    public boolean dodajSledeci(){

        if(listaMuzickihSimbola.size()==redniBr){
            System.out.println("Gotovo");
            return true;
        }

        JLabel novaLabela =new JLabel("",SwingConstants.CENTER);

        int pomeraj=10;

        if(listaLabela.size()!=0){

            JLabel poslednjiElementListe = listaLabela.get(listaLabela.size()-1);
            pomeraj=poslednjiElementListe.getX()+poslednjiElementListe.getWidth();

        }

        if(listaMuzickihSimbola.get(redniBr) instanceof Nota){
            novaLabela.setText("" + ((Nota) listaMuzickihSimbola.get(redniBr)).getTaster());
            System.out.println("Dodata nota " + ((Nota) listaMuzickihSimbola.get(redniBr)).getTaster());

            if(listaMuzickihSimbola.get(redniBr).getTrajanje()==Trajanje.CETVRTINA){
                novaLabela.setBackground(Color.RED);
                novaLabela.setBounds(pomeraj,10,110,100);
                pomeraj=pomeraj+110;
            }
            else{
                novaLabela.setBackground(Color.GREEN);
                novaLabela.setBounds(pomeraj,10,55,100);
                pomeraj=pomeraj+55;
            }
        }
        else if(listaMuzickihSimbola.get(redniBr) instanceof Akord){
            String text = "<html>";
            for (Nota n:((Akord) listaMuzickihSimbola.get(redniBr)).getNizNota()) {
                text=text+n.getTaster()+"<br>";
            }

            System.out.println("Dodat akord " + text);

            novaLabela.setText(text);
            novaLabela.setBackground(Color.RED);
            novaLabela.setBounds(pomeraj,10,110,100);
            pomeraj=pomeraj+110;
        }
        else if(listaMuzickihSimbola.get(redniBr) instanceof Pauza){
            novaLabela.setBackground(Color.BLACK);

            System.out.println("Dodata pauza");

            if(listaMuzickihSimbola.get(redniBr).getTrajanje()==Trajanje.CETVRTINA){
                novaLabela.setText("|");
                novaLabela.setBounds(pomeraj,10,110,100);
                pomeraj=pomeraj+110;
            }
            else{
                novaLabela.setText(" ");
                novaLabela.setBounds(pomeraj,10,55,100);
                pomeraj=pomeraj+55;
            }

        }

        ++redniBr;

        novaLabela.setOpaque(true);

        panel.add(novaLabela);
        panel.repaint();
        panel.revalidate();

        listaLabela.add(novaLabela);

        return false;
    }

    public void obrada(){
        if(ucitana){
            while (listaLabela.size()!=0){
                listaLabela.remove(0);
            }
            redniBr=0;
            redniBrZaIspis=0;
            poslednja=true;
            panel.removeAll();
            panel.revalidate();
            panel.repaint();
        }

        for (int i=0;i<9;i++){
            dodajSledeci();
        }

        ucitana=true;
    }


    public void removeFirst(){
        panel.removeAll();

        if(poslednja && listaLabela.size()==1){
            panel.add(new JLabel("Sve gotovo"));
            panel.removeAll();
            panel.repaint();
            panel.revalidate();
            System.out.println("Gotovo sve");
            poslednja=false;
            return;
        }

        JLabel labelaZaBrisanje = listaLabela.remove(0);
        int sirina = labelaZaBrisanje.getWidth();

        for (JLabel labela:listaLabela) {
            labela.setBounds(labela.getX()-sirina,10,labela.getWidth(),labela.getHeight());
            panel.add(labela);
            panel.repaint();
            panel.revalidate();
        }

        dodajSledeci();
    }

    public MuzickiSimbol getFirst(){
        MuzickiSimbol ms=null;
        if(redniBrZaIspis<listaMuzickihSimbola.size()) {
            ms = listaMuzickihSimbola.get(redniBrZaIspis);
        }
        return ms;
    }

    public void incRedniBrZaIspis(){
        ++redniBrZaIspis;
        if(redniBrZaIspis<listaMuzickihSimbola.size()) {
            if (listaMuzickihSimbola.get(redniBrZaIspis) instanceof Pauza) {
                removeFirst();
                ++redniBrZaIspis;
            }
        }
    }

}
