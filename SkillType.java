package com.thunderaura.skill;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public enum SkillType {
    AURA("aura", "skill.thunderaura.aura"),
    STRIKE("strike", "skill.thunderaura.strike"),
    DASH("dash", "skill.thunderaura.dash"),
    CHAIN("chain", "skill.thunderaura.chain");

    private final String id;
    private final String translationKey;

    SkillType(String id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("thunderaura", "textures/gui/skill_" + id + ".png");
    }

    public static SkillType fromId(String id) {
        for (SkillType type : values()) {
            if (type.id.equals(id)) return type;
        }
        return AURA;
    }
}
