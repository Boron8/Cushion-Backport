package me.creeper.cushionbackport.entity;

import me.creeper.cushionbackport.CBRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Cushion extends BlockAttachedEntity {
    private static final DyeColor DEFAULT_COLOR = DyeColor.WHITE;
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(Cushion.class, EntityDataSerializers.INT);

    public Cushion(EntityType<? extends Cushion> type, Level level) {
        super(type, level);
    }

    public Cushion(Level level, BlockPos pos) {
        super(CBRegistry.CUSHION.get(), level, pos);
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLOR));
    }

    public void setColor(DyeColor color) {
        this.entityData.set(DATA_COLOR, color.getId());
    }

    @Override
    public boolean dampensVibrations() {
        return true;
    }

    @Override
    public void dropItem(ServerLevel level, Entity causedBy) {
        this.playSound(CBRegistry.CUSHION_BREAK.get(), 1.0f, 1.0f);
        this.showBreakingParticles();
        if (level.getGameRules().get(GameRules.ENTITY_DROPS)) {
            // hasInfiniteMaterials = creative mode = don't drop
            if (!(causedBy instanceof Player player && player.hasInfiniteMaterials())) {
                this.spawnAtLocation(level, CBRegistry.cushionItemByColor(this.getColor()));
            }
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!player.isSecondaryUseActive() && !this.isVehicle() && (this.level().isClientSide() || player.startRiding(this))) {
            if (!this.level().isClientSide()) {
                // CLIENT
                this.playSound(CBRegistry.CUSHION_SIT.get(), 1.0f, 1.0f);
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.SUCCESS;
            }
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!this.level().isClientSide() && this.getRemovalReason() == null) {
            this.playSound(CBRegistry.CUSHION_GET_UP.get(), 1.0f, 1.0f);
        }
    }

    @Override
    public void onPassengerTurned(Entity passenger) {
        passenger.setYBodyRot(passenger.getYRot());
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(CBRegistry.cushionItemByColor(this.getColor()));
    }

    @Override
    public void setPos(double x, double y, double z) {
        this.setPosRaw(x, y, z);
        super.setPos(x, y, z);
    }

    // Mojang's implementation of 'wouldSuriveAt' is misspelled (MC-310451), so this one is too
    public static boolean wouldSuriveAt(Level level, AABB boundingBox) {
        AABB anchorBox = new AABB(
                boundingBox.minX, boundingBox.minY - 0.015625, boundingBox.minZ,
                Math.nextDown(boundingBox.maxX), boundingBox.maxY, Math.nextDown(boundingBox.maxZ)
        );

        for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(anchorBox.minX), Mth.floor(anchorBox.minY), Mth.floor(anchorBox.minZ), Mth.floor(anchorBox.maxX), Mth.floor(anchorBox.maxY), Mth.floor(anchorBox.maxZ))) {
            BlockState blockState = level.getBlockState(blockPos);
            VoxelShape shape = blockState.getShape(level, blockPos);
            if (!shape.isEmpty() && shape.bounds().move(blockPos).intersects(anchorBox)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean survives() {
        Level level = this.level();
        AABB boundingBox = this.getBoundingBox();
        if (!wouldSuriveAt(level, boundingBox)) {
            return false;
        }

        AABB inner = boundingBox.deflate(1.0E-7);
        for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(inner.minX), Mth.floor(inner.minY), Mth.floor(inner.minZ), Mth.floor(inner.maxX), Mth.floor(inner.maxY), Mth.floor(inner.maxZ))) {
            if (!level.getBlockState(blockPos).isCollisionShapeFullBlock(level, blockPos)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(DATA_COLOR, DEFAULT_COLOR.getId());
    }

    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel level) {
            level.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, CBRegistry.woolByColor(this.getColor()).defaultBlockState()),
                    this.getX(),
                    this.getY(0.666666),
                    this.getZ(),
                    10,
                    this.getBbWidth() / 4.0f,
                    this.getBbHeight() / 4.0f,
                    this.getBbWidth() / 4.0f,
                    0.05
            );
        }
    }

    @Override
    protected void recalculateBoundingBox() {
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected AABB makeBoundingBox(Vec3 pos) {
        return new AABB(
                pos.x - 0.5, pos.y, pos.z - 0.5,
                pos.x + 0.5, pos.y + 0.25, pos.z + 0.5
        );
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("color", DyeColor.CODEC, this.getColor());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setColor(input.read("color", DyeColor.CODEC).orElse(DEFAULT_COLOR));
    }
}
