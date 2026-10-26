package com.thunderaura;

import com.thunderaura.capability.PlayerThunderData;
import com.thunderaura.config.ThunderConfig;
import com.thunderaura.network.ModNetwork;
import com.thunderaura.skill.SkillRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ThunderAura.MOD_ID)
public class ThunderAura {
    public static final String MOD_ID = "thunderaura";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ThunderAura(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Thunder Aura loading...");

        // Config (skill balance – keybinds are changed in Options > Controls)
        modContainer.registerConfig(ModConfig.Type.COMMON, ThunderConfig.SPEC);

        // Register player data attachment
        com.thunderaura.capability.PlayerThunderData.ATTACHMENT_TYPES.register(modEventBus);

        // Skills
        SkillRegistry.init();

        // Network packets
        ModNetwork.register(modEventBus);

        // Tick aura / cooldowns
        NeoForge.EVENT_BUS.addListener(this::onPlayerTick);

        LOGGER.info("Thunder Aura ready.");
    }

    private void onPlayerTick(PlayerTickEvent.Post event) {
        // Tick active aura / cooldowns on server
        if (!event.getEntity().level().isClientSide()) {
            PlayerThunderData.get(event.getEntity()).ifPresent(data -> data.tick(event.getEntity()));
        }
    }
}
