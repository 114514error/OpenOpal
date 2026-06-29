package wtf.opal.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
//import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.opal.client.OpalClient;
import wtf.opal.client.feature.module.impl.visual.CapeModule;

@Mixin(PlayerListEntry.class)
public final class PlayerListEntryMixin {

    @Unique
    private static Identifier ELYTRA_TEXTURE;

    @Final
    @Shadow
    private GameProfile profile;

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void hookSkinTextures(final CallbackInfoReturnable<SkinTextures> cir) {

        //10j3k fix capes

//        final User user = ClientSocket.getInstance().getUserOrNull(profile.id());
//        if (user == null) {
//            return;
//        }
//
//        final CapeModule.CapeType capeType;
//        if (user instanceof ResolvedUser resolvedUser) {
//            capeType = resolvedUser.getCapeType();
//        } else {
//            final CapeModule capeModule = OpalClient.getInstance().getModuleRepository().getModule(CapeModule.class);
//            capeType = capeModule.isEnabled() ? capeModule.getType() : null;
//        }
//
//        if (capeType == null) {
//            return;
//        }
//
//        if (ELYTRA_TEXTURE == null) {
//            ELYTRA_TEXTURE = Identifier.of("textures/entity/elytra.png");
//        }
//
//        final SkinTextures oldTextures = cir.getReturnValue();
//
//        cir.setReturnValue(
//                new SkinTextures(
//                        oldTextures.body(),
//                        oldTextures.cape(),
//                        capeType.getIdentifier(),
//                        ELYTRA_TEXTURE,
//                        oldTextures.model(),
//                        oldTextures.secure()
//                )
//        );
    }

}