package de.kriegergilde.badboy;

import android.util.Log;

/**
 * Created by Klaus on 28.03.2016.
 */
public enum Henchman {

    NONE(0, 0, 0, 0, 0, "keine Handlanger"),
    DOG(0, 0, 0, 10, 5, "Hundewelpe"),
    BEGGARS(0, 0, 8, 10, 300, "Bettlerbande"),
    WHORES(40, 0, 10, 10, 2_000, "Prostituierte"),
    THUGS(60, 0, 30, 10, 50_000, "Schlägertrupp"),
    BODYGUARDS(60, 0, 80, 10, 500_000, "Bodyguards"),
    PRIVATE_ARMY(60, 0, 1_000, 10, 50_000_000, "Privatarmee");

    private final int era;
    private final int cooldownRed; // in h
    private final int lifeBonus;
    private final int successBonus; // in %-Punkten
    private final int price;
    private final String name;

    private Henchman(int era, int cooldownRed, int lifeBonus, int successBonus, int price, String name) {
        this.era = era;
        this.cooldownRed = cooldownRed;
        this.lifeBonus = lifeBonus;
        this.successBonus = successBonus;
        this.price = price;
        this.name = name;
    }

    public int getCooldownRed() {
        return cooldownRed;
    }

    public int getLifeBonus() {
        return lifeBonus;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getSuccessBonus() {
        return successBonus;
    }

    public int getEra() {
        return era;
    }

    public boolean isMax(){
        return this.ordinal() == values().length-1;
    }

    public String asString(boolean includePrice){
        StringBuilder sb = new StringBuilder(30);
        sb.append(name);
        sb.append("\n");
        if (cooldownRed !=0){
            sb.append("Cooldown Reduktion: ");
            sb.append(cooldownRed);
            sb.append("h ");
        }
        if (successBonus !=0){
            sb.append("Erfolg: +");
            sb.append(successBonus);
            sb.append("% ");
        }
        if (lifeBonus !=0){
            sb.append("Schutz: +");
            sb.append(lifeBonus);
            sb.append(" ");
        }
        if(includePrice){
            sb.append("Preis: ");
            sb.append(price);
            sb.append("€ ");
        }
        return sb.toString().trim();
    }

    public Henchman next(){
        if (isMax()) {
            throw new IllegalStateException("henchman already at max");
        }
        return values()[ordinal()+1];
    }
}
