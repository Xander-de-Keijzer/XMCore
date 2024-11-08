package nl.xandermarc.mc.core.commands

import nl.xandermarc.mc.core.commands.TrackCommand.description
import nl.xandermarc.mc.core.commands.TrackCommand.name
import nl.xandermarc.mc.core.managers.EditorManager
import nl.xandermarc.mc.core.managers.TrackManager
import nl.xandermarc.mc.lib.commands.Command
import nl.xandermarc.mc.lib.commands.annotations.*
import nl.xandermarc.mc.lib.extensions.send
import nl.xandermarc.mc.ride.tracked.TrackEditor

/**
 * Represents a command within the Minecraft plugin framework.
 *
 * @property name The name of the command. Defaults to the suffix of the class name if not provided.
 * @property description A brief description of the command's functionality.
 *
 * @constructor Creates a Command instance with an optional name and description.
 *
 * The Command class also integrates with Paper's lifecycle events to automatically register commands when
 * the plugin lifecycle reaches the appropriate stage.
 *
 * To create a command, extend the Command class and define functions within the subclass. Each function
 * represents a different command that can be executed. The arguments of the function are automatically parsed
 * from the command input if possible. For example, if you define a function like:
 *
 * ```kotlin
 * fun create(track: String) {
 *     // Command logic here
 * }
 * ```
 *
 * The `track` argument will be parsed from the input provided by the player, assuming a string input is available.
 * If the command is `/command create myTrack`, the `track` parameter will be assigned the value "myTrack".
 *
 * The supported types are [Boolean], [Enum], [Int], [List]<[String]>, [org.bukkit.entity.Player], [String].
 * There is a special type [Unit] which can be used to create a literal argument, meaning the player has to pass the
 * parameter name as an argument. For example `fun players(list: Unit)` will require `/<cmd> players list`.
 *
 * Annotations can be used to modify the behavior of the command:
 * - **@[Greedy]**: Captures all remaining arguments as a single parameter, joined by the specified separator. For instance, `@Greedy("_") track: String` will capture everything after the command keyword, allowing for spaces in the value, and join them using "_" as the separator.
 * - **@[Lower]**: Converts the argument value to lowercase, ensuring that comparisons are case-insensitive.
 * - **@[Permission]**: Restricts command execution to users with the specified permission node. This helps enforce role-based access control for commands.
 * - **@[PlayerOnly]**: Ensures that the command can only be executed by a player (not from the console or other sources). This is useful when the command logic involves player-specific actions.
 * - **@[Options]**: Restricts the argument to a predefined set of valid options, ensuring that only specific values are allowed.
 * - **@[Root]**: Marks the function as the root command, which means the name of the function will be ignored and thus not used as a subcommand.
 * - **@[Range]**: Defines a valid numeric range for the argument. This is useful for ensuring that input values fall within a specific range, such as limiting a value between 1 and 100.
 */
@Permission("command.track")
object TrackCommand : Command() {

    fun create(@Greedy("_") @Lower track: String) {
        if (TrackManager.exists(track)) return source.send("Track <0> already exists!", track)

        TrackManager.create(track)
        source.send("Track <0> created.", track)
    }

    fun remove(@Greedy("_") @Lower track: String) {
        if (!TrackManager.exists(track)) return source.send("Track <0> does not exist!", track)

        TrackManager.remove(track)
        source.send("Track <0> removed.", track)
    }

    @PlayerOnly fun edit(@Greedy("_") @Lower track: String) {
        if (!TrackManager.exists(track)) return player.send("Track <0> does not exist!", track)

        EditorManager.registerEditor(TrackEditor(player, TrackManager.get(track)!!))
        player.send("Track editor of <0> started.", track)
    }

    @PlayerOnly fun edit() {
        if (!EditorManager.hasEditor(player)) return player.send("You are not editing any track, use <0>.", "/track edit <name>")

        EditorManager.closeEditor(player)
        player.sendMessage("Track editor closed.")
    }

}
