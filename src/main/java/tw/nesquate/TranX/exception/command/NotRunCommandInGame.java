package tw.nesquate.TranX.exception.command;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import tw.nesquate.TranX.exception.SendErrorable;

public class NotRunCommandInGame extends Exception implements SendErrorable {
    public void sendError(ServerCommandSource source){
        source.sendError(Text.translatable("Please enter server to run this command."));
    }
}
