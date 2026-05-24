package ru.limbo2136.createbetterradars.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AAConfig {
    // Общий объект конфига. Его регистрирует главный класс мода.
    public static final ModConfigSpec SPEC;

    // ===== ПВО-РАДАР =====

    // На сколько блоков вверх ПВО-радар может сканировать от своей высоты.
    public static final ModConfigSpec.IntValue VERTICAL_SCAN_UP_RANGE;

    // Слепая зона над ПВО-радаром.
    // Например, 32 значит: цели ниже radarY + 32 игнорируются.
    public static final ModConfigSpec.IntValue ANTI_AIR_BLIND_ZONE_ABOVE;

    // Множитель горизонтальной дальности.
    // 1.0 = как обычный радар.
    // 0.75 = 75% от обычной дальности.
    public static final ModConfigSpec.DoubleValue HORIZONTAL_RANGE_MULTIPLIER;

    // Должен ли ПВО-радар видеть Sable/Aeronautics-структуры.
    public static final ModConfigSpec.BooleanValue ANTI_AIR_SCAN_SABLE;

    // Должен ли ПВО-радар видеть снаряды / Projectile.
    public static final ModConfigSpec.BooleanValue ANTI_AIR_SCAN_PROJECTILES;

    // Базовая нагрузка ПВО-радара.
    // Итоговая нагрузка будет: antiAirStressImpact + количество рефлекторов.
    public static final ModConfigSpec.DoubleValue ANTI_AIR_STRESS_IMPACT;

    // Подавляет шумные DEBUG/WARN логи Create: Radars при автонаведении CBC.
    // Механику не меняет, только уменьшает log spam.
    public static final ModConfigSpec.BooleanValue SUPPRESS_CREATE_RADARS_TARGETING_LOGS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("anti_air_radar");

        VERTICAL_SCAN_UP_RANGE = builder
                .comment("How many blocks upward the anti-air radar scans from its own Y level.")
                .defineInRange("verticalScanUpRange", 256, 1, 384);

        ANTI_AIR_BLIND_ZONE_ABOVE = builder
                .comment("Blind zone above the anti-air radar. Targets below this vertical offset are ignored.")
                .defineInRange("blindZoneAbove", 32, 0, 128);

        HORIZONTAL_RANGE_MULTIPLIER = builder
                .comment("Horizontal range multiplier. 1.0 = normal radar range, 0.5 = half range.")
                .defineInRange("horizontalRangeMultiplier", 1.0D, 0.1D, 2.0D);

        ANTI_AIR_SCAN_SABLE = builder
                .comment("If true, anti-air radar scans Sable/Aeronautics structures.")
                .define("scanSable", true);

        ANTI_AIR_SCAN_PROJECTILES = builder
                .comment("If true, anti-air radar scans projectiles, including compatible cannon shells if they are Projectile entities.")
                .define("scanProjectiles", true);

        ANTI_AIR_STRESS_IMPACT = builder
                .comment("Base stress impact of the Anti-Air Radar Bearing. Final stress = this value + reflector count.")
                .defineInRange("antiAirStressImpact", 4.0D, 0.0D, 256.0D);

        builder.pop();

        SUPPRESS_CREATE_RADARS_TARGETING_LOGS = builder
                .comment("Suppress noisy Create: Radars CBC targeting logs. Does not change targeting mechanics.")
                .define("suppressCreateRadarsTargetingLogs", true);

        SPEC = builder.build();
    }
}