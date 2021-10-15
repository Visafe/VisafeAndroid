
package app.visafe.sys;

public final class VpnState {
  // Whether the user has requested that the VPN be active.  This is persistent state, sync'd to
  // disk.
  public final boolean activationRequested;

  // Whether the VPN is running.  When this is true a key icon is showing in the status bar.
  public final boolean on;

  // Whether we have a connection to a DOH server, and if so, whether the connection is ready or
  // has recently been failing.
  public final ViSafeVpnService.State connectionState;

  VpnState(boolean activationRequested, boolean on, ViSafeVpnService.State connectionState) {
    this.activationRequested = activationRequested;
    this.on = on;
    this.connectionState = connectionState;
  }
}
