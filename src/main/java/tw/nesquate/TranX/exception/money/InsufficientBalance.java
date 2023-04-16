package tw.nesquate.TranX.exception.money;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import tw.nesquate.TranX.exception.SendErrorable;

public class InsufficientBalance extends Exception implements SendErrorable {

    @Override
    public void sendError(ServerCommandSource source) {
        source.sendError(Text.translatable("Insufficient balance"));
    }
}
