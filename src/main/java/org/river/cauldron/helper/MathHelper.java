package org.river.cauldron.helper;

public class MathHelper {

    public static Integer tryParseInt(Object obj) {
        Integer retVal;
        try {
            retVal = Integer.parseInt((String) obj);
        } catch (NumberFormatException nfe) {
            retVal = 0; // or null if that is your preference
        }
        return retVal;
    }

}
