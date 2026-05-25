package ru.limbo2136.createbetterradars.block.entity;

import com.happysg.radar.block.radar.bearing.RadarBearingBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import ru.limbo2136.createbetterradars.config.AAConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RlsRadarBearingBlockEntity extends RadarBearingBlockEntity {
    public RlsRadarBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getRange() {
        return (float) (super.getRange() * AAConfig.RLS_HORIZONTAL_RANGE_MULTIPLIER.get());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean result = super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        int verticalRange = AAConfig.RLS_VERTICAL_SCAN_DOWN_RANGE.get();
        int allowedDepth = AAConfig.RLS_ALLOWED_DEPTH.get();
        double horizontalMultiplier = AAConfig.RLS_HORIZONTAL_RANGE_MULTIPLIER.get();

        List<MutableComponent> targets = new ArrayList<>();

        if (AAConfig.RLS_SCAN_PLAYERS.get()) {
            targets.add(Component.translatable("goggle.create_better_radars.target_players"));
        }

        if (AAConfig.RLS_SCAN_HOSTILE_MOBS.get()) {
            targets.add(Component.translatable("goggle.create_better_radars.target_hostile_mobs"));
        }

        if (AAConfig.RLS_SCAN_PASSIVE_MOBS.get()) {
            targets.add(Component.translatable("goggle.create_better_radars.target_passive_mobs"));
        }

        if (AAConfig.RLS_SCAN_SABLE.get()) {
            targets.add(Component.translatable("goggle.create_better_radars.target_sable"));
        }

        MutableComponent targetText;

        if (targets.isEmpty()) {
            targetText = Component.translatable("goggle.create_better_radars.target_none");
        } else {
            targetText = targets.get(0).copy();

            for (int i = 1; i < targets.size(); i++) {
                targetText.append(Component.literal(", "));
                targetText.append(targets.get(i));
            }
        }

        String multiplierText = String.format(Locale.ROOT, "%.2f", horizontalMultiplier);

        tooltip.add(Component.empty());

        tooltip.add(
                Component.translatable("goggle.create_better_radars.rls_radar")
                        .withStyle(ChatFormatting.AQUA)
        );

        tooltip.add(
                Component.literal(" ")
                        .append(
                                Component.translatable(
                                        "goggle.create_better_radars.rls_scan_zone",
                                        verticalRange
                                )
                        )
                        .withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(
                Component.literal(" ")
                        .append(
                                Component.translatable(
                                        "goggle.create_better_radars.rls_allowed_depth",
                                        allowedDepth
                                )
                        )
                        .withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(
                Component.literal(" ")
                        .append(
                                Component.translatable(
                                        "goggle.create_better_radars.targets",
                                        targetText
                                )
                        )
                        .withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(
                Component.literal(" ")
                        .append(
                                Component.translatable(
                                        "goggle.create_better_radars.range_multiplier",
                                        multiplierText
                                )
                        )
                        .withStyle(ChatFormatting.GRAY)
        );

        return result;
    }

    @Override
    public float calculateStressApplied() {
        float impact = (float) (AAConfig.RLS_STRESS_IMPACT.get() + getDishCount());

        this.lastStressApplied = impact;
        return impact;
    }

}
