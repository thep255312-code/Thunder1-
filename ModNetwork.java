package com.thunderaura.network;

import com.thunderaura.ThunderAura;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {
    public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(ThunderAura.MOD_ID, "main");

    public static void register(IEventBus modBus) {
        modBus.addListener(ModNetwork::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(ThunderAura.MOD_ID).versioned("1.0");

        registrar.playToServer(
                UseSkillPacket.TYPE,
                UseSkillPacket.STREAM_CODEC,
                UseSkillPacket::handle
        );

        registrar.playToServer(
                SelectSkillPacket.TYPE,
                SelectSkillPacket.STREAM_CODEC,
                SelectSkillPacket::handle
        );
    }
}
