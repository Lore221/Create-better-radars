package ru.limbo2136.createbetterradars.mixin;

import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.happysg.radar.block.radar.behavior.IRadar;
import com.happysg.radar.block.radar.track.RadarTrack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(targets = "com.happysg.radar.block.monitor.MonitorRenderer", remap = false)
public abstract class MonitorRendererSableTrackFixMixin {
    /*
     * Test modes:
     *
     * 0 = original Create: Radars angle
     * 1 = inverted original angle
     * 2 = original angle + 180 degrees
     * 3 = inverted original angle + 180 degrees
     * 4 = inverted original angle + 90 degrees
     * 5 = inverted original angle - 90 degrees
     * 6 = original angle + 90 degrees
     * 7 = original angle - 90 degrees
     *
     * Start with 0. This is the control mode.
     */
    @Unique
    private static final int CBR_ROTATION_MODE = 0;

    @Unique
    private static int cbr$headDebugCounter = 0;

    @Unique
    private static int cbr$relativeDebugCounter = 0;

    @Unique
    private static int cbr$getShipDebugCounter = 0;

    @Unique
    private static int cbr$angleDebugCounter = 0;

    @Inject(method = "renderTrack", at = @At("HEAD"))
    private void create_better_radars$debugRenderTrackHead(
            RadarTrack track,
            MonitorBlockEntity monitor,
            IRadar radar,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int index,
            CallbackInfo ci
    ) {
        if (cbr$headDebugCounter++ < 20) {
            Object ownShip = create_better_radars$getShipReflectively(monitor);
            Object controllerShip = create_better_radars$getControllerShipReflectively(monitor);

            System.out.println("[CBR DEBUG] renderTrack HEAD"
                    + " index=" + index
                    + " radarType=" + (radar == null ? "null" : radar.getRadarType())
                    + " relative=" + (radar != null && radar.renderRelativeToMonitor())
                    + " ownShip=" + (ownShip != null)
                    + " controllerShip=" + (controllerShip != null));
        }
    }

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

        boolean result = original || forced;

        if (cbr$relativeDebugCounter++ < 40) {
            System.out.println("[CBR DEBUG] renderRelative redirect"
                    + " radarType=" + (radar == null ? "null" : radar.getRadarType())
                    + " original=" + original
                    + " forced=" + forced
                    + " result=" + result);
        }

        return result;
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

        Object result = ownShip != null ? ownShip : controllerShip;

        if (cbr$getShipDebugCounter++ < 40) {
            System.out.println("[CBR DEBUG] renderTrack getShip redirect"
                    + " ownShip=" + (ownShip != null)
                    + " controllerShip=" + (controllerShip != null)
                    + " result=" + (result != null));
        }

        return result;
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
        double fixedAngle = switch (CBR_ROTATION_MODE) {
            case 0 -> originalAngle;
            case 1 -> -originalAngle;
            case 2 -> originalAngle + Math.PI;
            case 3 -> -originalAngle + Math.PI;
            case 4 -> -originalAngle + (Math.PI / 2.0D);
            case 5 -> -originalAngle - (Math.PI / 2.0D);
            case 6 -> originalAngle + (Math.PI / 2.0D);
            case 7 -> originalAngle - (Math.PI / 2.0D);
            default -> originalAngle;
        };

        if (cbr$angleDebugCounter++ < 40) {
            System.out.println("[CBR DEBUG] track rotation mode="
                    + CBR_ROTATION_MODE
                    + " originalDeg="
                    + Math.toDegrees(originalAngle)
                    + " fixedDeg="
                    + Math.toDegrees(fixedAngle));
        }

        return fixedAngle;
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