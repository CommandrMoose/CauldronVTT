package org.river.cauldron.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class ClientUtil {

    //note: very resource intense, try to avoid repetitive calls
    public static boolean doesExist(Identifier id) {
        return MinecraftClient.getInstance().getResourceManager().getResource(id).isPresent();
    }



}
