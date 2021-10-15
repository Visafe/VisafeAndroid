
package app.visafe.sys.firebase;

import android.os.Bundle;

/**
 * Builder-pattern wrapper around android.os.Bundle, with type enforcement for parameters used
 * by visafe.
 */
class BundleBuilder {

  // Parameters sent in Analytics events
  enum Params {
    BYTES,
    CHUNKS,
    DEVICE_COUNTRY,
    DOWNLOAD,
    DURATION,
    LATENCY,
    MODE,
    NETWORK_COUNTRY,
    NETWORK_TYPE,
    PORT,
    RESULT,
    RETRY,
    SERVER,
    SPLIT,
    TCP_HANDSHAKE_MS,
    TIMEOUT,
    UPLOAD,
  }

  final private Bundle bundle = new Bundle();

  BundleBuilder put(Params key, int val) {
    bundle.putInt(key.name(), val);
    return this;
  }

  BundleBuilder put(Params key, long val) {
    bundle.putLong(key.name(), val);
    return this;
  }

  BundleBuilder put(Params key, String val) {
    bundle.putString(key.name(), val);
    return this;
  }

  Bundle build() {
    return bundle;
  }
}
