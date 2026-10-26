package com.thunderaura.capability;

import com.thunderaura.ThunderAura;
import com.thunderaura.skill.SkillType;
import com.thunderaura.skill.impl.AuraSkill;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@EventBusSubscriber(modid = ThunderAura.MOD_ID)
public class PlayerThunderData implements INBTSerializable<CompoundTag> {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ThunderAura.MOD_ID);

    public static final Supplier<AttachmentType<PlayerThunderData>> THUNDER_DATA =
            ATTACHMENT_TYPES.register("thunder_data",
                    () -> AttachmentType.serializable(PlayerThunderData::new)
                            .copyOnDeath()
                            .build());

    private int auraTicksRemaining = 0;
    private SkillType selectedSkill = SkillType.AURA;
    private final Map<SkillType, Integer> cooldowns = new EnumMap<>(SkillType.class);

    public PlayerThunderData() {
        for (SkillType t : SkillType.values()) {
            cooldowns.put(t, 0);
        }
    }

    public static Optional<PlayerThunderData> get(Player player) {
        return Optional.of(player.getData(THUNDER_DATA.get()));
    }

    public void tick(Player player) {
        for (SkillType t : SkillType.values()) {
            int cd = cooldowns.getOrDefault(t, 0);
            if (cd > 0) cooldowns.put(t, cd - 1);
        }

        if (auraTicksRemaining > 0) {
            auraTicksRemaining--;
            if (player instanceof ServerPlayer sp) {
                AuraSkill.tickAura(sp, this);
            }
            if (auraTicksRemaining <= 0 && player instanceof ServerPlayer sp) {
                sp.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable("message.thunderaura.aura_ended"),
                        true
                );
            }
        }
    }

    public void activateAura(int ticks) {
        this.auraTicksRemaining = ticks;
    }

    public boolean isAuraActive() {
        return auraTicksRemaining > 0;
    }

    public int getAuraTicksRemaining() {
        return auraTicksRemaining;
    }

    public void setCooldown(SkillType type, int ticks) {
        cooldowns.put(type, ticks);
    }

    public boolean isOnCooldown(SkillType type) {
        return cooldowns.getOrDefault(type, 0) > 0;
    }

    public int getCooldown(SkillType type) {
        return cooldowns.getOrDefault(type, 0);
    }

    public SkillType getSelectedSkill() {
        return selectedSkill;
    }

    public void setSelectedSkill(SkillType selectedSkill) {
        this.selectedSkill = selectedSkill;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("auraTicks", auraTicksRemaining);
        tag.putString("selected", selectedSkill.getId());
        CompoundTag cds = new CompoundTag();
        for (Map.Entry<SkillType, Integer> e : cooldowns.entrySet()) {
            cds.putInt(e.getKey().getId(), e.getValue());
        }
        tag.put("cooldowns", cds);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        auraTicksRemaining = tag.getInt("auraTicks");
        selectedSkill = SkillType.fromId(tag.getString("selected"));
        CompoundTag cds = tag.getCompound("cooldowns");
        for (SkillType t : SkillType.values()) {
            if (cds.contains(t.getId())) {
                cooldowns.put(t, cds.getInt(t.getId()));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            PlayerThunderData oldData = event.getOriginal().getData(THUNDER_DATA.get());
            PlayerThunderData newData = event.getEntity().getData(THUNDER_DATA.get());
            newData.selectedSkill = oldData.selectedSkill;
        }
    }
}
