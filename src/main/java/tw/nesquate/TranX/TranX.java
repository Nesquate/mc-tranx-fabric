package tw.nesquate.TranX;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import tw.nesquate.TranX.command.Commands;
import tw.nesquate.TranX.database.AbstractDatabase;
import tw.nesquate.TranX.database.File;
import tw.nesquate.TranX.exception.command.NullUUIDException;
import tw.nesquate.TranX.exception.general.DatabaseErrorException;
import tw.nesquate.TranX.item.Items;
import tw.nesquate.TranX.money.Money;
import tw.nesquate.TranX.utils.Utils;

import java.io.IOException;
import java.util.UUID;

public class TranX implements ModInitializer {

    @Override
    public void onInitialize() {

        Utils.LOGGER.info("TranX is starting...");
        try{
            AbstractDatabase db = new File();
            Money moneyAdapter = new Money(db);
            Commands command = new Commands(moneyAdapter);
            command.register();
            Items items = new Items();
            items.register();

            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                try{
                    UUID uuid = handler.player.getUuid();
                    moneyAdapter.newMoneyRecord(uuid);
                } catch (NullUUIDException | DatabaseErrorException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}