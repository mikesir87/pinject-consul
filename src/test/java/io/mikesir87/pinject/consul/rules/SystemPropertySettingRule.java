package io.mikesir87.pinject.consul.rules;

import static io.mikesir87.pinject.consul.rules.ConsulPopulatingRule.PROD_TREE_ROOT;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A rule that sets the environment variables used by the
 * {@link io.mikesir87.pinject.consul.ConsulPropertyProvider}. The values to
 * be used can be manipulated by using the {@link EnvProperties} annotation
 * on a test method.
 *
 * @author Michael Irwin
 */
public class SystemPropertySettingRule implements TestRule {

  @Override
  public Statement apply(Statement statement, Description description) {
    EnvProperties env = description.getAnnotation(EnvProperties.class);
    String host = (env == null) ? "localhost" : env.host();
    String root = (env == null) ? PROD_TREE_ROOT : env.treeRoot();
    System.setProperty("PINJECT_CONSUL_HOST", host);
    System.setProperty("PINJECT_CONSUL_TREE_ROOT", root);
    return statement;
  }
}
