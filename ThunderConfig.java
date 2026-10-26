package com.thunderaura.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ThunderConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // --- Skill toggles / balance ---
    public static final ModConfigSpec.IntValue AURA_DURATION_TICKS;
    public static final ModConfigSpec.IntValue AURA_COOLDOWN_TICKS;
    public static final ModConfigSpec.DoubleValue AURA_DAMAGE;
    public static final ModConfigSpec.IntValue STRIKE_COOLDOWN_TICKS;
    public static final ModConfigSpec.DoubleValue STRIKE_DAMAGE;
    public static final ModConfigSpec.IntValue DASH_COOLDOWN_TICKS;
    public static final ModConfigSpec.DoubleValue DASH_DISTANCE;

    // Keybinds are registered in client code with defaults,
    // but players can change them in Options > Controls.
    // These config values are for skill strength only.

    static {
        BUILDER.push("skills");

        AURA_DURATION_TICKS = BUILDER
                .comment("How long Thunder Aura lasts (ticks, 20 = 1 second)")
                .defineInRange("auraDurationTicks", 200, 40, 1200);

        AURA_COOLDOWN_TICKS = BUILDER
                .comment("Cooldown after using Thunder Aura")
                .defineInRange("auraCooldownTicks", 100, 0, 600);

        AURA_DAMAGE = BUILDER
                .comment("Damage per second while Aura is active (to nearby mobs)")
                .defineInRange("auraDamage", 2.0, 0.0, 50.0);

        STRIKE_COOLDOWN_TICKS = BUILDER
                .comment("Cooldown for Lightning Strike")
                .defineInRange("strikeCooldownTicks", 40, 0, 400);

        STRIKE_DAMAGE = BUILDER
                .comment("Damage of Lightning Strike")
                .defineInRange("strikeDamage", 8.0, 1.0, 100.0);

        DASH_COOLDOWN_TICKS = BUILDER
                .comment("Cooldown for Lightning Dash")
                .defineInRange("dashCooldownTicks", 60, 0, 400);

        DASH_DISTANCE = BUILDER
                .comment("How far Lightning Dash moves the player")
                .defineInRange("dashDistance", 8.0, 2.0, 32.0);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
