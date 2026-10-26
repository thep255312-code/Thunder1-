package com.thunderaura.client;

import com.thunderaura.ThunderAura;
import com.thunderaura.network.SelectSkillPacket;
import com.thunderaura.network.UseSkillPacket;
import com.thunderaura.skill.SkillType;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ThunderAura.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    private static boolean wheelWasDown = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        // Open skill wheel while holding the key
        if (ModKeybinds.OPEN_WHEEL.isDown()) {
            if (!(mc.screen instanceof SkillWheelScreen)) {
                mc.setScreen(new SkillWheelScreen());
            }
            wheelWasDown = true;
        } else if (wheelWasDown) {
            // Released – close wheel if still open
            if (mc.screen instanceof SkillWheelScreen) {
                mc.setScreen(null);
            }
            wheelWasDown = false;
        }

        // Use selected skill
        while (ModKeybinds.USE_SKILL.consumeClick()) {
            PacketDistributor.sendToServer(new UseSkillPacket((String) null)); // server uses selected
        }

        // Quick skills
        while (ModKeybinds.QUICK_AURA.consumeClick()) {
            PacketDistributor.sendToServer(new SelectSkillPacket(SkillType.AURA));
            PacketDistributor.sendToServer(new UseSkillPacket(SkillType.AURA));
        }
        while (ModKeybinds.QUICK_STRIKE.consumeClick()) {
            PacketDistributor.sendToServer(new SelectSkillPacket(SkillType.STRIKE));
            PacketDistributor.sendToServer(new UseSkillPacket(SkillType.STRIKE));
        }
        while (ModKeybinds.QUICK_DASH.consumeClick()) {
            PacketDistributor.sendToServer(new SelectSkillPacket(SkillType.DASH));
            PacketDistributor.sendToServer(new UseSkillPacket(SkillType.DASH));
        }
    }
}
