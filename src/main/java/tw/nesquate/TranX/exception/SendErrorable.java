package tw.nesquate.TranX.exception;

import net.minecraft.server.command.ServerCommandSource;

public interface SendErrorable {
    void sendError(ServerCommandSource source);
}
