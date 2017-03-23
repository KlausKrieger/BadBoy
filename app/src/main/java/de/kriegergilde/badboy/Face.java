package de.kriegergilde.badboy;

/**
 * Created by Klaus on 28.03.2016.
 */
public enum Face {

    NONE(0, 0, 0, 0, 0, "keine Kopfbedeckung"),
    GUCCI_SUNGLASSES(20, 1, 0, 0, 250, "Gucci-Sonnenbrille"),
    MOTORCYCLE_HELMET(20, 2, 2, 0, 750, "Motorradhelm"),
    CLOWN_MASK(20, 4, 2, 2, 15000, "Clown-Maske");

    private final int era;
    private final int cooldownRed; // in h
    private final int lifeBonus;
    private final int successBonus; // in %-Punkten
    private final int price;
    private final String name;

    private Face(int era, int cooldownRed, int lifeBonus, int successBonus, int price, String name) {
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

    public Face next(){
        if (isMax()) {
            throw new IllegalStateException("face item already at max");
        }
        return values()[ordinal()+1];
    }
}
