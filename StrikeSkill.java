package com.thunderaura.skill.impl;

import com.thunderaura.capability.PlayerThunderData;
import com.thunderaura.config.ThunderConfig;
import com.thunderaura.skill.SkillType;
import com.thunderaura.skill.ThunderSkill;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class StrikeSkill implements ThunderSkill {
    @Override
    public SkillType getType() {
        return SkillType.STRIKE;
    }

    @Override
    public void execute(ServerPlayer player) {
        PlayerThunderData.get(player).ifPresent(data -> {
            if (data.isOnCooldown(SkillType.STRIKE)) {
                player.displayClientMessage(Component.translatable("message.thunderaura.cooldown"), true);
                return;
            }

            ServerLevel level = player.serverLevel();
            Vec3 eye = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            Vec3 end = eye.add(look.scale(32));

            // Raycast to find target position
            BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            Vec3 targetPos = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();

            // Prefer living entity near the hit point
            LivingEntity targetEntity = null;
            AABB search = new AABB(targetPos, targetPos).inflate(2.5);
            double bestDist = Double.MAX_VALUE;
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, search, ent -> ent != player && ent.isAlive())) {
                double d = e.distanceToSqr(targetPos);
                if (d < bestDist) {
                    bestDist = d;
                    targetEntity = e;
                }
            }

            BlockPos strikePos;
            if (targetEntity != null) {
                strikePos = targetEntity.blockPosition();
                targetEntity.hurt(level.damageSources().lightningBolt(), ThunderConfig.STRIKE_DAMAGE.get().floatValue());
            } else {
                strikePos = BlockPos.containing(targetPos);
            }

            // Spawn visual lightning bolt (does not set fire by default in this usage)
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
            if (bolt != null) {
                bolt.moveTo(Vec3.atBottomCenterOf(strikePos));
                bolt.setVisualOnly(true); // no real damage / fire from the bolt itself
                level.addFreshEntity(bolt);
            }

            // Extra particles
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    strikePos.getX() + 0.5, strikePos.getY() + 1, strikePos.getZ() + 0.5,
                    40, 0.5, 1.0, 0.5, 0.15);

            level.playSound(null, strikePos, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1.0f, 1.0f);

            data.setCooldown(SkillType.STRIKE, getCooldownTicks());
            player.displayClientMessage(Component.translatable("message.thunderaura.strike"), true);
        });
    }

    @Override
    public int getCooldownTicks() {
        return ThunderConfig.STRIKE_COOLDOWN_TICKS.get();
    }
}
