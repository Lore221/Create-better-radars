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

public class AntiAirRadarBearingBlockEntity extends RadarBearingBlockEntity {
    public AntiAirRadarBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // Тип радара. Может использоваться мониторами, логикой, отладкой или будущими интеграциями.
    @Override
    public String getRadarType() {
        return super.getRadarType();
    }

    // Горизонтальная дальность считается как у обычного радара,
    // но умножается на множитель из server config.
    @Override
    public float getRange() {
        return (float) (super.getRange() * AAConfig.HORIZONTAL_RANGE_MULTIPLIER.get());
    }

    // Информация, которая показывается через Engineer's Goggles.
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean result = super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        int blindZone = AAConfig.ANTI_AIR_BLIND_ZONE_ABOVE.get();
        int verticalRange = AAConfig.VERTICAL_SCAN_UP_RANGE.get();
        double horizontalMultiplier = AAConfig.HORIZONTAL_RANGE_MULTIPLIER.get();

        List<MutableComponent> targets = new ArrayList<>();

        if (AAConfig.ANTI_AIR_SCAN_SABLE.get()) {
            targets.add(Component.translatable("goggle.create_better_radars.target_sable"));
        }

        if (AAConfig.ANTI_AIR_SCAN_PROJECTILES.get()) {
            targets.add(Component.translatable("goggle.create_better_radars.target_projectiles"));
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
                Component.translatable("goggle.create_better_radars.anti_air_radar")
                        .withStyle(ChatFormatting.AQUA)
        );

        tooltip.add(
                Component.literal(" ")
                        .append(
                                Component.translatable(
                                        "goggle.create_better_radars.scan_zone",
                                        blindZone,
                                        verticalRange
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
        // Оригинальный radar bearing считает нагрузку так:
        // базовая нагрузка блока + количество рефлекторов.
        //
        // У нашего блока базовая нагрузка берётся из конфига,
        // потому что он не зарегистрирован через Create Registrate stress transformer.
        float impact = (float) (AAConfig.ANTI_AIR_STRESS_IMPACT.get() + getDishCount());

        this.lastStressApplied = impact;
        return impact;
    }
}