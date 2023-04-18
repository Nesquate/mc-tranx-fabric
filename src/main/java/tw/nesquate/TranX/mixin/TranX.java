package tw.nesquate.TranX.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tw.nesquate.TranX.utils.Utils;

@Mixin(TitleScreen.class)
public class TranX {
	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
		Utils.LOGGER.info("This line is printed by an example mod mixin!");
	}
}
