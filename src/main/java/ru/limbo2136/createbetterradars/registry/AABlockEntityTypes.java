package ru.limbo2136.createbetterradars.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.limbo2136.createbetterradars.CreateBetterRadars;
import ru.limbo2136.createbetterradars.block.entity.AntiAirRadarBearingBlockEntity;

public class AABlockEntityTypes {
    // Реестр BlockEntityType нашего мода.
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateBetterRadars.MOD_ID);

    // Тип BlockEntity для ПВО-вращателя.
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AntiAirRadarBearingBlockEntity>> ANTI_AIR_RADAR_BEARING =
            BLOCK_ENTITY_TYPES.register("anti_air_radar_bearing", () ->
                    BlockEntityType.Builder.of(
                            // Создаём нашу BlockEntity.
                            (pos, state) -> new AntiAirRadarBearingBlockEntity(
                                    AABlockEntityTypes.ANTI_AIR_RADAR_BEARING.get(),
                                    pos,
                                    state
                            ),

                            // Указываем, к какому блоку эта BlockEntity относится.
                            ModBlocks.ANTI_AIR_RADAR_BEARING.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}