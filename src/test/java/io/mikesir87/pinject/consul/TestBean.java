package io.mikesir87.pinject.consul;

import javax.inject.Inject;

import org.soulwing.cdi.properties.Property;

/**
 * Bean that will be injected during tests.
 *
 * @author Michael Irwin
 */
public class TestBean {

  static final String DEFAULT_VALUE = "Default value";

  @Inject
  @Property(value = DEFAULT_VALUE)
  private String message;

  String getMessage() {
    return message;
  }
}
