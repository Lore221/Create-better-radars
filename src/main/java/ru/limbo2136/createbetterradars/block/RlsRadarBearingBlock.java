package ru.limbo2136.createbetterradars.block;

import com.happysg.radar.block.radar.bearing.RadarBearingBlock;
import com.happysg.radar.block.radar.bearing.RadarBearingBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import ru.limbo2136.createbetterradars.block.entity.RlsRadarBearingBlockEntity;
import ru.limbo2136.createbetterradars.registry.AABlockEntityTypes;

public class RlsRadarBearingBlock extends RadarBearingBlock {
    public RlsRadarBearingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class<RadarBearingBlockEntity> getBlockEntityClass() {
        return (Class) RlsRadarBearingBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RadarBearingBlockEntity> getBlockEntityType() {
        return AABlockEntityTypes.RLS_RADAR_BEARING.get();
    }
}
