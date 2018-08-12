package io.mikesir87.pinject.consul.rules;

import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Rule that simply catches exceptions that might have been thrown during
 * execution of another rule. This occurs specifically when the provider is
 * given a bad host name, which causes the property resolver to throw an
 * exception, preventing the weld container from creating.
 *
 * @author Michael Irwin
 */
public class CaptureWeldExceptionRule implements TestRule {

  @Override
  public Statement apply(Statement statement, Description description) {
    return new Statement() {
      @Override public void evaluate() throws Throwable {
        try {
          statement.evaluate();
        } catch (Exception e) {
          Test testInfo = description.getAnnotation(Test.class);
          if (testInfo.expected().isAssignableFrom(e.getClass()))
            return;
          throw e;
        }
      }
    };
  }

}
