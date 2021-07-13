package vn.ncsc.visafe.dns.net.go

import android.os.SystemClock
import androidx.collection.LongSparseArray
import doh.Doh
import doh.Summary
import doh.Token
import intra.Listener
import intra.TCPSocketSummary
import intra.UDPSocketSummary
import vn.ncsc.visafe.dns.net.dns.DnsPacket
import vn.ncsc.visafe.dns.net.doh.Transaction
import vn.ncsc.visafe.dns.sys.IntraVpnService
import java.net.ProtocolException
import java.util.*

class GoIntraListener internal constructor(private var vpnService: IntraVpnService) : Listener {
    override fun onTCPSocketClosed(summary: TCPSocketSummary) {
        val retry = summary.retry
        if (retry != null) {
            // Connection was eligible for split-retry.
            if (retry.split.toInt() == 0) {
                // Split-retry was not attempted.
                return
            }
            val success = summary.downloadBytes > 0
        }
    }

    override fun onUDPSocketClosed(summary: UDPSocketSummary) {
        val totalBytes = summary.uploadBytes + summary.downloadBytes
        if (totalBytes < UDP_THRESHOLD_BYTES) {
            return
        }
    }

    companion object {
        // UDP is often used for one-off messages and pings.  The relative overhead of reporting metrics
        // on these short messages would be large, so we only report metrics on sockets that transfer at
        // least this many bytes.
        private const val UDP_THRESHOLD_BYTES = 10000
        private val goStatusMap = LongSparseArray<Transaction.Status>()
        private fun len(a: ByteArray?): Int {
            return a?.size ?: 0
        }

        init {
            goStatusMap.put(Doh.Complete, Transaction.Status.COMPLETE)
            goStatusMap.put(Doh.SendFailed, Transaction.Status.SEND_FAIL)
            goStatusMap.put(Doh.HTTPError, Transaction.Status.HTTP_ERROR)
            goStatusMap.put(Doh.BadQuery, Transaction.Status.INTERNAL_ERROR) // TODO: Add a BAD_QUERY Status
            goStatusMap.put(Doh.BadResponse, Transaction.Status.BAD_RESPONSE)
            goStatusMap.put(Doh.InternalError, Transaction.Status.INTERNAL_ERROR)
        }
    }

    override fun onQuery(url: String?): Token? {
        return null
    }

    override fun onResponse(token: Token?, summary: Summary) {
        val query: DnsPacket = try {
            DnsPacket(summary.query)
        } catch (e: ProtocolException) {
            return
        }
        val latencyMs = (1000 * summary.latency).toLong()
        val nowMs = SystemClock.elapsedRealtime()
        val queryTimeMs = nowMs - latencyMs
        val transaction = Transaction(query, queryTimeMs)
        transaction.response = summary.response
        transaction.responseTime = (1000 * summary.latency).toLong()
        transaction.serverIp = summary.server
        transaction.status = goStatusMap[summary.status]
        transaction.responseCalendar = Calendar.getInstance()
        vpnService.recordTransaction(transaction)
    }

}