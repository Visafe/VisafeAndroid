
package app.visafe.net.doh;

import android.content.Context;
import app.visafe.net.go.GoProber;

/**
 * This class performs parallel probes to all of the specified servers and calls the listener when
 * the fastest probe succeeds or all probes have failed.  Each instance can only be used once.
 */
public class Race {

  public interface Listener {
    /**
     * This method is called once, when the race has concluded.
     * @param index The index in urls of the fastest server, or -1 if all probes failed.
     */
    void onResult(int index);
  }

  /**
   * Starts a race between different servers.
   * @param context Used to read the IP addresses of the servers from storage.
   * @param urls The URLs for all the DOH servers to compare.
   * @param listener Called once on an arbitrary thread with the result of the race.
   */
  public static void start(Context context, String[] urls, Listener listener) {
    Prober prober = new GoProber(context);
    System.out.println(urls);
    start(prober, urls, listener);
  }

  // Exposed for unit testing only.
  static void start(Prober prober, String[] urls, Listener listener) {
    Collector collector = new Collector(urls.length, listener);
    for (int i = 0; i < urls.length; ++i) {
      prober.probe(urls[i], new Callback(i, collector));
    }
  }

  private static class Collector {
    private final int numCallbacks;
    private final Listener listener;
    private int numFailed = 0;
    private boolean reportedSuccess = false;

    Collector(int numCallbacks, Listener listener) {
      this.numCallbacks = numCallbacks;
      this.listener = listener;
    }

    synchronized void onCompleted(int index, boolean succeeded) {
      if (succeeded) {
        if (!reportedSuccess) {
          listener.onResult(index);
          reportedSuccess = true;
        }
      } else {
        ++numFailed;
        if (numFailed == numCallbacks) {
          // All probes failed
          listener.onResult(-1);
        }
      }
    }

  }

  private static class Callback implements Prober.Callback {
    private final int index;
    private final Collector collector;


    private Callback(int index, Collector collector) {
      this.index = index;
      this.collector = collector;
    }

    @Override
    public void onCompleted(boolean succeeded) {
      collector.onCompleted(index, succeeded);
    }
  }
}
