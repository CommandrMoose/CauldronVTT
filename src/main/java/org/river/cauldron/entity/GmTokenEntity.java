package org.river.cauldron.entity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.river.cauldron.cauldron.CauldronCharacters;
import org.river.cauldron.cauldron.CauldronInitiativePlacement;
import org.river.cauldron.components.CauldronPlayerComponent;
import org.river.cauldron.components.CauldronWorldDataComponent;
import org.river.cauldron.item.GmTokenSpawnerItem;
import org.river.cauldron.network.s2c.OpenGmTokenScreenS2CPayload;
import org.river.cauldron.registry.CauldronEntityComponents;
import org.river.cauldron.registry.CauldronWorldComponent;

import java.util.Objects;
import java.util.UUID;

public class GmTokenEntity extends CharacterTokenEntity {

    private boolean spawnOnInit = false;
    private boolean addToInitiativeOnSpawn = false;
    private String encounterId;
    private float initiative = 0;
    private String tokenId;

    public GmTokenEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 1000)
                .add(EntityAttributes.MAX_HEALTH, 21000) // 2000HP
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3); // Adjusted speed
    }


    @Override
    public Box getBoundingBox(EntityPose pose) {
        return Box.from(new Vec3d(0,0,0));
    }


    @Override
    public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
        return true;
    }


    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {

        var player = source.getAttacker();

        if (player instanceof ServerPlayerEntity sp && player.getEntityWorld() instanceof ServerWorld entityWorld) {

            if (sp.getMainHandStack().getItem() instanceof GmTokenSpawnerItem || sp.getOffHandStack().getItem() instanceof GmTokenSpawnerItem) {
                CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(this.getEntityWorld());
                if (comp instanceof CauldronWorldDataComponent) {
                    comp.getEncounterDataManager().removeGmTokenFromEncounter(this.getEncounterId(), this.getTokenId());
                    sp.sendMessage(Text.literal("Removed from the encounter data."), true);
                    setPos(0, -10000, 0);
                }
            }
        }

        return super.damage(world, source, amount);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {

        if (player.getEntityWorld() instanceof ServerWorld world && hand == Hand.MAIN_HAND) {

            if (player.getMainHandStack().getItem() instanceof GmTokenSpawnerItem || player.getOffHandStack().getItem() instanceof GmTokenSpawnerItem) {
                CauldronPlayerComponent playerComponent = CauldronEntityComponents.PLAYER.get(player);
                if (playerComponent.getEncounterModifierId() == null) {
                    player.sendMessage(Text.literal("You must be modifying the correct encounter to edit tokens."), true);
                    return ActionResult.FAIL;
                }

                if (!Objects.equals(playerComponent.getEncounterModifierId(), this.encounterId)) {
                    player.sendMessage(Text.literal("This token is not apart of the encounter."), true);
                    return ActionResult.FAIL;
                }

                OpenGmTokenScreenS2CPayload payload = new OpenGmTokenScreenS2CPayload(this.uuidString, CauldronCharacters.SERVER_CHARACTER_DATA,  this.getCharacterId() != null ? this.getCharacterId() : "default", this.addToInitiativeOnSpawn, this.spawnOnInit);
                ServerPlayNetworking.send((ServerPlayerEntity) player, payload);
            }

        }

        return ActionResult.PASS;
    }

    public void spawn() {

        CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(this.getEntityWorld());
        if (comp instanceof CauldronWorldDataComponent) {


            var characterToken = comp.GetCharacterCreator().createDummyCharacter(this.getCharacterId(), this.getEntityWorld(), this.getBlockPos().toCenterPos(), this.getYaw());

            if (this.addToInitiativeOnSpawn && characterToken.isPresent()) {
                System.out.println("Adding" + characterToken.get().getCharacterId() + " to the initiative!!!");


                comp.getEncounterManager().addToInitiative(characterToken.get(), new CauldronInitiativePlacement("GM", getCharacterId(), (int) getInitiative()));
            }

            this.setPosition(new Vec3d(0, -100000, 0));

        }
    }


    @Override
    public void readData(ReadView view) {
        super.readData(view);

        this.spawnOnInit = view.getBoolean("spawn_on_init", true);
        this.addToInitiativeOnSpawn = view.getBoolean("add_to_init", true);
        this.encounterId = view.getString("encounter_id", null);
        this.initiative = view.getFloat("initiative", 0);
        this.tokenId = view.getString("tokenId", null);

    }

    @Override
    public boolean saveData(WriteView view) {

        view.putBoolean("spawn_on_init", this.spawnOnInit);
        view.putBoolean("add_to_init", this.addToInitiativeOnSpawn);
        view.putString("encounter_id", this.encounterId);
        view.putFloat("initiative", this.initiative);
        if (this.tokenId != null) {
            view.putString("tokenId", this.tokenId);
        }

        return super.saveData(view);
    }


    public boolean isSpawnOnInit() {
        return spawnOnInit;
    }

    public void setSpawnOnInit(boolean spawnOnInit) {
        this.spawnOnInit = spawnOnInit;
    }

    public boolean isAddToInitiativeOnSpawn() {
        return addToInitiativeOnSpawn;
    }

    public void setAddToInitiativeOnSpawn(boolean addToInitiativeOnSpawn) {
        this.addToInitiativeOnSpawn = addToInitiativeOnSpawn;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public String getEncounterId() {
        return encounterId;
    }

    public void setInitiative(float initiative) {
        this.initiative = initiative;
    }

    public float getInitiative() {
        return initiative;
    }

    public void setTokenId(String id) {
        this.tokenId = id;
    }

    public String getTokenId() {
        return tokenId;
    }
}
