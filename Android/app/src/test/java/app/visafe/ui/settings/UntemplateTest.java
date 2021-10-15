
package app.visafe.ui.settings;

import org.junit.Test;

import static org.junit.Assert.*;

public class UntemplateTest {

  private void assertUnmodified(String input) {
    assertEquals(input, Untemplate.strip(input));
  }

  @Test
  public void testUnmodified() throws Exception {
    assertUnmodified("");
    assertUnmodified("https://foo.example/");
  }

  @Test
  public void testTemplateBasic() throws Exception {
    assertEquals("", Untemplate.strip("{test}"));
    assertEquals("https://foo.example/query",
        Untemplate.strip("https://foo.example/query{?dns}"));
  }

  @Test
  public void testTemplateWeird() throws Exception {
    assertEquals("https://foo.example/prefix/suffix",
        Untemplate.strip("https://foo.example/prefix{/dns}/suffix"));
  }

}