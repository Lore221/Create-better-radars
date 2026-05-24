package ru.limbo2136.createbetterradars.util;

import com.happysg.radar.block.monitor.MonitorBlock;
import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.happysg.radar.compat.vs2.PhysicsHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;

public class MonitorOrientationUtil {
    public static Direction getPhysicalFacing(MonitorBlockEntity monitor) {
        if (monitor == null) {
            return Direction.NORTH;
        }

        Direction localFacing = monitor.getBlockState().getValue(MonitorBlock.FACING);

        BlockEntity reference = getControllerOrSelf(monitor);

        // Если монитор не на Sable, оставляем обычное поведение.
        if (getShipReflectively(reference) == null) {
            return localFacing;
        }

        Vec3 localVec = new Vec3(
                localFacing.getStepX(),
                localFacing.getStepY(),
                localFacing.getStepZ()
        );

        Vec3 worldVec = PhysicsHandler.getWorldVecDirectionTransform(localVec, reference);

        return nearestHorizontalDirection(worldVec, localFacing);
    }

    private static BlockEntity getControllerOrSelf(MonitorBlockEntity monitor) {
        try {
            MonitorBlockEntity controller = monitor.getController();

            if (controller != null) {
                return controller;
            }
        } catch (Throwable ignored) {
        }

        return monitor;
    }

    private static Object getShipReflectively(Object blockEntity) {
        if (blockEntity == null) {
            return null;
        }

        try {
            Method getShip = blockEntity.getClass().getMethod("getShip");
            return getShip.invoke(blockEntity);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Direction nearestHorizontalDirection(Vec3 vec, Direction fallback) {
        double x = vec.x;
        double z = vec.z;

        if (Math.abs(x) >= Math.abs(z)) {
            return x >= 0.0D ? Direction.EAST : Direction.WEST;
        }

        return z >= 0.0D ? Direction.SOUTH : Direction.NORTH;
    }
}