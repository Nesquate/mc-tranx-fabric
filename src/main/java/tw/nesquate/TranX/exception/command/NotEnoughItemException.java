package tw.nesquate.TranX.exception.command;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import tw.nesquate.TranX.exception.SendErrorable;

public class NotEnoughItemException extends Exception implements SendErrorable {

    @Override
    public void sendError(ServerCommandSource source) {
        source.sendError(Text.translatable("Not enough item."));
    }
}
