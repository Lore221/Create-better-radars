package ru.limbo2136.createbetterradars.mixin;

import com.happysg.radar.block.monitor.MonitorBlock;
import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.happysg.radar.block.radar.behavior.IRadar;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(targets = "com.happysg.radar.block.monitor.MonitorRenderer", remap = false)
public abstract class MonitorRendererSableSweepFixMixin {
    @Unique
    private static final ThreadLocal<MonitorBlockEntity> create_better_radars$currentMonitor = new ThreadLocal<>();

    @Unique
    private static final ThreadLocal<IRadar> create_better_radars$currentRadar = new ThreadLocal<>();

    @Unique
    private static final ThreadLocal<Boolean> create_better_radars$sweepAngleAlreadyFixed =
            ThreadLocal.withInitial(() -> false);

    @Inject(method = "renderSweep", at = @At("HEAD"))
    private void create_better_radars$captureSweepContext(
            IRadar radar,
            MonitorBlockEntity monitor,
            PoseStack poseStack,
            MultiBufferSource buffer,
            float partialTicks,
            CallbackInfo ci
    ) {
        create_better_radars$currentRadar.set(radar);
        create_better_radars$currentMonitor.set(monitor);
        create_better_radars$sweepAngleAlreadyFixed.set(false);
    }

    @Inject(method = "renderSweep", at = @At("RETURN"))
    private void create_better_radars$clearSweepContext(
            IRadar radar,
            MonitorBlockEntity monitor,
            PoseStack poseStack,
            MultiBufferSource buffer,
            float partialTicks,
            CallbackInfo ci
    ) {
        create_better_radars$currentRadar.remove();
        create_better_radars$currentMonitor.remove();
        create_better_radars$sweepAngleAlreadyFixed.remove();
    }

    @ModifyVariable(
            method = "renderSweep",
            at = @At(value = "STORE"),
            index = 12,
            require = 0
    )
    private float create_better_radars$fixFinalPhysicalSweepAngle(float finalAngle) {
        IRadar radar = create_better_radars$currentRadar.get();
        MonitorBlockEntity monitor = create_better_radars$currentMonitor.get();

        if (radar == null || monitor == null) {
            return finalAngle;
        }

        if (!"spinning".equals(radar.getRadarType())) {
            return finalAngle;
        }

        Object ship = create_better_radars$getShipOrControllerShip(monitor);

        if (ship == null) {
            return finalAngle;
        }

        if (Boolean.TRUE.equals(create_better_radars$sweepAngleAlreadyFixed.get())) {
            return finalAngle;
        }

        create_better_radars$sweepAngleAlreadyFixed.set(true);

        Direction localFacing = monitor.getBlockState().getValue(MonitorBlock.FACING);

        float localYaw = create_better_radars$directionYawClockwise(localFacing);
        float shipYaw = create_better_radars$getActualShipYawClockwiseDeg(ship);
        float axisOffset = localFacing.getAxis() == Direction.Axis.Z ? 180.0F : 0.0F;

        float correction = localYaw + (2.0F * shipYaw) - 180.0F - axisOffset;

        return create_better_radars$wrap(finalAngle + correction);
    }

    @Unique
    private static float create_better_radars$directionYawClockwise(Direction direction) {
        return switch (direction) {
            case NORTH -> 0.0F;
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 270.0F;
            default -> 0.0F;
        };
    }

    @Unique
    private static Object create_better_radars$getShipOrControllerShip(MonitorBlockEntity monitor) {
        Object ship = create_better_radars$getShipReflectively(monitor);

        if (ship != null) {
            return ship;
        }

        try {
            MonitorBlockEntity controller = monitor.getController();

            if (controller != null) {
                return create_better_radars$getShipReflectively(controller);
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    @Unique
    private static Object create_better_radars$getShipReflectively(Object object) {
        if (object == null) {
            return null;
        }

        try {
            Method getShip = object.getClass().getMethod("getShip");
            return getShip.invoke(object);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private static float create_better_radars$getActualShipYawClockwiseDeg(Object ship) {
        try {
            Method logicalPose = ship.getClass().getMethod("logicalPose");
            Object pose = logicalPose.invoke(ship);

            Vector3d forward = new Vector3d(0.0D, 0.0D, 1.0D);

            Method transformNormal = pose.getClass().getMethod("transformNormal", Vector3d.class);
            Object transformed = transformNormal.invoke(pose, forward);

            Vector3d result = transformed instanceof Vector3d vector ? vector : forward;

            double rawYawRad = Math.atan2(result.x(), -result.z());
            float rawYawDeg = (float) Math.toDegrees(rawYawRad);

            return create_better_radars$wrap(rawYawDeg - 90.0F);
        } catch (Throwable ignored) {
            return 0.0F;
        }
    }

    @Unique
    private static float create_better_radars$wrap(float angle) {
        angle %= 360.0F;

        if (angle < 0.0F) {
            angle += 360.0F;
        }

        return angle;
    }
}