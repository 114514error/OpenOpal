package wtf.opal.mixin;

import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import wtf.opal.client.OpalClient;
import wtf.opal.client.feature.module.impl.visual.StreamerModeModule;
import wtf.opal.client.feature.module.repository.ModuleRepository;

@Mixin(TextVisitFactory.class)
public final class TextVisitFactoryMixin {

    @ModifyVariable(
            method = "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private static String modifyVisitFormatted(final String text) {
        final OpalClient opal = OpalClient.getInstance();
        if (opal == null) {
            return text;
        }

        final ModuleRepository moduleRepository = opal.getModuleRepository();
        if (moduleRepository == null) {
            return text;
        }

        final StreamerModeModule streamerModeModule = moduleRepository.getModule(StreamerModeModule.class);
        if (streamerModeModule.isEnabled()) {
            return streamerModeModule.filter(text);
        }

        return text;
    }

}
