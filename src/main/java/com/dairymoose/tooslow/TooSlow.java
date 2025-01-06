package com.dairymoose.tooslow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("tooslow")
public class TooSlow
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public TooSlow() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

}
