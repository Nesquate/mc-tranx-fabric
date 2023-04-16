package tw.nesquate.TranX.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
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
import tw.nesquate.TranX.exception.money.MinusMoneyException;
import tw.nesquate.TranX.exception.money.NullMoneyException;
import tw.nesquate.TranX.money.Money;
import tw.nesquate.TranX.money.Value;
import tw.nesquate.TranX.utils.Utils;

import java.math.BigDecimal;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {

    private final MoneyCommands moneyCommands;
    public Commands(Money moneyAdapter) {
        moneyCommands = new MoneyCommands(moneyAdapter);
    }

    public void register() {
        registryMoneyCommand();
    }

    private void registryMoneyCommand(){
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("money")
                        .executes(moneyCommands::getMoney))));
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
                dispatcher.register(
                        literal("transfer").then(
                                argument("Player", EntityArgumentType.player()).then(
                                        argument("Money", IntegerArgumentType.integer()).executes(moneyCommands::transferMoney)
                                )
                        )
                )
        ));
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("deposit").executes(moneyCommands::depositMoney)
        )));
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("withdraw").then(
                        argument("Money", IntegerArgumentType.integer()).executes(moneyCommands::withdrawMoney)
                )
        )));
    }


}
