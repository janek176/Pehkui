package virtuoel.pehkui.capability;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import virtuoel.pehkui.Pehkui;
import virtuoel.pehkui.api.IScaleData;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.network.PehkuiNetwork;

@Mod.EventBusSubscriber(modid = Pehkui.MOD_ID)
public final class ScaleDataCapability {
    private static final ResourceLocation KEY = new ResourceLocation(Pehkui.MOD_ID, "scale_data");
    private static final Map<Entity, NBTTagCompound> LAST_SYNC =
        new WeakHashMap<Entity, NBTTagCompound>();

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

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(KEY, new Provider());
    }

    @SubscribeEvent
    public static void livingUpdate(LivingEvent.LivingUpdateEvent event) {
        Entity entity = event.getEntityLiving();
        IScaleData data = get(entity);
        if (data == null) return;
        data.tick();
        if (entity.world.isRemote) return;
        NBTTagCompound snapshot = data.serializeNBT();
        NBTTagCompound previous = LAST_SYNC.get(entity);
        if (!snapshot.equals(previous)) {
            LAST_SYNC.put(entity, snapshot.copy());
            PehkuiNetwork.syncTracking(entity);
            if (entity instanceof EntityPlayerMP) {
                PehkuiNetwork.syncTo(entity, (EntityPlayerMP) entity);
            }
        }
    }

    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking event) {
        if (!event.getEntityPlayer().world.isRemote &&
            event.getEntityPlayer() instanceof EntityPlayerMP) {
            PehkuiNetwork.syncTo(event.getTarget(), (EntityPlayerMP) event.getEntityPlayer());
        }
    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        EntityPlayer original = event.getOriginal();
        EntityPlayer replacement = event.getEntityPlayer();
        if (original.hasCapability(CAPABILITY, null) && replacement.hasCapability(CAPABILITY, null)) {
            replacement.getCapability(CAPABILITY, null).deserializeNBT(
                original.getCapability(CAPABILITY, null).serializeNBT()
            );
        }
    }

    public static IScaleData get(Entity entity) {
        return entity != null && CAPABILITY != null ? entity.getCapability(CAPABILITY, null) : null;
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
        public NBTTagCompound serializeNBT() { return instance.serializeNBT(); }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) { instance.deserializeNBT(nbt); }
    }
}
