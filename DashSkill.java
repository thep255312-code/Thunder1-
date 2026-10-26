package com.thunderaura.skill.impl;

import com.thunderaura.capability.PlayerThunderData;
import com.thunderaura.config.ThunderConfig;
import com.thunderaura.skill.SkillType;
import com.thunderaura.skill.ThunderSkill;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class DashSkill implements ThunderSkill {
    @Override
    public SkillType getType() {
        return SkillType.DASH;
    }

    @Override
    public void execute(ServerPlayer player) {
        PlayerThunderData.get(player).ifPresent(data -> {
            if (data.isOnCooldown(SkillType.DASH)) {
                player.displayClientMessage(Component.translatable("message.thunderaura.cooldown"), true);
                return;
            }

            ServerLevel level = player.serverLevel();
            double distance = ThunderConfig.DASH_DISTANCE.get();
            Vec3 look = player.getLookAngle().normalize().scale(distance);

            // Particle trail at start
            for (int i = 0; i < 20; i++) {
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        player.getX(), player.getY() + 1, player.getZ(),
                        1, 0.3, 0.5, 0.3, 0.1);
            }

            // Teleport / boost
            Vec3 target = player.position().add(look);
            // Simple safe dash – in a full mod you would raycast for walls
            player.teleportTo(target.x, target.y, target.z);
            player.fallDistance = 0;

            // Particles at end
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    player.getX(), player.getY() + 1, player.getZ(),
                    25, 0.4, 0.6, 0.4, 0.12);

            level.playSound(null, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.4f, 1.8f);

            data.setCooldown(SkillType.DASH, getCooldownTicks());
            player.displayClientMessage(Component.translatable("message.thunderaura.dash"), true);
        });
    }

    @Override
    public int getCooldownTicks() {
        return ThunderConfig.DASH_COOLDOWN_TICKS.get();
    }
}
