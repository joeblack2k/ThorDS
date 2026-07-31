package me.magnum.melonds.domain.model.enhancement

import java.util.zip.CRC32

interface DeltaPatchApplier {
    fun apply(source: ByteArray, patch: ByteArray): ByteArray
}

object IpsPatchApplier : DeltaPatchApplier {
    override fun apply(source: ByteArray, patch: ByteArray): ByteArray {
        require(patch.copyOfRange(0, 5).decodeToString() == "PATCH") { "Invalid IPS header" }
        var cursor = 5
        var output = source.copyOf()
        while (true) {
            require(cursor + 3 <= patch.size) { "Truncated IPS record" }
            if (patch.copyOfRange(cursor, cursor + 3).decodeToString() == "EOF") return output
            val offset = patch.readUInt24(cursor)
            val length = patch.readUInt16(cursor + 3)
            cursor += 5
            if (length == 0) {
                require(cursor + 3 <= patch.size) { "Truncated IPS RLE" }
                val count = patch.readUInt16(cursor)
                val value = patch[cursor + 2]
                cursor += 3
                output = output.grow(offset + count)
                repeat(count) { output[offset + it] = value }
            } else {
                require(cursor + length <= patch.size) { "Truncated IPS payload" }
                output = output.grow(offset + length)
                patch.copyInto(output, offset, cursor, cursor + length)
                cursor += length
            }
        }
    }
}

object BpsPatchApplier : DeltaPatchApplier {
    override fun apply(source: ByteArray, patch: ByteArray): ByteArray {
        require(patch.size >= 16 && patch.copyOfRange(0, 4).decodeToString() == "BPS1") { "Invalid BPS header" }
        var cursor = 4
        fun readNumber(): Long {
            var result = 0L
            var shift = 1L
            while (cursor < patch.size) {
                val value = patch[cursor++].toInt() and 0xff
                result += (value and 0x7f) * shift
                if (value and 0x80 != 0) return result
                shift = shift shl 7
                result += shift
            }
            error("Truncated BPS number")
        }
        fun readSigned(): Long {
            val value = readNumber()
            return if (value and 1 == 0L) value shr 1 else -((value shr 1) + 1)
        }

        val sourceSize = readNumber().toInt()
        val targetSize = readNumber().toInt()
        val metadataSize = readNumber().toInt()
        require(sourceSize == source.size && cursor + metadataSize <= patch.size - 12) { "BPS source mismatch" }
        require(source.crc32() == patch.readUInt32(patch.size - 12)) { "BPS source checksum mismatch" }
        cursor += metadataSize
        val output = ByteArray(targetSize)
        var outputOffset = 0
        var sourceRelative = 0
        var targetRelative = 0
        while (outputOffset < targetSize) {
            val action = readNumber()
            val type = (action and 3).toInt()
            val length = (action shr 2).toInt() + 1
            require(outputOffset + length <= targetSize) { "BPS action exceeds target" }
            when (type) {
                0 -> {
                    require(outputOffset + length <= source.size) { "BPS source read out of range" }
                    source.copyInto(output, outputOffset, outputOffset, outputOffset + length)
                }
                1 -> {
                    require(cursor + length <= patch.size - 12) { "Truncated BPS target read" }
                    patch.copyInto(output, outputOffset, cursor, cursor + length)
                    cursor += length
                }
                2 -> {
                    sourceRelative += readSigned().toInt()
                    require(sourceRelative >= 0 && sourceRelative + length <= source.size) { "BPS source copy out of range" }
                    source.copyInto(output, outputOffset, sourceRelative, sourceRelative + length)
                    sourceRelative += length
                }
                3 -> {
                    targetRelative += readSigned().toInt()
                    require(targetRelative >= 0 && targetRelative < outputOffset) { "BPS target copy out of range" }
                    repeat(length) { output[outputOffset + it] = output[targetRelative + it] }
                    targetRelative += length
                }
            }
            outputOffset += length
        }
        require(cursor == patch.size - 12) { "Unexpected BPS data" }
        require(output.crc32() == patch.readUInt32(cursor + 4)) { "BPS target checksum mismatch" }
        require(patch.copyOfRange(0, patch.size - 4).crc32() == patch.readUInt32(cursor + 8)) { "BPS patch checksum mismatch" }
        return output
    }
}

private fun ByteArray.grow(size: Int): ByteArray = if (size <= this.size) this else copyOf(size)
private fun ByteArray.readUInt16(offset: Int): Int = ((this[offset].toInt() and 0xff) shl 8) or (this[offset + 1].toInt() and 0xff)
private fun ByteArray.readUInt24(offset: Int): Int = ((this[offset].toInt() and 0xff) shl 16) or ((this[offset + 1].toInt() and 0xff) shl 8) or (this[offset + 2].toInt() and 0xff)
private fun ByteArray.readUInt32(offset: Int): Long = (0..3).fold(0L) { value, index -> value or ((this[offset + index].toLong() and 0xff) shl (index * 8)) }
private fun ByteArray.crc32(): Long = CRC32().also { it.update(this) }.value
