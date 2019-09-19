package controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TextFormater extends Formater {

    private ArrayList<MuzickiSimbol> listaUcitanihSimbola;
    private boolean flag = true;
    private boolean dodataOsminaPrva=false;

    public TextFormater(String fileName,ArrayList<MuzickiSimbol> listaUcitanihSimbola) {
        super(fileName);
        this.listaUcitanihSimbola=listaUcitanihSimbola;
    }

    @Override
    public void format() throws IOException {
        try{
            FileWriter out = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(out);

            for(MuzickiSimbol ms : listaUcitanihSimbola) {

                if((ms.getTrajanje()!=Trajanje.OSMINA || !(ms instanceof Nota)) && dodataOsminaPrva==true){
                    bw.append(' ');
                    dodataOsminaPrva=false;
                }

                if(ms instanceof Nota) {
                    if(ms.getTrajanje() == Trajanje.CETVRTINA) {
                        if(!flag) {
                            bw.append(']');
                            flag = true;
                        }
                        bw.append(((Nota) ms).getTaster());
                    }
                    else if(ms.getTrajanje() == Trajanje.OSMINA) {
                        if(flag) {
                            bw.append('[').append(((Nota) ms).getTaster());
                            dodataOsminaPrva=true;
                            flag = false;
                            continue;
                        }
                        dodataOsminaPrva=false;
                        bw.append(' ');
                        bw.append(((Nota) ms).getTaster());
                    }
                    continue;
                }
                if(ms instanceof Akord) {
                    if(!flag) {
                        bw.append(']');
                        flag = true;
                    }
                    bw.append(((Akord) ms).toStringForTxt());
                    continue;
                }
                if(ms instanceof Pauza) {
                    if(!flag) {
                        bw.append(']');
                        flag = true;
                    }
                    if(ms.getTrajanje() == Trajanje.CETVRTINA) bw.append("|");
                    else if(ms.getTrajanje() == Trajanje.OSMINA)bw.append(" ");

                    continue;
                }
            }

            if(!flag) bw.append(']');

            bw.close();
            out.close();
        }catch (IOException e) {}
    }
}
