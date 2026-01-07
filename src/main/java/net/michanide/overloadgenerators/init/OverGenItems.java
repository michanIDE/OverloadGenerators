package net.michanide.overloadgenerators.init;

import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.item.ItemCore;

public class OverGenItems {
    
    private OverGenItems() {
    }

    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(OverloadGenerators.MOD_ID);
    
    public static final ItemRegistryObject<ItemCore> CORE = ITEMS.register("core", ItemCore::new);
}
