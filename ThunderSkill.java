package com.thunderaura.skill;

import net.minecraft.server.level.ServerPlayer;

public interface ThunderSkill {
    SkillType getType();

    /** Called on server when the skill is activated. */
    void execute(ServerPlayer player);

    /** Cooldown in ticks (can read from config). */
    int getCooldownTicks();
}
