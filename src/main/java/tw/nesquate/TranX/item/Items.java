package tw.nesquate.TranX.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Items {

    public static final CoinItem COIN = new CoinItem(new FabricItemSettings(), null);
    public void register(){
        Registry.register(Registries.ITEM, new Identifier("tranx", "coin"), COIN);

    }
}
