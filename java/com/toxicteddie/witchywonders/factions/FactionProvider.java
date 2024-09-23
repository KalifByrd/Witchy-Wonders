package com.toxicteddie.witchywonders.factions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class FactionProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<IFaction> FACTION_CAP;

    static {
        FACTION_CAP = CapabilityManager.get(new CapabilityToken<>() {});
    }

    private IFaction instance = new Faction();  // Instance of your Faction implementation
    private final LazyOptional<IFaction> lazyInstance = LazyOptional.of(() -> instance);

    
    public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
        return capability == FACTION_CAP;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        return capability == FACTION_CAP ? lazyInstance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("FactionType", instance.getFaction().toString());
        // Serialize member list into a list of strings
        tag.putInt("MemberCount", instance.getMembers().size());
        int count = 0;
        for (String member : instance.getMembers()) {
            tag.putString("Member" + count++, member);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.setFaction(Faction.FactionType.valueOf(nbt.getString("FactionType")));
        int memberCount = nbt.getInt("MemberCount");
        for (int i = 0; i < memberCount; i++) {
            instance.addMember(nbt.getString("Member" + i));
        }
    }
}
