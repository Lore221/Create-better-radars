package ru.limbo2136.createbetterradars.mixin;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Этот mixin чинит log spam в Create: Radars + Create Big Cannons.
//
// В Create: Radars есть debug-лог в CannonUtil.getInitialVelocity(),
// где в строке 6 плейсхолдеров {}, но передаётся только 4 аргумента.
// Из-за этого Log4j печатает огромный stacktrace при автонаведении.
//
// Мы не меняем механику наведения.
// Мы только подавляем конкретный сломанный debug-лог.
@Mixin(targets = "com.happysg.radar.compat.cbc.CannonUtil", remap = false)
public abstract class CannonUtilLogSpamMixin {
    @Redirect(
            method = "getInitialVelocity",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;[Ljava/lang/Object;)V"
            ),
            require = 0
    )
    private static void create_better_radars$suppressBrokenGetInitialVelocityDebug(
            Logger logger,
            String message,
            Object[] arguments
    ) {
        // Подавляем только конкретную сломанную строку.
        if (message != null && message.contains("getInitialVelocity for contraption")) {
            return;
        }

        // Остальные debug-логи оставляем как были.
        logger.debug(message, arguments);
    }
}