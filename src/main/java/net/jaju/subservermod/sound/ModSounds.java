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
    public static final RegistryObject<SoundEvent> FRYPAN_SOUND = SOUND_EVENTS.register("frypan_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "frypan_sound")));
    public static final RegistryObject<SoundEvent> OVENBAKE_SOUND = SOUND_EVENTS.register("ovenbake_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "ovenbake_sound")));
    public static final RegistryObject<SoundEvent> OVENFINISH_SOUND = SOUND_EVENTS.register("ovenfinish_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "ovenfinish_sound")));
    public static final RegistryObject<SoundEvent> FISHING_SOUND = SOUND_EVENTS.register("fishing_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "fishing_sound")));
    public static final RegistryObject<SoundEvent> BUYING_SOUND = SOUND_EVENTS.register("buying_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "buying_sound")));
    public static final RegistryObject<SoundEvent> RAWFISH_SOUND = SOUND_EVENTS.register("rawfish_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "rawfish_sound")));
    public static final RegistryObject<SoundEvent> DRINK_POTION_SOUND = SOUND_EVENTS.register("drink_potion_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "drink_potion_sound")));
    public static final RegistryObject<SoundEvent> LOUDSPEAKER_SOUND = SOUND_EVENTS.register("loudspeaker_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "loudspeaker_sound")));
    public static final RegistryObject<SoundEvent> GOHOME_SOUND = SOUND_EVENTS.register("gohome_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "gohome_sound")));
    public static final RegistryObject<SoundEvent> RANDOMBOX_SOUND = SOUND_EVENTS.register("randombox_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "randombox_sound")));
    public static final RegistryObject<SoundEvent> HOWSWEET_SOUND = SOUND_EVENTS.register("howsweet_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Subservermod.MOD_ID, "howsweet_sound")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}