package rcon

import java.io.*
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

private const val SERVERDATA_EXECCOMMAND = 2
private const val SERVERDATA_AUTH = 3

class Rcon @Throws(IOException::class, AuthenticationException::class) constructor(
    host: String,
    port: Int,
    password: String,
    charset: Charset = Charsets.UTF_8,
    private val requestId: Int = Random().nextInt(),
) : AutoCloseable {
    private val socket = Socket(host, port)

    init {
        val res = send(SERVERDATA_AUTH, password.toByteArray(charset))

        if (res.requestId == -1)
            throw AuthenticationException("Password rejected by server")
    }

    @Throws(IOException::class)
    override fun close() = socket.close()

    @Throws(IOException::class)
    fun command(payload: String, charset: Charset = Charsets.UTF_8): String {
        require(payload.isNotBlank()) { "Payload can't be null or empty" }

        val response = send(SERVERDATA_EXECCOMMAND, payload.toByteArray(charset))
        return String(response.payload, charset)
    }

    @Throws(IOException::class)
    private fun send(type: Int, payload: ByteArray): RconPacket {
        socket.getOutputStream().writePacket(requestId, type, payload)
        return socket.getInputStream().readPacket()
    }
}
