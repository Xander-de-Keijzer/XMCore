package nl.xandermarc.mc.core.commands

import nl.xandermarc.mc.core.managers.EditorManager
import nl.xandermarc.mc.core.managers.TrackManager
import nl.xandermarc.mc.lib.commands.Command
import nl.xandermarc.mc.lib.commands.annotations.*
import nl.xandermarc.mc.lib.extensions.send
import nl.xandermarc.mc.ride.Track

@Permission("command.track")
object TrackCommand : Command() {

    fun create(@Greedy("_") @Lower track: String) {
        if (TrackManager.has(track)) return source.send("Track <0> already exists!", track)

        TrackManager.put(Track(track))
        source.send("Track <0> created.", track)
    }

    fun remove(@Greedy("_") @Lower track: String) {
        if (!TrackManager.has(track)) return source.send("Track <0> does not exist!", track)

        TrackManager.remove(track)
        source.send("Track <0> removed.", track)
    }

    @PlayerOnly fun edit(@Greedy("_") @Lower track: String) {
        if (!TrackManager.has(track)) return player.send("Track <0> does not exist!", track)

        TrackManager.get(track)!!.startEditor(player)
        player.send("Track editor of <0> started.", track)
    }

    @PlayerOnly fun edit() {
        if (!EditorManager.has(player)) return player.send("You are not editing any track, use <0>.", "/track edit <name>")

        EditorManager.closeAll(player)
        player.sendMessage("Track editor closed.")
    }

}
