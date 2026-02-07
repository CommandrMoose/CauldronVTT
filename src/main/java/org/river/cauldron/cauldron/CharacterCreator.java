package org.river.cauldron.cauldron;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.river.cauldron.data.CauldronCharacterData;
import org.river.cauldron.entity.CharacterTokenEntity;
import org.river.cauldron.network.s2c.UpdateCharacterListS2CPayload;
import org.river.cauldron.registry.CauldronEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CharacterCreator {

    // Responsible for the storage and modification of a character


    public void ListCharacters(PlayerEntity player) {

        if (!player.getEntityWorld().isClient()) {
            CauldronCharacters.SERVER_CHARACTER_DATA.forEach(x -> {
                player.sendMessage(Text.literal(x.id + " - " + x.characterName), false);
            });
        }


    }

    public boolean deleteCharacter(String id) {
        var characterExists = CharacterExists(id);

        if (!characterExists) {
            return false;
        }

        CauldronCharacters.SERVER_CHARACTER_DATA = CauldronCharacters.SERVER_CHARACTER_DATA.stream().filter((x) -> {return !Objects.equals(x.id, id);}).toList();
        return true;
    }

    public boolean createCharacter(String id, String name, String skinName) {
        // Look to see if character could be created
        var characterExists = CharacterExists(id);

        if (characterExists) {
            return false;
        }

        var newCharacter = new CauldronCharacterData(id, name, skinName);
        System.out.println("Created new character");

        CauldronCharacters.SERVER_CHARACTER_DATA.add(newCharacter);

        return true;
    }

    public Optional<CharacterTokenEntity> createDummyCharacter(String id, World world, Vec3d position, float rotation) {
        if (world instanceof ServerWorld sw) {
            var character = GetCharacter(id);
            if (character.isPresent()) {
                CharacterTokenEntity token = new CharacterTokenEntity(CauldronEntities.CHARACTER_TOKEN, world);
                token.setPosition(position);
                token.setCharacterId(character.get().id, character.get().characterSkin);
                token.setBodyYaw(rotation);
                token.setHeadYaw(rotation);
                sw.spawnEntityAndPassengers(token);
                return Optional.of(token);
            }
        }

        return Optional.empty();
    }

    private boolean CharacterExists(String id) {
        return CauldronCharacters.SERVER_CHARACTER_DATA.stream().anyMatch((x) -> {
            return Objects.equals(x.id, id);
        });
    }

    public Optional<CauldronCharacterData> GetCharacter(String id) {
        return CauldronCharacters.SERVER_CHARACTER_DATA.stream().filter((x) -> {
            return Objects.equals(x.id, id);
        }).findFirst();
    }

    // READ

    // WRITE

}
