package com.vn.visafe_android.dns.net.doh

import com.vn.visafe_android.dns.net.dns.DnsPacket
import java.io.Serializable
import java.util.*

/**
 * A representation of a complete DNS transaction, whether it succeeded or failed.
 */
class Transaction(query: DnsPacket, timestamp: Long) : Serializable {
    enum class Status {
        COMPLETE, SEND_FAIL, HTTP_ERROR, BAD_RESPONSE, INTERNAL_ERROR, CANCELED
    }

    val queryTime: Long = timestamp
    val name: String = query.queryName
    val type: Short = query.queryType
    var responseTime: Long = 0
    var status: Status? = null
    var response: ByteArray? = null
    var responseCalendar: Calendar? = null
    var serverIp: String? = null
}