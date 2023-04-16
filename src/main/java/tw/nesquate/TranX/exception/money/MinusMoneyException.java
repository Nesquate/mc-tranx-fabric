package tw.nesquate.TranX.exception.money;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import tw.nesquate.TranX.exception.SendErrorable;

public class MinusMoneyException extends Exception implements SendErrorable {
    @Override
    public void sendError(ServerCommandSource source) {
        source.sendError(Text.translatable("Money should be greater than 0."));
    }
}
