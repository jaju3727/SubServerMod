package net.jaju.subservermod.subclass.skill.woodcutter;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.subclass.Woodcutter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, value = Dist.CLIENT)
public class WoodcutterSkill {
    private transient Woodcutter woodcutter;

    public WoodcutterSkill(Woodcutter woodcutter) {
        this.woodcutter = woodcutter;
        MinecraftForge.EVENT_BUS.register(this);
    }
}
