package me.magnum.melonds.domain.model.enhancement

import me.magnum.melonds.domain.model.Cheat

object RuntimeActionReplayComposer {
    fun compose(plan: ResolvedSessionPlan): List<Cheat> {
        val curated = plan.curatedRuntimeCodes.sortedBy { it.id }.mapIndexed { index, code ->
            Cheat(
                id = Long.MIN_VALUE + index,
                cheatDatabaseId = Long.MIN_VALUE,
                name = "ThorDS: ${code.id}",
                description = "Curated enhancement runtime code",
                code = code.codeWords.joinToString("\n"),
                enabled = true,
            )
        }
        return curated + plan.userCheats
    }
}
