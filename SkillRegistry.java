package com.thunderaura.skill;

import com.thunderaura.skill.impl.AuraSkill;
import com.thunderaura.skill.impl.DashSkill;
import com.thunderaura.skill.impl.StrikeSkill;
import net.minecraft.server.level.ServerPlayer;

import java.util.EnumMap;
import java.util.Map;

public class SkillRegistry {
    private static final Map<SkillType, ThunderSkill> SKILLS = new EnumMap<>(SkillType.class);

    public static void init() {
        register(SkillType.AURA, new AuraSkill());
        register(SkillType.STRIKE, new StrikeSkill());
        register(SkillType.DASH, new DashSkill());
        // CHAIN can be added later
    }

    private static void register(SkillType type, ThunderSkill skill) {
        SKILLS.put(type, skill);
    }

    public static ThunderSkill get(SkillType type) {
        return SKILLS.get(type);
    }

    public static void execute(ServerPlayer player, SkillType type) {
        ThunderSkill skill = get(type);
        if (skill != null) {
            skill.execute(player);
        }
    }
}
