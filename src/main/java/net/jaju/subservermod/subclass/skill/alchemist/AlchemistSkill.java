package net.jaju.subservermod.subclass.skill.alchemist;

import net.jaju.subservermod.subclass.Alchemist;
import net.jaju.subservermod.subclass.Miner;
import net.jaju.subservermod.subclass.skill.miner.MinerSkill;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AlchemistSkill {
    private Alchemist alchemist;

    public AlchemistSkill(Alchemist alchemist) {
        this.alchemist = alchemist;
    }
}
