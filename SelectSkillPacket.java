package com.thunderaura.network;

import com.thunderaura.ThunderAura;
import com.thunderaura.capability.PlayerThunderData;
import com.thunderaura.skill.SkillType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SelectSkillPacket(SkillType skill) implements CustomPacketPayload {
    public static final Type<SelectSkillPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ThunderAura.MOD_ID, "select_skill"));

    public static final StreamCodec<FriendlyByteBuf, SelectSkillPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeUtf(pkt.skill.getId()),
                    buf -> new SelectSkillPacket(SkillType.fromId(buf.readUtf()))
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SelectSkillPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PlayerThunderData.get(player).ifPresent(data -> {
                    data.setSelectedSkill(packet.skill);
                    player.displayClientMessage(
                            Component.translatable("message.thunderaura.selected", packet.skill.getDisplayName()),
                            true
                    );
                });
            }
        });
    }
}
