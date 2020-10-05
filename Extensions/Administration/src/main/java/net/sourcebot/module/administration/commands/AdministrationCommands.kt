package net.sourcebot.module.administration.commands

import net.dv8tion.jda.api.entities.Message
import net.sourcebot.api.command.RootCommand
import net.sourcebot.api.command.argument.*
import net.sourcebot.api.response.EmptyResponse
import net.sourcebot.api.response.InfoResponse
import net.sourcebot.api.response.Response

abstract class AdministrationCommand internal constructor(
        final override val name: String,
        final override val description: String
) : RootCommand() {
    override val permission = "administration.$name"
    override val guildOnly = true
}

class ChangelogCommand : AdministrationCommand(
        "changelog", "Posts a new changelog in your guild."
) {

    override val argumentInfo = ArgumentInfo(
            OptionalArgument("channel", "What channel to send the changelog to. (Defaults to the executed channel)"),
            Argument("update", "What the changelog is about. Use `|` to create a new line")
    )

    override fun execute(message: Message, args: Arguments): Response {
        val channel = args.next(Adapter.channel(message.guild)) ?: message.textChannel
        val update = args.slurp(" ", "You didnt provide a update")

        channel.sendMessage(ChangelogResponse(update).asEmbed(message.author)).queue()
        return EmptyResponse()
    }
}

class BroadcastCommand : AdministrationCommand(
        "broadcast", "Broadcasts a message to your guild via the bot."
) {
    override val aliases = arrayOf("bc", "say")
    override var cleanupResponse = false

    override val argumentInfo = ArgumentInfo(
            OptionalArgument("channel", "The channel you want to send the message to. (Defaults to the executed channel"),
            OptionalArgument("embed", "Whether you want it into a embed."),
            Argument("message", "The message content you want to broadcast")
    )

    override fun execute(message: Message, args: Arguments): Response {
        message.delete().queue()
        val channel = args.next(Adapter.channel(message.guild)) ?: message.textChannel

        if (args.hasNext() && args.next()?.equals("embed", ignoreCase = true)!!) {
            val embeddedMessage = InfoResponse("Broadcasted Message", args.slurp(" ", "You must provide a valid broadcast message"))
                    .asEmbed(message.author)
            channel.sendMessage(embeddedMessage).queue()
        }
        args.backtrack(1)
        channel.sendMessage(args.slurp(" ", "You must provide a valid broadcast message")).queue()
        return EmptyResponse()
    }
}

private class ChangelogResponse constructor(
        update: String
) : InfoResponse(
        "Guild Changelog", "Here is an overview of the changes"
) {
    init {
        addField("Overview", "-${update.replace("|", "\n-")}", false)
    }
}