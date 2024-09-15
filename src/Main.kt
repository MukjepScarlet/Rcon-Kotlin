import rcon.Rcon

/**
 * Sample Usage
 */
fun main(vararg args: String) {
    val port = args[0].toInt()
    val password = args[1]
    val command = args.copyOfRange(2, args.size).joinToString(" ")

    Rcon("127.0.0.1", port, password).use {
        println(it.command(command))
    }
}