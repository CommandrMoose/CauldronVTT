package org.river.cauldron.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import org.river.cauldron.components.CauldronWorldDataComponent;
import org.river.cauldron.item.CharacterMiniSpawnerItem;
import org.river.cauldron.registry.CauldronEntityComponents;
import org.river.cauldron.registry.CauldronWorldComponent;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;

import software.bernie.geckolib.animatable.stateless.StatelessGeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CharacterTokenEntity extends PathAwareEntity implements StatelessGeoEntity {


    public static final DataTicket<String> CHARACTER_SKIN_TICKET = DataTicket.create("character_skin", String.class);
    public static final DataTicket<Boolean> IS_SELECTED_TICKET = DataTicket.create("is_selected", Boolean.class);

    private String characterId;
    private String characterSkin;

    public static final TrackedData<Boolean> IS_SELECTED = DataTracker.registerData(CharacterTokenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> CHARACTER_ID = DataTracker.registerData(CharacterTokenEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<String> CHARACTER_SKIN_DATA = DataTracker.registerData(CharacterTokenEntity.class, TrackedDataHandlerRegistry.STRING);

    public CharacterTokenEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_SELECTED, false);
        builder.add(CHARACTER_ID, "");
        builder.add(CHARACTER_SKIN_DATA, "");
    }

    public void setCharacterId(String characterId, String characterSkin) {
        this.characterId = characterId;
        this.characterSkin = characterSkin;
        this.dataTracker.set(CHARACTER_ID, characterId);
        this.dataTracker.set(CHARACTER_SKIN_DATA, characterSkin);
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean saveData(WriteView view) {

        if (characterId != null && !characterId.isEmpty()) {
            view.putString("character_id", characterId);
            view.putString("character_skin", characterSkin);
        }
        return super.saveData(view);
    }

    @Override
    public void readData(ReadView view) {
        super.readData(view);

        var characterId = view.getString("character_id", "default");
        var characterSkin = view.getString("character_skin", "default");

        this.characterId = characterId;
        this.characterSkin = characterSkin;

        this.dataTracker.set(CHARACTER_ID, characterId);
        this.dataTracker.set(CHARACTER_SKIN_DATA, characterSkin);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {

        if (player.getActiveHand() == hand) {


                var playerComponent = CauldronEntityComponents.PLAYER.get(player);
                if (this.isSelected()) {

                    this.setSelected(false);
                    playerComponent.setActiveCharacter(this);
                    return super.interactMob(player, hand);
                }


                playerComponent.setActiveCharacter(this);

                var otherTokens = player.getEntityWorld().getEntitiesByType(TypeFilter.instanceOf(CharacterTokenEntity.class), new Box(getBlockPos()).expand(32), EntityPredicates.VALID_ENTITY);
                otherTokens.forEach(x -> {
                    if (x == this) {return;}
                    x.setSelected(false);
                });

                this.setSelected(true);

        }



        return super.interactMob(player, hand);
    }

    public void pathFindToPosition(BlockPos pos) {
        this.navigation.startMovingTo(pos.getX() + 0.5f, pos.getY(), pos.getZ()+ 0.5f, 0, 1.25f);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 1000)
                .add(EntityAttributes.MAX_HEALTH, 2000.0) // 2000HP
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3); // Adjusted speed
    }


    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {

        var player = source.getAttacker();

        if (player instanceof ServerPlayerEntity sp && player.getEntityWorld() instanceof ServerWorld entityWorld) {

            if (sp.getMainHandStack().getItem() instanceof CharacterMiniSpawnerItem || sp.getOffHandStack().getItem() instanceof CharacterMiniSpawnerItem) {
                CauldronWorldDataComponent comp = CauldronWorldComponent.WORLD.getNullable(this.getEntityWorld());
                if (comp instanceof CauldronWorldDataComponent) {

                    if (!comp.getEncounterManager().isHasEncounterStarted()) {
                        setPos(0, -10000, 0);
                    }
                }
            }
        }

        return super.damage(world, source, amount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}

    @Override
    public @Nullable AnimatableInstanceCache animatableCacheOverride() {
        return cache;
    }

    public boolean isSelected() {return dataTracker.get(IS_SELECTED);}
    public void setSelected(boolean state) {dataTracker.set(IS_SELECTED, state);}

    public String getCharacterSkin() { return dataTracker.get(CHARACTER_SKIN_DATA);}
    public String getCharacterId() { return dataTracker.get(CHARACTER_ID);}

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    @Override
    public void tick() {
        super.tick();

        // Preference the client side unless you need the server side for some reason. The code is the same either way.
        if (this.getEntityWorld().isClient()) {
            // Just play the living animation all the time
            playAnimation(DefaultAnimations.LIVING);

        }
    }

}
