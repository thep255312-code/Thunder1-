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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class AuraSkill implements ThunderSkill {
    @Override
    public SkillType getType() {
        return SkillType.AURA;
    }

    @Override
    public void execute(ServerPlayer player) {
        PlayerThunderData.get(player).ifPresent(data -> {
            if (data.isOnCooldown(SkillType.AURA)) {
                player.displayClientMessage(Component.translatable("message.thunderaura.cooldown"), true);
                return;
            }

            int duration = ThunderConfig.AURA_DURATION_TICKS.get();
            data.activateAura(duration);
            data.setCooldown(SkillType.AURA, getCooldownTicks());

            // Visual + sound feedback
            ServerLevel level = player.serverLevel();
            level.playSound(null, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.6f, 1.4f);

            // Initial burst of particles
            for (int i = 0; i < 30; i++) {
                double ox = (level.random.nextDouble() - 0.5) * 2.5;
                double oy = level.random.nextDouble() * 2.0;
                double oz = (level.random.nextDouble() - 0.5) * 2.5;
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        player.getX() + ox, player.getY() + oy, player.getZ() + oz,
                        1, 0, 0, 0, 0.05);
            }

            player.displayClientMessage(Component.translatable("message.thunderaura.aura_activated"), true);
        });
    }

    @Override
    public int getCooldownTicks() {
        return ThunderConfig.AURA_COOLDOWN_TICKS.get();
    }

    /** Called every tick while aura is active (from PlayerThunderData). */
    public static void tickAura(ServerPlayer player, PlayerThunderData data) {
        if (!(player.level() instanceof ServerLevel level)) return;

        // Continuous particles around the player
        if (player.tickCount % 2 == 0) {
            for (int i = 0; i < 4; i++) {
                double angle = level.random.nextDouble() * Math.PI * 2;
                double radius = 0.8 + level.random.nextDouble() * 1.2;
                double ox = Math.cos(angle) * radius;
                double oz = Math.sin(angle) * radius;
                double oy = level.random.nextDouble() * 1.8;
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        player.getX() + ox, player.getY() + oy, player.getZ() + oz,
                        1, 0, 0.02, 0, 0.02);
            }
            // Occasional bigger spark
            if (level.random.nextFloat() < 0.15f) {
                level.sendParticles(ParticleTypes.END_ROD,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        2, 0.6, 0.5, 0.6, 0.01);
            }
        }

        // Damage nearby hostiles every second
        if (player.tickCount % 20 == 0) {
            double damage = ThunderConfig.AURA_DAMAGE.get();
            AABB box = player.getBoundingBox().inflate(3.5);
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive())) {
                entity.hurt(level.damageSources().lightningBolt(), (float) damage);
                // Small spark on hit
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(),
                        8, 0.3, 0.4, 0.3, 0.05);
            }
        }
    }
}
