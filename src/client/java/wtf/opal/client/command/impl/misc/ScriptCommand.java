package wtf.opal.client.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import wtf.opal.client.OpalClient;
import wtf.opal.client.command.Command;
import wtf.opal.utility.misc.chat.ChatUtility;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static wtf.opal.client.Constants.mc;

public final class ScriptCommand extends Command {

    public ScriptCommand() {
        super("script", "Allows you to do actions with your scripts.");
    }

    @Override
    protected void onCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("reload").executes(context -> {
            final int scriptAmount = OpalClient.getInstance().getScriptRepository().loadScripts();

            ChatUtility.print(scriptAmount + " scripts successfully reloaded!");

            OpalClient.getInstance().getScriptRepository().getScriptList().forEach(script -> {
                if (script.getModule() != null) {
                    script.getModule().setEnabled(true);
                }
            });
            return SINGLE_SUCCESS;
        }));
    }
}
