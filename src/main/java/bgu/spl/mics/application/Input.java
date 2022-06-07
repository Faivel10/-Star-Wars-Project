package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Attack;


//this file is used to get the information from the json file using gson.
public class Input {
    private Attack[] attacks;
    private int R2D2;
    private int Lando;
    private int Ewoks;

    public Attack[] getAttacks() {
        return attacks;
    }

    public void setAttacks(Attack[] attacks) {
        this.attacks = attacks;
    }

    public int getR2D2() {
        return R2D2;
    }

    public void setR2D2(int r2D2) {
        R2D2 = r2D2;
    }

    public int getLando() {
        return Lando;
    }

    public void setLando(int lando) {
        Lando = lando;
    }

    public int getEwoks() {
        return Ewoks;
    }

    public void setEwoks(int ewoks) {
        Ewoks = ewoks;
    }
}
