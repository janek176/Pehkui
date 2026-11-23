package virtuoel.pehkui.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import virtuoel.pehkui.api.IScaleData;
import virtuoel.pehkui.api.ScaleData;

public final class ScaleDataCapability {
    @CapabilityInject(IScaleData.class)
    public static Capability<IScaleData> CAPABILITY = null;

    private ScaleDataCapability() {}

    public static void register() {
        CapabilityManager.INSTANCE.register(IScaleData.class, new Capability.IStorage<IScaleData>() {
            @Override
            public net.minecraft.nbt.NBTBase writeNBT(Capability<IScaleData> capability, IScaleData instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IScaleData> capability, IScaleData instance, EnumFacing side, net.minecraft.nbt.NBTBase nbt) {
                if (nbt instanceof NBTTagCompound) instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, ScaleData::new);
    }

    public static final class Provider implements ICapabilitySerializable<NBTTagCompound> {
        private final IScaleData instance = new ScaleData();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            return capability == CAPABILITY ? CAPABILITY.cast(instance) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return instance.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            instance.deserializeNBT(nbt);
        }
    }
}
