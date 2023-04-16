package tw.nesquate.TranX.exception.general;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import tw.nesquate.TranX.exception.SendErrorable;

public class ZeroException extends Exception implements SendErrorable {
    @Override
    public void sendError(ServerCommandSource source) {
        source.sendError(Text.translatable("This value should not be zero."));
    }
}
