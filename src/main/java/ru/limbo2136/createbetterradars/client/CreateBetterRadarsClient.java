package ru.limbo2136.createbetterradars.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import ru.limbo2136.createbetterradars.CreateBetterRadars;

@Mod(value = CreateBetterRadars.MOD_ID, dist = Dist.CLIENT)
public class CreateBetterRadarsClient {
    public CreateBetterRadarsClient(ModContainer modContainer) {
        // Подключаем стандартный экран конфигурации NeoForge.
        // После этого кнопка Mods -> Create: Better Radars -> Config/Settings
        // должна открыть настройки нашего мода.
        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                ConfigurationScreen::new
        );
    }
}