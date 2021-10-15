
package app.visafe.net.doh;

/**
 * A prober can perform asynchronous checks to determine whether a DOH server is working.
 */
public abstract class Prober {

  protected static final byte[] QUERY_DATA = {
      0, 0,  // [0-1]   query ID
      1, 0,  // [2-3]   flags, RD=1
      0, 1,  // [4-5]   QDCOUNT (number of queries) = 1
      0, 0,  // [6-7]   ANCOUNT (number of answers) = 0
      0, 0,  // [8-9]   NSCOUNT (number of authoritative answers) = 0
      0, 0,  // [10-11] ARCOUNT (number of additional records) = 0
      // Start of first query
      7, 'y', 'o', 'u', 't', 'u', 'b', 'e',
      3, 'c', 'o', 'm',
      0,  // null terminator of FQDN (DNS root)
      0, 1,  // QTYPE = A
      0, 1   // QCLASS = IN (Internet)
  };

  public interface Callback {
    void onCompleted(boolean succeeded);
  }

  /**
   * Called to execute the probe on a new thread.
   * @param url The DOH server URL to probe.
   * @param callback How to report the probe results
   */
  public abstract void probe(String url, Callback callback);
}
