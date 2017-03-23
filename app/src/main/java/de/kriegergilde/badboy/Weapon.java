package de.kriegergilde.badboy;

/**
 * Created by Klaus on 28.03.2016.
 */
public enum Weapon {

    NONE(0, 0, 0, 0, 0, "keine Waffe"),
    BUTTERFLY_KNIFE(30, 0, 0, 0, 400, "Butterfly-Messer"),
    TOMAHAWK(30, 0, 0, 2, 1_500, "Tomahawk"),
    SAMURAISCHWERT(30, 0, 0, 4, 3500, "Samuraischwert"),
    PISTOL(50, 0, 0, 4, 6_000, "Pistole"),
    REVOLVER(50, 0, 0, 6, 14_000, "Revolver"),
    UZI(50, 0, 0, 8, 35_000, "Uzi"),
    ASSAULT_RIFLE(50, 0, 0, 10, 120_000, "Sturmgewehr"),
    SNIPER_RIFLE(50, 0, 0, 12, 300_000, "Scharfschützengewehr"),
    MINIGUN(70, 0, 0, 14, 800_000, "Minigun");

    private final int era;
    private final int cooldownRed; // in h
    private final int lifeBonus;
    private final int successBonus; // in %-Punkten
    private final int price;
    private final String name;

    private Weapon(int era, int cooldownRed, int lifeBonus, int successBonus, int price, String name) {
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

    public Weapon next(){
        if (isMax()) {
            throw new IllegalStateException("weapon already at max");
        }
        return values()[ordinal()+1];
    }
}
