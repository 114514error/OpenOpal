package wtf.opal.client.command.impl.player.movement;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import wtf.opal.client.command.Command;
import wtf.opal.utility.misc.chat.ChatUtility;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static wtf.opal.client.Constants.mc;

public final class VClipCommand extends Command {

    public VClipCommand() {
        super("vclip", "Teleports you up or down.", "v");
    }

    @Override
    protected void onCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("distance", DoubleArgumentType.doubleArg()).executes(context -> {
            final double distance = DoubleArgumentType.getDouble(context, "distance");
            mc.player.setPosition(mc.player.getX(), mc.player.getY() + distance, mc.player.getZ());
            ChatUtility.print("Clipped §l" + distance + "§7 block" + (distance == 1 ? "" : "s") + "!");
            return SINGLE_SUCCESS;
        }));
    }
}
