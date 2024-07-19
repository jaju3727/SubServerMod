package net.jaju.subservermod.subclass.skill.miner;

import net.jaju.subservermod.subclass.Miner;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class MinerSkill {

    private Miner miner;

    public MinerSkill(Miner miner) {
        this.miner = miner;
    }
}
