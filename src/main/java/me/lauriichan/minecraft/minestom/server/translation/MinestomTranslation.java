package me.lauriichan.minecraft.minestom.server.translation;

import me.lauriichan.laylib.localization.MessageProvider;
import me.lauriichan.laylib.localization.source.Message;
import me.lauriichan.minecraft.minestom.server.extension.Extension;

@Extension
public final class MinestomTranslation implements ITranslationExtension {
    
    private MinestomTranslation() {
        throw new UnsupportedOperationException();
    }
    
    @Message(id = "server.name", content = "&cMinestomServer")
    public static MessageProvider SERVER_NAME;
    @Message(id = "server.prefix", content = "$server.prefix &8|&7")
    public static MessageProvider SERVER_PREFIX;
    
    @Message(id = "command.system.execution-failed", content = "$#server.prefix &cFailed to execute command '$command', please contact an administrator for help")
    public static MessageProvider COMMAND_SYSTEM_EXECUTION_FAILED;

}
