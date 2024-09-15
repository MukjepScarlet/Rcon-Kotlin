package rcon

import java.io.*
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal data class RconPacket(
    val requestId: Int,
    val type: Int,
    val payload: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RconPacket

        if (requestId != other.requestId) return false
        if (type != other.type) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = requestId
        result = 31 * result + type
        result = 31 * result + payload.contentHashCode()
        return result
    }
}

@Throws(IOException::class)
internal fun OutputStream.writePacket(requestId: Int, type: Int, payload: ByteArray) {
    val bodyLength = 4 + 4 + payload.size + 2
    val packetLength = 4 + bodyLength

    val buffer = ByteBuffer.allocate(packetLength).order(ByteOrder.LITTLE_ENDIAN)

    buffer.putInt(bodyLength)
    buffer.putInt(requestId)
    buffer.putInt(type)
    buffer.put(payload)
    buffer.put(0.toByte()) // Null byte terminators
    buffer.put(0.toByte())

    write(buffer.array())
    flush()
}

@Throws(IOException::class, MalformedPacketException::class)
internal fun InputStream.readPacket(): RconPacket {
    val header = ByteArray(4 * 3)
    read(header)

    try {
        val buffer = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)

        val length = buffer.getInt()
        val requestId = buffer.getInt()
        val type = buffer.getInt()

        val payload = ByteArray(length - 4 - 4 - 2)
        val dis = DataInputStream(this)

        dis.readFully(payload)
        dis.read(ByteArray(2)) // 读掉末尾的两个空字节

        return RconPacket(requestId, type, payload)
    } catch (e: BufferUnderflowException) {
        throw MalformedPacketException("Cannot read the whole packet")
    } catch (e: EOFException) {
        throw MalformedPacketException("Cannot read the whole packet")
    }
}
