package me.stephenbullough.immafreedommod;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class immafreedommod extends JavaPlugin
{
    public immafreedommod plugin;
    public final Logger logger = Logger.getLogger("Minecraft");
    
    public void onEnable()
    {
        logger.info("ImmaFreedomMod has been successfully enabled!");
    }
    
    public void onDisable()
    {
        logger.info("ImmaFreedomMod has been successfully disabled!");
    }
}
