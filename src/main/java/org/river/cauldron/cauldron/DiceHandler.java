package org.river.cauldron.cauldron;

import java.util.Random;

public class DiceHandler {

    public static Random random = new Random();

    public static int rollDice(int maxValue) {
        return random.nextInt(maxValue) + 1;
    }

}
