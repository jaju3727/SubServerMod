package net.jaju.subservermod.subclass.skill.miner;

import net.jaju.subservermod.subclass.Miner;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class MinerSkill {

    private static Miner miner;

    public MinerSkill(Miner miner) {
        MinerSkill.miner = miner;
    }
}
