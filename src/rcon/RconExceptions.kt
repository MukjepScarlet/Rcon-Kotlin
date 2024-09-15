package rcon

import java.io.IOException

class AuthenticationException(message: String? = null) : Exception(message)

class MalformedPacketException(message: String? = null) : IOException(message)
