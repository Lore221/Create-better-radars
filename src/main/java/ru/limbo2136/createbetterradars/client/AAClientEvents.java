package ru.limbo2136.createbetterradars.client;

import com.simibubi.create.content.contraptions.bearing.BearingRenderer;
import com.simibubi.create.content.contraptions.bearing.BearingVisual;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import ru.limbo2136.createbetterradars.CreateBetterRadars;
import ru.limbo2136.createbetterradars.registry.AABlockEntityTypes;

@SuppressWarnings("unused")
@EventBusSubscriber(
        modid = CreateBetterRadars.MOD_ID,
        value = Dist.CLIENT
)
public class AAClientEvents {
    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Обычный renderer на случай, если Flywheel отключён или не отрисовал visual.
        event.registerBlockEntityRenderer(
                AABlockEntityTypes.ANTI_AIR_RADAR_BEARING.get(),
                BearingRenderer::new
        );
        event.registerBlockEntityRenderer(
                AABlockEntityTypes.RLS_RADAR_BEARING.get(),
                BearingRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerFlywheelVisuals(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Flywheel Visual. Именно он рисует вращающуюся верхушку bearing.
            SimpleBlockEntityVisualizer
                    .builder(AABlockEntityTypes.ANTI_AIR_RADAR_BEARING.get())
                    .factory(BearingVisual::new)
                    .apply();
            SimpleBlockEntityVisualizer
                    .builder(AABlockEntityTypes.RLS_RADAR_BEARING.get())
                    .factory(BearingVisual::new)
                    .apply();

            CreateBetterRadars.LOGGER.info("Registered Flywheel visuals for specialized radar bearings");
        });
    }
}
