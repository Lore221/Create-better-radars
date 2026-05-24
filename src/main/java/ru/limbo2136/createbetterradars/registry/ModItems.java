package ru.limbo2136.createbetterradars.registry;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.limbo2136.createbetterradars.CreateBetterRadars;

public class ModItems {
    // Реестр предметов нашего мода.
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CreateBetterRadars.MOD_ID);

    // Предметная версия блока.
    public static final DeferredItem<BlockItem> ANTI_AIR_RADAR_BEARING =
            ITEMS.registerSimpleBlockItem(ModBlocks.ANTI_AIR_RADAR_BEARING);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}