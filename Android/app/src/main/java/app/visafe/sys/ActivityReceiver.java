
package app.visafe.sys;

import java.util.Collection;

/**
 * Interface for classes that can receive information about when recent events occurred.
 *
 * This class primarily serves to allow HistoryGraph to scan QueryTracker's activity data
 * while QueryTracker holds a lock, without making an extra copy and without QueryTracker
 * having to import HistoryGraph.
 */
public interface ActivityReceiver {
  /**
   * @param activity The SystemClock.elapsedRealtime() timestamps of each event in the recent
   *                 activity history as an unmodifiable collection.  The implementor must not
   *                 retain the argument past the end of this call, as it is owned by the caller
   *                 and may be modified.
   */
  void receive(Collection<Long> activity);
}
