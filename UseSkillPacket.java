package com.thunderaura.network;

import com.thunderaura.ThunderAura;
import com.thunderaura.capability.PlayerThunderData;
import com.thunderaura.skill.SkillRegistry;
import com.thunderaura.skill.SkillType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UseSkillPacket(String skillId) implements CustomPacketPayload {
    public static final Type<UseSkillPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ThunderAura.MOD_ID, "use_skill"));

    public static final StreamCodec<FriendlyByteBuf, UseSkillPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeUtf(pkt.skillId == null ? "" : pkt.skillId),
                    buf -> {
                        String id = buf.readUtf();
                        return new UseSkillPacket(id.isEmpty() ? null : id);
                    }
            );

    public UseSkillPacket(SkillType type) {
        this(type == null ? null : type.getId());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UseSkillPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                SkillType toUse;
                if (packet.skillId == null || packet.skillId.isEmpty()) {
                    toUse = PlayerThunderData.get(player)
                            .map(PlayerThunderData::getSelectedSkill)
                            .orElse(SkillType.AURA);
                } else {
                    toUse = SkillType.fromId(packet.skillId);
                }
                SkillRegistry.execute(player, toUse);
            }
        });
    }
}
