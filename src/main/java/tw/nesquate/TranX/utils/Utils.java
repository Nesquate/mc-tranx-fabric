package tw.nesquate.TranX.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import tw.nesquate.TranX.exception.command.NotEnoughItemException;
import tw.nesquate.TranX.exception.command.NotRunCommandInGame;
import tw.nesquate.TranX.exception.general.ZeroException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {
    public static PlayerEntity getPlayer(ServerCommandSource source) throws NotRunCommandInGame {
        PlayerEntity player = source.getPlayer();

        if(player == null){
            throw new NotRunCommandInGame();
        }

        return player;
    }

    public static void decrementItems(PlayerEntity player, Item item, int count){
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getItem().equals(item)) {
                if (count / 64 >= 1) {
                    itemStack.decrement(64);
                    count -= 64;
                } else {
                    itemStack.decrement(count);
                    count -= count;
                }
            }
        }
    }

    public static int giveItem(PlayerEntity player, Item item , int itemValue, Integer count) throws ZeroException {
        if(count.equals(0)){
            throw new ZeroException();
        }

        BigDecimal countB = new BigDecimal(count);
        int amount = countB.divide(new BigDecimal(itemValue), RoundingMode.DOWN).intValue();
        player.giveItemStack(new ItemStack(item, amount));

        return amount;
    }

    public static int checkItem(PlayerEntity player, Item item) throws NotEnoughItemException {
        Inventory inventory = player.getInventory();
        int count = inventory.count(item);

        if (count == 0) {
            throw new NotEnoughItemException();
        }

        return count;
    }
}
