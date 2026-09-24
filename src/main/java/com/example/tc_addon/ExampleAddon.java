package com.example.tc_addon;

/*? if forge {*/
/*import net.minecraftforge.fml.common.Mod;
*//*?}*/
/*? if neoforge {*/
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
/*?}*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Mod entry point. All Talking Colonists wiring lives in {@link TalkingColonistsIntegration}. */
@Mod(ExampleAddon.MOD_ID)
public class ExampleAddon {
    public static final String MOD_ID = /*$ mod_id*/ "tc_addon_example";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /*? if forge {*/
    /*public ExampleAddon() {
        TalkingColonistsIntegration.register();
    }
    *//*?}*/

    /*? if neoforge {*/
    public ExampleAddon(IEventBus modEventBus, ModContainer modContainer) {
        TalkingColonistsIntegration.register();
    }
    /*?}*/
}
