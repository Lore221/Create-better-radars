package ru.limbo2136.createbetterradars.block;

import com.happysg.radar.block.radar.bearing.RadarBearingBlock;
import com.happysg.radar.block.radar.bearing.RadarBearingBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import ru.limbo2136.createbetterradars.block.entity.AntiAirRadarBearingBlockEntity;
import ru.limbo2136.createbetterradars.registry.AABlockEntityTypes;

public class AntiAirRadarBearingBlock extends RadarBearingBlock {
    public AntiAirRadarBearingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // Обычный RadarBearingBlock ожидает RadarBearingBlockEntity.
    // Наш BlockEntity является его наследником, поэтому такой cast безопасен.
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class<RadarBearingBlockEntity> getBlockEntityClass() {
        return (Class) AntiAirRadarBearingBlockEntity.class;
    }

    // Здесь мы подменяем тип BlockEntity на наш собственный.
    // Без этого новый блок использовал бы обычную BlockEntity радара.
    @Override
    public BlockEntityType<? extends RadarBearingBlockEntity> getBlockEntityType() {
        return AABlockEntityTypes.ANTI_AIR_RADAR_BEARING.get();
    }
}