package wtf.opal.client.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Util;
import wtf.opal.client.command.Command;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public final class DashboardCommand extends Command {

    public DashboardCommand() {
        super("dashboard", "Opens the Opal dashboard.", "dash");
    }

    @Override
    protected void onCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            Util.getOperatingSystem().open("https://opalclient.com/dash");
            return SINGLE_SUCCESS;
        });
    }

}
