package controller;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Formater {
    String fileName;

    public Formater(String fileName) {
        this.fileName = fileName;
    }

    public abstract void format() throws IOException;
}
