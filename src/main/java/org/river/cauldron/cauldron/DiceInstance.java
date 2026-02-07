package org.river.cauldron.cauldron;

public class DiceInstance {

    private String diceName;
    private int maxDiceNumber;
    private int currentValue = -1;

    public DiceInstance(String diceName, int maxDiceNumber) {
        this.diceName = diceName;
        this.maxDiceNumber = maxDiceNumber;
    }

    public void roll() {
        currentValue = DiceHandler.rollDice(maxDiceNumber);
    }


    public String getDiceName() {
        return diceName;
    }

    public int getCurrentValue() {
        return currentValue;
    }
}
