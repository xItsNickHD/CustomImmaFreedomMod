package me.stephenbullough.immafreedommod;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class immafreedommod extends JavaPlugin
{
    boolean thanksEnabled = true;
    String thanks = "Thank you for using ImmaFreedom!";
    String enable = "ImmaFreedomMod has been successfully disabled!";
    String disable = "ImmaFreedomMod has been successfully disabled!";
    
    public immafreedommod plugin;
    public final Logger logger = Logger.getLogger("Minecraft");
    
    public void onEnable()
    {
        logger.info(enable);
    }
    
    public void onDisable()
    {
        if (thanksEnabled == true)
        {
        logger.info(thanks);    
        }
        logger.info(disable);
    }
}
