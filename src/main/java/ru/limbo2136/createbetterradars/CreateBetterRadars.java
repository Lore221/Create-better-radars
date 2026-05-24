package ru.limbo2136.createbetterradars;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import ru.limbo2136.createbetterradars.config.AAConfig;
import ru.limbo2136.createbetterradars.registry.AABlockEntityTypes;
import ru.limbo2136.createbetterradars.registry.AACreativeTabEvents;
import ru.limbo2136.createbetterradars.registry.ModBlocks;
import ru.limbo2136.createbetterradars.registry.ModItems;
import ru.limbo2136.createbetterradars.logging.RadarsLogSpamFilter;

@Mod(CreateBetterRadars.MOD_ID)
public class CreateBetterRadars {
    public static final String MOD_ID = "create_better_radars";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateBetterRadars(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(
                ModConfig.Type.SERVER,
                AAConfig.SPEC,
                "create_better_radars-server.toml"
        );

        RadarsLogSpamFilter.install();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        AABlockEntityTypes.register(modEventBus);

        modEventBus.addListener(AACreativeTabEvents::addItems);

        LOGGER.info("Create: Better Radars loaded");
    }
}