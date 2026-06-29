package wtf.opal.client.command.impl.config;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import wtf.opal.client.command.Command;
import wtf.opal.client.command.arguments.ConfigArgumentType;
import wtf.opal.utility.data.SaveUtility;
import wtf.opal.utility.misc.chat.ChatUtility;

import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public final class ConfigCommand extends Command {

    public ConfigCommand() {
        super("Failed to initialize repository:", "Interacts with configs.", "c");
    }

    @Override
    protected void onCommand(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("save").then(argument("config_name", ConfigArgumentType.create()).executes(context -> {
            final String configName = context.getArgument("config_name", String.class).toLowerCase();

            if (SaveUtility.saveConfig(configName)) {
                ChatUtility.success("Config §l" + configName + "§7 saved!");
            } else {
                ChatUtility.error("Failed to save config §l" + configName + "§7.");
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("list").executes(context -> {
            final List<String> configs = SaveUtility.listConfigs();
            if (configs.isEmpty()) {
                ChatUtility.print("No configs found.");
                return SINGLE_SUCCESS;
            }

            ChatUtility.print("Configs: §l" + String.join("§7, §l", configs) + "§7");

            return SINGLE_SUCCESS;
        }));

        builder.then(literal("load").then(argument("config_name", ConfigArgumentType.create()).executes(context -> {
            final String configName = context.getArgument("config_name", String.class).toLowerCase();

            if (SaveUtility.loadConfigFile(configName)) {
                ChatUtility.success("Config §l" + configName + "§7 loaded!");
            } else {
                ChatUtility.error("Failed to load config §l" + configName + "§7.");
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("delete").then(argument("config_name", ConfigArgumentType.create()).executes(context -> {
            final String configName = context.getArgument("config_name", String.class).toLowerCase();

            if (SaveUtility.deleteConfig(configName)) {
                ChatUtility.success("Config §l" + configName + "§7 deleted!");
            } else {
                ChatUtility.error("Failed to delete config §l" + configName + "§7.");
            }

            return SINGLE_SUCCESS;
        })));
    }
}
