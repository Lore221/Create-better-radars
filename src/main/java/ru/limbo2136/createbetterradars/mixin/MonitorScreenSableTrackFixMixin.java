package ru.limbo2136.createbetterradars.mixin;

import com.happysg.radar.block.radar.behavior.IRadar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.happysg.radar.block.monitor.MonitorScreen", remap = false)
public abstract class MonitorScreenSableTrackFixMixin {
    @Redirect(
            method = {
                    "renderSweep",
                    "renderTracks",
                    "updateHoverFromMouse"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/happysg/radar/block/radar/behavior/IRadar;renderRelativeToMonitor()Z"
            ),
            require = 0
    )
    private boolean create_better_radars$forceRelativeForSpinningRadarOnScreen(IRadar radar) {
        return radar != null && (radar.renderRelativeToMonitor() || "spinning".equals(radar.getRadarType()));
    }
}