package tw.nesquate.TranX.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import tw.nesquate.TranX.money.Money;

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
