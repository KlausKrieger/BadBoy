package de.kriegergilde.badboy;

import java.util.Random;

/**
 * Created by Klaus on 25.03.2016.
 */
public final class Dice {

    private final static Random rand = new Random();

    private Dice(){

    }

    public static int dice(int sides){
        return rand.nextInt(sides)+1;
    }

    public static int dice(int fromIncluding, int toIncluding){
        return rand.nextInt(toIncluding-fromIncluding+1)+fromIncluding;
    }


}
