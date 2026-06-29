package wtf.opal.mixin;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.opal.client.OpalClient;

import java.util.List;

@Mixin(PlayerListHud.class)
public final class PlayerListHudMixin {

    @Unique
    private static Text GRAY_OPENING_PARENTHESIS;

    @Unique
    private static Text GRAY_CLOSING_PARENTHESIS;

    @Unique
    private static Text EMPTY_TEXT;

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;")
    )
    private Text redirectPlayerName(PlayerListHud instance, PlayerListEntry entry) {
        final Text playerNameText = instance.getPlayerName(entry);

        final String user = OpalClient.getInstance().getUser();
        if (user == null) {
            return playerNameText;
        }

        if (GRAY_OPENING_PARENTHESIS == null) {
            GRAY_OPENING_PARENTHESIS = Text.literal(" " + Formatting.GRAY + "(");
            GRAY_CLOSING_PARENTHESIS = Text.literal(Formatting.GRAY + ")");
            EMPTY_TEXT = Text.empty();
        }

        return Texts.join(
                List.of(
                        playerNameText,
                        GRAY_OPENING_PARENTHESIS,
//                        MutableText.of(PlainTextContent.of(user.getName())).withColor(user.getRole().getArgb()),
                        MutableText.of(PlainTextContent.of(user)).withColor(-1),
                        GRAY_CLOSING_PARENTHESIS
                ),
                EMPTY_TEXT
        );
    }

}
