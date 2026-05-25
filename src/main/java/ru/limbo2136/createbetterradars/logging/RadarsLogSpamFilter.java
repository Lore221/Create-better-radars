package ru.limbo2136.createbetterradars.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import ru.limbo2136.createbetterradars.CreateBetterRadars;
import ru.limbo2136.createbetterradars.config.AAConfig;

public class RadarsLogSpamFilter extends AbstractFilter {
    private static boolean installed = false;

    public static void install() {
        if (installed) {
            return;
        }

        installed = true;

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();

        RadarsLogSpamFilter filter = new RadarsLogSpamFilter();

        // Root logger.
        configuration.getRootLogger().addFilter(filter);

        // Existing logger configs.
        // This helps catch Create: Radars loggers even if they have their own config.
        for (LoggerConfig loggerConfig : configuration.getLoggers().values()) {
            loggerConfig.addFilter(filter);
        }

        context.updateLoggers();

        CreateBetterRadars.LOGGER.info("Installed residual Create: Radars debug log filter");
    }

    @Override
    public Filter.Result filter(LogEvent event) {
        if (shouldSuppress(event)) {
            return Filter.Result.DENY;
        }

        return Filter.Result.NEUTRAL;
    }

    private static boolean shouldSuppress(LogEvent event) {
        String loggerName = event.getLoggerName();

        // First filter by logger name.
        // Do not touch NeoForge/Netty/Minecraft logs during early loading.
        // Reading mod config too early can crash the game.
        if (loggerName == null || !loggerName.startsWith("com.happysg.radar")) {
            return false;
        }

        if (!isSuppressEnabledSafely()) {
            return false;
        }

        if (event.getMessage() == null) {
            return false;
        }

        // Use getFormat(), not getFormattedMessage().
        // getFormattedMessage() may trigger parameter formatting.
        String message = event.getMessage().getFormat();

        if (message == null) {
            return false;
        }

        return isForgottenCreateRadarsDebugMessage(message);
    }

    private static boolean isSuppressEnabledSafely() {
        try {
            return AAConfig.SUPPRESS_FORGOTTEN_CREATE_RADARS_DEBUG_LOGS.get();
        } catch (IllegalStateException | NullPointerException ignored) {
            // This only affects known Create: Radars debug messages and does not change mechanics.
            return true;
        }
    }

    private static boolean isForgottenCreateRadarsDebugMessage(String message) {
        if (message.contains("PITCH.rotateCBC")) {
            return true;
        }

        if (message.contains("PITCH setAndAcquireTrack")) {
            return true;
        }

        if (message.contains("RANGE DBG endpoint")) {
            return true;
        }

        if (message.contains("WFC AIMCHK")) {
            return true;
        }

        return false;
    }
}
