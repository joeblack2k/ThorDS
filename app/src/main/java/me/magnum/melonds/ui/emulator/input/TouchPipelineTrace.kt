package me.magnum.melonds.ui.emulator.input

import android.graphics.RectF
import android.view.MotionEvent
import java.util.ArrayDeque

internal object TouchPipelineTrace {
    private const val MAX_SEQUENCES = 32
    private val lock = Any()
    private val sequences = ArrayDeque<MutableMap<String, Any?>>()
    private var nextSequenceId = 0L

    fun begin(event: MotionEvent, viewport: RectF?, displayId: Int?, accepted: Boolean) {
        synchronized(lock) {
            if (sequences.size == MAX_SEQUENCES) sequences.removeFirst()
            sequences.addLast(linkedMapOf(
                "touchSequenceId" to ++nextSequenceId,
                "actionMasked" to event.actionMasked,
                "eventTime" to event.eventTime,
                "downTime" to event.downTime,
                "source" to event.source,
                "deviceId" to event.deviceId,
                "displayId" to displayId,
                "localX" to event.x,
                "localY" to event.y,
                "rawX" to event.rawX,
                "rawY" to event.rawY,
                "viewport" to viewport?.let { listOf(it.left, it.top, it.right, it.bottom) },
                "accepted" to accepted,
                "stages" to mutableListOf("android_handler"),
            ))
        }
    }

    fun record(stage: String, vararg values: Pair<String, Any?>) {
        synchronized(lock) {
            val sequence = sequences.lastOrNull() ?: return
            @Suppress("UNCHECKED_CAST")
            (sequence["stages"] as MutableList<String>).add(stage)
            values.forEach { sequence[it.first] = it.second }
        }
    }

    fun dumpJson(): String {
        synchronized(lock) {
            fun encode(value: Any?): String = when (value) {
                null -> "null"
                is String -> "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
                is List<*> -> value.joinToString(prefix = "[", postfix = "]") { encode(it) }
                is Map<*, *> -> value.entries.joinToString(prefix = "{", postfix = "}") {
                    encode(it.key.toString()) + ":" + encode(it.value)
                }
                else -> value.toString()
            }
            return sequences.joinToString(prefix = "[", postfix = "]") { encode(it) }
        }
    }
}
