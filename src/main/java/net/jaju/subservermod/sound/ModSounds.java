package net.jaju.subservermod.sound;

import net.jaju.subservermod.Subservermod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Subservermod.MOD_ID);

    public static final RegistryObject<SoundEvent> MINER_SOUND = SOUND_EVENTS.register("miner_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "miner_sound")));
    public static final RegistryObject<SoundEvent> WOODCUTTER_SOUND = SOUND_EVENTS.register("woodcutter_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "woodcutter_sound")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}