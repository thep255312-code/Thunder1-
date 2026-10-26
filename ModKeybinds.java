package com.thunderaura.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.thunderaura.ThunderAura;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/**
 * Keybinds are fully configurable in Options > Controls.
 * Default keys are only suggestions.
 */
@EventBusSubscriber(modid = ThunderAura.MOD_ID, value = Dist.CLIENT)
public class ModKeybinds {
    public static final String CATEGORY = "key.categories.thunderaura";

    /** Hold to open the skill wheel */
    public static final KeyMapping OPEN_WHEEL = new KeyMapping(
            "key.thunderaura.open_wheel",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );

    /** Use currently selected skill */
    public static final KeyMapping USE_SKILL = new KeyMapping(
            "key.thunderaura.use_skill",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            CATEGORY
    );

    /** Quick slot 1 – Aura */
    public static final KeyMapping QUICK_AURA = new KeyMapping(
            "key.thunderaura.quick_aura",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            CATEGORY
    );

    /** Quick slot 2 – Strike */
    public static final KeyMapping QUICK_STRIKE = new KeyMapping(
            "key.thunderaura.quick_strike",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F,
            CATEGORY
    );

    /** Quick slot 3 – Dash */
    public static final KeyMapping QUICK_DASH = new KeyMapping(
            "key.thunderaura.quick_dash",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            CATEGORY
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_WHEEL);
        event.register(USE_SKILL);
        event.register(QUICK_AURA);
        event.register(QUICK_STRIKE);
        event.register(QUICK_DASH);
    }
}
