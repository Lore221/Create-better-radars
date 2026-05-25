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

    public static final ModConfigSpec.IntValue RLS_ALLOWED_DEPTH;
    public static final ModConfigSpec.IntValue RLS_VERTICAL_SCAN_DOWN_RANGE;
    public static final ModConfigSpec.DoubleValue RLS_HORIZONTAL_RANGE_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue RLS_SCAN_PLAYERS;
    public static final ModConfigSpec.BooleanValue RLS_SCAN_HOSTILE_MOBS;
    public static final ModConfigSpec.BooleanValue RLS_SCAN_PASSIVE_MOBS;
    public static final ModConfigSpec.BooleanValue RLS_SCAN_SABLE;
    public static final ModConfigSpec.DoubleValue RLS_STRESS_IMPACT;

    // Подавляет шумные DEBUG/WARN логи Create: Radars при автонаведении CBC.
    // Механику не меняет, только уменьшает log spam.
    public static final ModConfigSpec.BooleanValue SUPPRESS_FORGOTTEN_CREATE_RADARS_DEBUG_LOGS;

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

        builder.push("rls_radar");

        RLS_ALLOWED_DEPTH = builder
                .comment("How far below surface a target can be and still be detected by RLS Radar.")
                .defineInRange("rlsAllowedDepth", 8, 0, 64);

        RLS_VERTICAL_SCAN_DOWN_RANGE = builder
                .comment("Vertical detection range below the RLS Radar.")
                .defineInRange("rlsVerticalScanDownRange", 256, 1, 384);

        RLS_HORIZONTAL_RANGE_MULTIPLIER = builder
                .comment("Horizontal range multiplier. 1.0 = normal radar range, 0.5 = half range.")
                .defineInRange("rlsHorizontalRangeMultiplier", 1.0D, 0.1D, 2.0D);

        RLS_SCAN_PLAYERS = builder
                .comment("If true, RLS Radar scans players.")
                .define("rlsScanPlayers", true);

        RLS_SCAN_HOSTILE_MOBS = builder
                .comment("If true, RLS Radar scans hostile mobs.")
                .define("rlsScanHostileMobs", true);

        RLS_SCAN_PASSIVE_MOBS = builder
                .comment("If true, RLS Radar scans passive mobs.")
                .define("rlsScanPassiveMobs", false);

        RLS_SCAN_SABLE = builder
                .comment("If true, RLS Radar scans Sable/Aeronautics structures.")
                .define("rlsScanSable", true);

        RLS_STRESS_IMPACT = builder
                .comment("Base stress impact of the RLS Radar Bearing. Final stress = this value + reflector count.")
                .defineInRange("rlsStressImpact", 4.0D, 0.0D, 256.0D);

        builder.pop();

        SUPPRESS_FORGOTTEN_CREATE_RADARS_DEBUG_LOGS = builder
                .comment("Suppress leftover Create: Radars debug logs that still reach the server console. Does not change targeting mechanics.")
                .define("suppressCreateRadarsTargetingLogs", true);

        SPEC = builder.build();
    }

    public static int effectiveAntiAirBlindZone() {
        return Math.min(ANTI_AIR_BLIND_ZONE_ABOVE.get(), VERTICAL_SCAN_UP_RANGE.get());
    }
}
