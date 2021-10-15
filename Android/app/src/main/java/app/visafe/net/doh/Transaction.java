
package app.visafe.net.doh;

import app.visafe.net.dns.DnsPacket;
import java.io.Serializable;
import java.util.Calendar;

/**
 * A representation of a complete DNS transaction, whether it succeeded or failed.
 */
public class Transaction implements Serializable {

  public enum Status {
    COMPLETE,
    SEND_FAIL,
    HTTP_ERROR,
    BAD_RESPONSE,
    INTERNAL_ERROR,
    CANCELED
  }

  public Transaction(DnsPacket query, long timestamp) {
    this.name = query.getQueryName();
    this.type = query.getQueryType();
    this.queryTime = timestamp;
  }

  public final long queryTime;
  public final String name;
  public final short type;
  public long responseTime;
  public Status status;
  public byte[] response;
  public Calendar responseCalendar;
  public String serverIp;
}
