package tw.nesquate.TranX;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.impl.util.Arguments;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.serialize.IntegerArgumentSerializer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import static net.minecraft.server.command.CommandManager.*;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class Value{
	public static int DIAMOND = 5000;
}

public class TranX implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("TranX");
	private final HashMap<String , BigDecimal> money = new HashMap<>();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			UUID uuid = handler.player.getUuid();
			this.money.put(uuid.toString(), new BigDecimal(0));
		});

		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("money")
						.executes(this::getMoney))));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
				dispatcher.register(
						literal("transfer").then(
										argument("Player", EntityArgumentType.player()).then(
												argument("Money", IntegerArgumentType.integer()).executes(this::transferMoney)
										)
								)
				)
		));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(
				literal("deposit").executes(this::depositMoney)
		)));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(
				literal("withdraw").then(
						argument("Money", IntegerArgumentType.integer()).executes(this::withdrawMoney)
				)
		)));
	}

	public int getMoney(CommandContext<ServerCommandSource> context){
		ServerCommandSource source = context.getSource();
		PlayerEntity player = source.getPlayer();
		if(player == null){
			source.sendError(Text.translatable("Please enter server to run this command."));
			return -1;
		}

		UUID uuid = player.getUuid();

		BigDecimal money = uuid != null ? this.money.get(uuid.toString()) : null;

		if (money != null) {
			source.sendMessage(Text.translatable("Money: %s", money.toPlainString()));
			return 0;
		} else {
			source.sendError(Text.translatable("Error to get information"));
			return -1;
		}
	}

	public int transferMoney(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();

		BigDecimal transferMoney = new BigDecimal(context.getArgument("Money", Integer.class));
		if(transferMoney.compareTo(new BigDecimal(0)) <= 0){
			source.sendError(Text.translatable("Money should be greater than 0."));
			return -1;
		}

		PlayerEntity sourcePlayer = source.getPlayer();
		if(sourcePlayer == null){
			source.sendError(Text.translatable("Please enter server to run this command."));
			return -1;
		}
		EntitySelector selector = context.getArgument("Player", EntitySelector.class);
		PlayerEntity toPlayer = selector.getPlayer(source);

		UUID sourceUUID = sourcePlayer.getUuid();
		BigDecimal sourceMoney = sourceUUID != null ? this.money.get(sourceUUID.toString()) : null;
		if(sourceMoney == null){
			source.sendError(Text.translatable("Error to get source money."));
			return -1;
		}
		if(sourceMoney.compareTo(transferMoney) < 0 ){
			source.sendError(Text.translatable("Insufficient balance"));
			return -1;
		}

		UUID toUUID = toPlayer != null ? toPlayer.getUuid() : null;
		BigDecimal toMoney = toUUID != null ? this.money.get(toUUID.toString()) : null;
		if(toMoney == null){
			source.sendError(Text.translatable("Error to get other money."));
			return -1;
		}

		sourceMoney = sourceMoney.subtract(transferMoney);
		toMoney = toMoney.add(transferMoney);

		this.money.put(sourceUUID.toString(), sourceMoney);
		this.money.put(toUUID.toString(), toMoney);

		source.sendMessage(Text.translatable("Successful transfer %s to %s", transferMoney.toPlainString(), toPlayer.getName()));

		return 0;
	}

	public int depositMoney(CommandContext<ServerCommandSource> context){
		ServerCommandSource source = context.getSource();
		PlayerEntity player = source.getPlayer();
		if(player == null){
			source.sendError(Text.translatable("Please enter server to run this command."));
			return -1;
		}
		Inventory inventory = player.getInventory();

		UUID uuid = player.getUuid();
		BigDecimal money = this.money.get(uuid.toString());
		if(money == null){
			source.sendError(Text.translatable("Error to get player money."));
			return -1;
		}

		Item diamond = Registries.ITEM.get(new Identifier("minecraft", "diamond"));
		int count = inventory.count(diamond);

		if(count == 0){
			source.sendError(Text.translatable("You don't have any Diamond."));
			return -1;
		}

		int value = count * Value.DIAMOND;
		money = money.add(new BigDecimal(value));

		this.money.put(uuid.toString(), money);

		for(int i = 0; i < inventory.size();++i){
			ItemStack itemStack = inventory.getStack(i);
			if(itemStack.getItem().equals(diamond)){
				if(count / 64 >= 1){
					itemStack.decrement(64);
					count -= 64;
				} else {
					itemStack.decrement(count);
					count -= count;
				}
			}
		}

		source.sendMessage(Text.translatable("Successful to deposit %s", String.valueOf(value)));

		return 0;
	}

	public int withdrawMoney(CommandContext<ServerCommandSource> context){
		ServerCommandSource source = context.getSource();
		PlayerEntity player = source.getPlayer();
		BigDecimal count = new BigDecimal(context.getArgument("Money", Integer.class));

		if(player == null){
			source.sendError(Text.translatable("Please enter server to run this command."));
			return -1;
		}

		if(count.compareTo(new BigDecimal(0)) <= 0){
			source.sendError(Text.translatable("Money should be greater than 0."));
			return -1;
		}

		UUID uuid = player.getUuid();

		BigDecimal money = this.money.get(uuid.toString());
		if(money == null){
			source.sendError(Text.translatable("Error to get player money."));
			return -1;
		}

		if(money.compareTo(count) < 0){
			source.sendError(Text.translatable("Not enough."));
			return -1;
		}

		Item diamond = Registries.ITEM.get(new Identifier("minecraft", "diamond"));
		int amount = count.divide(new BigDecimal(Value.DIAMOND), RoundingMode.DOWN).intValue();
		player.giveItemStack(new ItemStack(diamond, amount));

		money = money.subtract(new BigDecimal(amount * Value.DIAMOND));
		this.money.put(uuid.toString(), money);

		source.sendMessage(Text.translatable("Successful to withdraw %s", amount * Value.DIAMOND));

		return 0;
	}
}