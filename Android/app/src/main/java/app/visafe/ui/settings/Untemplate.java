
package app.visafe.ui.settings;

/**
 * Utility wrapper for trivial resolution of URI templates to URLs.
 */

public class Untemplate {
  /**
   * Performs variable expansion on a URI Template (RFC 6570) in the special case where all
   * variables are undefined.  This is the only case of URI templates that is needed for DOH in POST
   * mode.
   * @param template A URI template (or just a URI)
   * @return A URI produced by this template when all variables are undefined
   */
  public static String strip(String template) {
    return template.replaceAll("\\{[^}]*\\}", "");
  }
}
