package ru.limbo2136.createbetterradars.registry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.limbo2136.createbetterradars.CreateBetterRadars;
import ru.limbo2136.createbetterradars.block.AntiAirRadarBearingBlock;

public class ModBlocks {
    // Реестр блоков нашего мода.
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(CreateBetterRadars.MOD_ID);

    // Теперь это не простой куб, а наследник обычного RadarBearingBlock.
    public static final DeferredBlock<AntiAirRadarBearingBlock> ANTI_AIR_RADAR_BEARING =
            BLOCKS.register(
                    "anti_air_radar_bearing",
                    () -> new AntiAirRadarBearingBlock(
                            BlockBehaviour.Properties.of()
                                    // Блок не является полным непрозрачным кубом.
                                    // Это важно для нормального освещения модели и вращающейся верхушки.
                                    .noOcclusion()

                                    .strength(3.0F, 6.0F)
                                    .sound(SoundType.METAL)
                                    .requiresCorrectToolForDrops()
                    )
            );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}