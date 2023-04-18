package tw.nesquate.TranX.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tw.nesquate.TranX.exception.command.NotEnoughItemException;
import tw.nesquate.TranX.exception.command.NotRunCommandInGame;
import tw.nesquate.TranX.exception.command.NullUUIDException;
import tw.nesquate.TranX.exception.general.ZeroException;
import tw.nesquate.TranX.exception.money.InsufficientBalance;
import tw.nesquate.TranX.exception.money.MinusMoneyException;
import tw.nesquate.TranX.exception.money.NullMoneyException;
import tw.nesquate.TranX.money.Money;
import tw.nesquate.TranX.money.Value;
import tw.nesquate.TranX.utils.Utils;

import java.math.BigDecimal;
import java.util.UUID;

public class MoneyCommands {
    private final Money moneyAdapter;

    public MoneyCommands(Money adapter){
        moneyAdapter = adapter;
    }

    public int getMoney(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try{
            PlayerEntity player = Utils.getPlayer(source);
            UUID uuid = player.getUuid();

            BigDecimal money = moneyAdapter.getMoney(uuid);

            source.sendMessage(Text.translatable("Money: %s", money.toPlainString()));
            return 0;
        } catch (NotRunCommandInGame e) {
            e.sendError(source);
            return -1;
        } catch (NullMoneyException e) {
            e.sendError(source);
            return -1;
        } catch (Exception e){ // Other exception
            source.sendError(Text.translatable("Unexceptional error."));
            e.printStackTrace();
            return -1;
        }
    }

    public int transferMoney(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try{
            BigDecimal transferMoney = new BigDecimal(context.getArgument("Money", Integer.class));

            PlayerEntity sourcePlayer = Utils.getPlayer(source);

            EntitySelector selector = context.getArgument("Player", EntitySelector.class);
            PlayerEntity toPlayer = selector.getPlayer(source);

            moneyAdapter.transfer(sourcePlayer, toPlayer, transferMoney);

            source.sendMessage(Text.translatable("Successful transfer %s to %s", transferMoney.toPlainString(), toPlayer.getName()));

            return 0;
        } catch (MinusMoneyException e) {
            e.sendError(source);
            return -1;
        } catch (NotRunCommandInGame e) {
            e.sendError(source);
            return -1;
        } catch (CommandSyntaxException e) {
            return -1;
        } catch (InsufficientBalance e) {
            e.sendError(source);
            return  -1;
        }
    }

    public int depositMoney(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try{
            PlayerEntity player = Utils.getPlayer(source);
            Item coin = Registries.ITEM.get(new Identifier("tranx", "coin"));
            int count = Utils.checkItem(player, coin);
            int value = count * Value.COIN;

            moneyAdapter.deposit(player, value);

            Utils.decrementItems(player, coin, count);
            source.sendMessage(Text.translatable("Successful to deposit %s", String.valueOf(value)));

            return 0;
        } catch (NotEnoughItemException e) {
            e.sendError(source);
            return -1;
        } catch (NotRunCommandInGame e) {
            e.sendError(source);
            return -1;
        } catch (NullUUIDException e) {
            e.sendError(source);
            return -1;
        } catch (NullMoneyException e) {
            e.sendError(source);
            return -1;
        }
    }

    public int withdrawMoney(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try{
            PlayerEntity player = Utils.getPlayer(source);
            Integer argument = context.getArgument("Money", Integer.class);
            Item coin = Registries.ITEM.get(new Identifier("tranx", "coin"));

            int amount = Utils.giveItem(player, coin, Value.COIN, argument);
            moneyAdapter.withdraw(player, amount * Value.COIN);

            source.sendMessage(Text.translatable("Successful to withdraw %s", amount * Value.COIN));

            return 0;
        } catch (NullUUIDException e) {
            e.sendError(source);
            return -1;
        } catch (NullMoneyException e) {
            e.sendError(source);
            return -1;
        } catch (NotRunCommandInGame e) {
            e.sendError(source);
            return -1;
        } catch (ZeroException e) {
            e.sendError(source);
            return -1;
        } catch (InsufficientBalance e){
            e.sendError(source);
            return -1;
        }
    }
}
