package ru.limbo2136.createbetterradars.mixin;

import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.happysg.radar.block.radar.behavior.IRadar;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(targets = "com.happysg.radar.block.monitor.MonitorScreen", remap = false)
public abstract class MonitorScreenSableSweepFixMixin {
    @Unique
    private static final ThreadLocal<MonitorBlockEntity> create_better_radars$currentMonitor = new ThreadLocal<>();

    @Inject(method = "renderSweep", at = @At("HEAD"))
    private void create_better_radars$captureSweepContext(
            GuiGraphics graphics,
            MonitorBlockEntity monitor,
            IRadar radar,
            float partialTicks,
            CallbackInfo ci
    ) {
        create_better_radars$currentMonitor.set(monitor);
    }

    @Inject(method = "renderSweep", at = @At("RETURN"))
    private void create_better_radars$clearSweepContext(
            GuiGraphics graphics,
            MonitorBlockEntity monitor,
            IRadar radar,
            float partialTicks,
            CallbackInfo ci
    ) {
        create_better_radars$currentMonitor.remove();
    }

    @Redirect(
            method = "renderSweep",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/happysg/radar/block/radar/behavior/IRadar;getGlobalAngle()F"
            ),
            require = 0
    )
    private float create_better_radars$fixHudSweepAngle(IRadar radar) {
        float original = radar.getGlobalAngle();

        if (radar == null || !"spinning".equals(radar.getRadarType())) {
            return original;
        }

        MonitorBlockEntity monitor = create_better_radars$currentMonitor.get();

        if (monitor == null) {
            return original;
        }

        Object ship = create_better_radars$getShipOrControllerShip(monitor);

        if (ship == null) {
            return original;
        }

        float shipYaw = create_better_radars$getActualShipYawClockwiseDeg(ship);

        // Successful HUD formula from tests:
        // correction = 270 + 3 * shipYaw
        float correction = 270.0F + (3.0F * shipYaw);

        return create_better_radars$wrap(original + correction);
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
