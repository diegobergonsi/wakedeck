package com.diegobergonsi.wakedeck.domain

/** Keeps each form field to the characters it can hold while the user types or pastes. Validation on save stays as the final check. */
object FieldInput {
    private const val MAC_MAX = 17
    private const val PORT_DIGITS = 5

    /** Hex digits and the separators [MagicPacket.parseMac] accepts, in capitals. */
    fun mac(text: String): String =
        text.filter { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' || it == ':' || it == '-' || it == '.' }.uppercase().take(MAC_MAX)

    fun port(text: String): String = text.filter { it in '0'..'9' }.take(PORT_DIGITS)

    /** IPv4, IPv6 or host name characters. */
    fun host(text: String): String =
        text.filter { it in '0'..'9' || it in 'a'..'z' || it in 'A'..'Z' || it == '.' || it == '-' || it == ':' || it == '_' }.take(Device.MAX_HOST)

    fun sshUser(text: String): String = text.filterNot { it.isWhitespace() }.take(Device.MAX_SSH_USER)
}
