package ru.limbo2136.createbetterradars.mixin;

import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.happysg.radar.block.radar.behavior.IRadar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Method;

@Mixin(targets = "com.happysg.radar.block.monitor.MonitorRenderer", remap = false)
public abstract class MonitorRendererSableTrackFixMixin {
    @Redirect(
            method = "renderTrack",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/happysg/radar/block/radar/behavior/IRadar;renderRelativeToMonitor()Z"
            ),
            require = 1
    )
    private boolean create_better_radars$forceRelativeForSpinningRadar(IRadar radar) {
        boolean original = radar != null && radar.renderRelativeToMonitor();
        boolean forced = radar != null && "spinning".equals(radar.getRadarType());

        return original || forced;
    }

    @Redirect(
            method = "renderTrack",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/happysg/radar/block/monitor/MonitorBlockEntity;getShip()Ldev/ryanhcode/sable/companion/SubLevelAccess;"
            ),
            require = 0
    )
    @Coerce
    private Object create_better_radars$useControllerShipForTrackRotation(MonitorBlockEntity monitor) {
        Object ownShip = create_better_radars$getShipReflectively(monitor);
        Object controllerShip = create_better_radars$getControllerShipReflectively(monitor);

        return ownShip != null ? ownShip : controllerShip;
    }

    @ModifyArg(
            method = "renderTrack",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/happysg/radar/block/monitor/MonitorRenderer;rotateAroundY(Lnet/minecraft/world/phys/Vec3;D)Lnet/minecraft/world/phys/Vec3;"
            ),
            index = 1,
            require = 0
    )
    private double create_better_radars$modifyTrackRotationAngle(double originalAngle) {
        return originalAngle;
    }

    @Unique
    private static Object create_better_radars$getShipReflectively(MonitorBlockEntity monitor) {
        if (monitor == null) {
            return null;
        }

        try {
            Method getShip = monitor.getClass().getMethod("getShip");
            return getShip.invoke(monitor);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private static Object create_better_radars$getControllerShipReflectively(MonitorBlockEntity monitor) {
        if (monitor == null) {
            return null;
        }

        try {
            MonitorBlockEntity controller = monitor.getController();

            if (controller == null) {
                return null;
            }

            Method getShip = controller.getClass().getMethod("getShip");
            return getShip.invoke(controller);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
