package net.sourcebot.module.administration

import net.sourcebot.Source
import net.sourcebot.api.module.SourceModule
import net.sourcebot.module.administration.commands.BroadcastCommand
import net.sourcebot.module.administration.commands.ChangelogCommand

class Administration : SourceModule() {

    override fun onEnable(source: Source) {
        source.commandHandler.registerCommands(
                this,
                ChangelogCommand(),
                BroadcastCommand()
        )
    }
}