package ru.limbo2136.createbetterradars.registry;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import ru.limbo2136.createbetterradars.CreateBetterRadars;

public class AACreativeTabEvents {
    public static void addItems(BuildCreativeModeTabContentsEvent event) {
        // ID текущей креативной вкладки.
        ResourceLocation tabId = event.getTabKey().location();

        // Create: Radars использует namespace create_radar.
        // Так мы добавляем предмет именно во вкладку мода Radars.
        if (!tabId.getNamespace().equals("create_radar")) {
            return;
        }

        CreateBetterRadars.LOGGER.info("Adding Anti-Air Radar Bearing to creative tab: {}", tabId);

        // Добавляем наш блок-предмет во вкладку.
        event.accept(ModItems.ANTI_AIR_RADAR_BEARING.get());
    }
}