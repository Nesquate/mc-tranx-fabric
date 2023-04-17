package tw.nesquate.TranX;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tw.nesquate.TranX.command.Commands;
import tw.nesquate.TranX.exception.command.NullUUIDException;
import tw.nesquate.TranX.item.Items;
import tw.nesquate.TranX.money.Money;

import java.util.UUID;

public class TranX implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("TranX");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");
        Money moneyAdapter = new Money();
        Commands command = new Commands(moneyAdapter);
        command.register();
        Items items = new Items();
        items.register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            try{
                UUID uuid = handler.player.getUuid();
                moneyAdapter.newMoneyRecord(uuid);
            } catch (NullUUIDException e) {
                throw new RuntimeException(e);
            }
        });
    }
}