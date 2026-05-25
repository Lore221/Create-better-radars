package ru.limbo2136.createbetterradars.mixin;

import com.happysg.radar.block.radar.behavior.RadarScanningBlockBehavior;
import com.happysg.radar.compat.vs2.PhysicsHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.limbo2136.createbetterradars.block.entity.AntiAirRadarBearingBlockEntity;
import ru.limbo2136.createbetterradars.block.entity.RlsRadarBearingBlockEntity;
import ru.limbo2136.createbetterradars.config.AAConfig;

import java.util.Set;

@Mixin(value = RadarScanningBlockBehavior.class, remap = false)
public abstract class RadarScanningBlockBehaviorMixin {
    // Горизонтальная дальность радара.
    @Shadow
    private double range;

    // Текущая точка сканирования радара.
    @Shadow
    private Vec3 scanPos;

    // Текущий угол обзора радара.
    @Shadow
    private double angle;

    // Угол обзора радара в градусах.
    @Shadow
    private int fov;

    // Флаги категорий сканирования из Create: Radars.
    @Shadow
    private boolean scanPlayers;

    @Shadow
    private boolean scanSable;

    @Shadow
    private boolean scanContraptions;

    @Shadow
    private boolean scanMobs;

    @Shadow
    private boolean scanAnimals;

    @Shadow
    private boolean scanProjectiles;

    @Shadow
    private boolean scanItems;

    @Shadow
    private Set<Entity> scannedEntities;

    // Получаем BlockEntity из родительского класса BlockEntityBehaviour.
    private SmartBlockEntity create_better_radars$getBlockEntity() {
        return ((BlockEntityBehaviourAccessor) (Object) this).create_better_radars$getBlockEntity();
    }

    // Проверяем, что поведение сканирования принадлежит именно нашему ПВО-радару.
    private boolean create_better_radars$isAntiAirRadar() {
        return create_better_radars$getBlockEntity() instanceof AntiAirRadarBearingBlockEntity;
    }

    private boolean create_better_radars$isRlsRadar() {
        return create_better_radars$getBlockEntity() instanceof RlsRadarBearingBlockEntity;
    }

    // Настраиваем категории целей для ПВО-радара.
    //
    // ПВО не должен быть универсальным радаром:
    // - игроков не видит;
    // - мобов не видит;
    // - животных не видит;
    // - предметы не видит;
    // - обычные contraptions не видит;
    // - Sable/Aeronautics видит, если включено в конфиге;
    // - снаряды/Projectile видит, если включено в конфиге.
    @Inject(method = "tick", at = @At("HEAD"))
    private void create_better_radars$configureAntiAirTargets(CallbackInfo ci) {
        if (!create_better_radars$isAntiAirRadar()) {
            return;
        }

        this.scanPlayers = false;
        this.scanContraptions = false;
        this.scanMobs = false;
        this.scanAnimals = false;
        this.scanItems = false;

        this.scanSable = AAConfig.ANTI_AIR_SCAN_SABLE.get();
        this.scanProjectiles = AAConfig.ANTI_AIR_SCAN_PROJECTILES.get();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void create_better_radars$configureRlsTargets(CallbackInfo ci) {
        if (!create_better_radars$isRlsRadar()) {
            return;
        }

        this.scanPlayers = AAConfig.RLS_SCAN_PLAYERS.get();
        this.scanSable = AAConfig.RLS_SCAN_SABLE.get();
        // Sable candidates are still collected by Create: Radars. RLS applies its downward
        // zone and surface-depth checks through the shared position-based FOV check.
        this.scanContraptions = false;
        this.scanMobs = AAConfig.RLS_SCAN_HOSTILE_MOBS.get() || AAConfig.RLS_SCAN_PASSIVE_MOBS.get();
        this.scanAnimals = AAConfig.RLS_SCAN_PASSIVE_MOBS.get();
        this.scanProjectiles = false;
        this.scanItems = false;
    }

    // Меняем объём сканирования.
    //
    // Обычный радар:
    // Y от radarY - yScan до radarY + yScan
    //
    // ПВО-радар:
    // Y от radarY + blindZoneAbove до radarY + verticalScanUpRange
    @Inject(method = "getRadarAABB", at = @At("HEAD"), cancellable = true)
    private void create_better_radars$getAntiAirRadarAABB(CallbackInfoReturnable<AABB> cir) {
        if (!create_better_radars$isAntiAirRadar()) {
            return;
        }

        SmartBlockEntity blockEntity = create_better_radars$getBlockEntity();
        Vec3 radarPos = PhysicsHandler.getWorldVec((BlockEntity) blockEntity);

        double x = radarPos.x;
        double y = radarPos.y;
        double z = radarPos.z;

        double verticalRange = AAConfig.VERTICAL_SCAN_UP_RANGE.get();
        double blindZone = AAConfig.effectiveAntiAirBlindZone();

        Level level = blockEntity.getLevel();

        double minY = level != null
                ? Math.max(y + blindZone, level.getMinBuildHeight())
                : y + blindZone;

        double maxY = level != null
                ? Math.min(y + verticalRange, level.getMaxBuildHeight())
                : y + verticalRange;

        cir.setReturnValue(new AABB(
                x - this.range,
                minY,
                z - this.range,
                x + this.range,
                maxY,
                z + this.range
        ));
    }

    @Inject(method = "getRadarAABB", at = @At("HEAD"), cancellable = true)
    private void create_better_radars$getRlsRadarAABB(CallbackInfoReturnable<AABB> cir) {
        if (!create_better_radars$isRlsRadar()) {
            return;
        }

        SmartBlockEntity blockEntity = create_better_radars$getBlockEntity();
        Vec3 radarPos = PhysicsHandler.getWorldVec((BlockEntity) blockEntity);

        double x = radarPos.x;
        double y = radarPos.y;
        double z = radarPos.z;

        double verticalRange = AAConfig.RLS_VERTICAL_SCAN_DOWN_RANGE.get();

        Level level = blockEntity.getLevel();

        double minY = level != null
                ? Math.max(y - verticalRange, level.getMinBuildHeight())
                : y - verticalRange;

        double maxY = level != null
                ? Math.min(y, level.getMaxBuildHeight())
                : y;

        cir.setReturnValue(new AABB(
                x - this.range,
                minY,
                z - this.range,
                x + this.range,
                maxY,
                z + this.range
        ));
    }

    // Меняем финальную проверку цели.
    //
    // Обычный радар использует Math.abs по Y, поэтому видит вверх и вниз.
    //
    // ПВО:
    // dy < blindZoneAbove — цель в слепой зоне, не видим.
    // dy > verticalScanUpRange — цель слишком высоко, не видим.
    @Inject(method = "isInFovAndRange", at = @At("HEAD"), cancellable = true)
    private void create_better_radars$isInAntiAirFovAndRange(Vec3 target, CallbackInfoReturnable<Boolean> cir) {
        if (!create_better_radars$isAntiAirRadar()) {
            return;
        }

        double dx = target.x() - this.scanPos.x();
        double dz = target.z() - this.scanPos.z();
        double dy = target.y() - this.scanPos.y();

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double verticalRange = AAConfig.VERTICAL_SCAN_UP_RANGE.get();
        double blindZone = AAConfig.effectiveAntiAirBlindZone();

        if (horizontalDistance > this.range) {
            cir.setReturnValue(false);
            return;
        }

        if (dy < blindZone) {
            cir.setReturnValue(false);
            return;
        }

        if (dy > verticalRange) {
            cir.setReturnValue(false);
            return;
        }

        if (horizontalDistance < 2.0D) {
            cir.setReturnValue(true);
            return;
        }

        double angleToTarget = Math.toDegrees(Math.atan2(dx, dz));
        angleToTarget = (angleToTarget + 360.0D) % 360.0D;

        double angleDiff = Math.abs(angleToTarget - this.angle);

        if (angleDiff > 180.0D) {
            angleDiff = 360.0D - angleDiff;
        }

        cir.setReturnValue(angleDiff <= this.fov / 2.0D);
    }

    @Inject(method = "isInFovAndRange", at = @At("HEAD"), cancellable = true)
    private void create_better_radars$isInRlsFovAndRange(Vec3 target, CallbackInfoReturnable<Boolean> cir) {
        if (!create_better_radars$isRlsRadar()) {
            return;
        }

        double dx = target.x() - this.scanPos.x();
        double dz = target.z() - this.scanPos.z();
        double dy = target.y() - this.scanPos.y();

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double verticalRange = AAConfig.RLS_VERTICAL_SCAN_DOWN_RANGE.get();

        if (horizontalDistance > this.range) {
            cir.setReturnValue(false);
            return;
        }

        if (dy > 0.0D) {
            cir.setReturnValue(false);
            return;
        }

        if (-dy > verticalRange) {
            cir.setReturnValue(false);
            return;
        }

        if (!create_better_radars$passesRlsSurfaceCheck(target)) {
            cir.setReturnValue(false);
            return;
        }

        if (horizontalDistance < 2.0D) {
            cir.setReturnValue(true);
            return;
        }

        double angleToTarget = Math.toDegrees(Math.atan2(dx, dz));
        angleToTarget = (angleToTarget + 360.0D) % 360.0D;

        double angleDiff = Math.abs(angleToTarget - this.angle);

        if (angleDiff > 180.0D) {
            angleDiff = 360.0D - angleDiff;
        }

        cir.setReturnValue(angleDiff <= this.fov / 2.0D);
    }

    @Inject(method = "scanForEntityTracks", at = @At("TAIL"))
    private void create_better_radars$filterRlsEntityCandidates(CallbackInfo ci) {
        if (!create_better_radars$isRlsRadar()) {
            return;
        }

        this.scannedEntities.removeIf(entity -> !create_better_radars$isRlsEntityCategoryEnabled(entity));
    }

    private boolean create_better_radars$isRlsEntityCategoryEnabled(Entity entity) {
        if (entity instanceof net.minecraft.world.entity.player.Player) {
            return AAConfig.RLS_SCAN_PLAYERS.get();
        }

        if (entity instanceof Mob) {
            if (entity instanceof Enemy) {
                return AAConfig.RLS_SCAN_HOSTILE_MOBS.get();
            }

            if (entity instanceof Animal || AAConfig.RLS_SCAN_PASSIVE_MOBS.get()) {
                return AAConfig.RLS_SCAN_PASSIVE_MOBS.get();
            }

            return false;
        }

        return false;
    }

    private boolean create_better_radars$passesRlsSurfaceCheck(Vec3 target) {
        SmartBlockEntity blockEntity = create_better_radars$getBlockEntity();
        Level level = blockEntity.getLevel();

        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        BlockPos targetPos = BlockPos.containing(target);

        if (!serverLevel.hasChunkAt(targetPos)) {
            return false;
        }

        // MOTION_BLOCKING_NO_LEAVES follows the practical ground/roof surface while ignoring tree leaves.
        // That keeps shallow trenches and houses visible without turning RLS into a deep cave x-ray.
        int surfaceY = serverLevel.getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                targetPos.getX(),
                targetPos.getZ()
        );

        return target.y() >= surfaceY - AAConfig.RLS_ALLOWED_DEPTH.get();
    }
}
