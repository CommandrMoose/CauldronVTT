package org.river.cauldron.cauldron;

import com.google.gson.Gson;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;
import org.river.cauldron.data.CauldronCharacterData;
import org.river.cauldron.data.CauldronCharacterDataset;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CauldronCharacters {
    public static List<CauldronCharacterData> SERVER_CHARACTER_DATA = new ArrayList<>();

    public static final String characterSaveFileLocation = FabricLoader.getInstance().getGameDir() + "/cauldron/character_data.cauldron";
    static ObjectMapper om = new ObjectMapper(new Gson());

    public static void readFromFiles() {

        System.out.println("Starting to read files");

        File characterSaveFile = new File(characterSaveFileLocation);

        StringBuilder line = new StringBuilder();
        try (Scanner myReader = new Scanner(characterSaveFile)) {
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
                line.append(data);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        if (!line.isEmpty()) {
            CauldronCharacterDataset jsonNode = om.readValue(line.toString(), CauldronCharacterDataset.class);
            SERVER_CHARACTER_DATA = jsonNode.characterData;
        }
    }

    public static void writeToFile() {
        var characterDataSet = new CauldronCharacterDataset();
        characterDataSet.characterData = SERVER_CHARACTER_DATA;

        SERVER_CHARACTER_DATA.forEach(x -> { System.out.println(x);});



        var fileValue = om.writeValueAsString(characterDataSet);
        var file = new File(characterSaveFileLocation);
        try {
            file.getParentFile().mkdir();
            file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(characterSaveFileLocation));
            System.out.println("Should be writing to file");
            writer.write(fileValue);

            writer.close();
        }catch (Exception e) {
            System.out.println("ERRRRR - " + e.getLocalizedMessage());
        }
    }

}
