package io.mikesir87.pinject.consul.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import com.ecwid.consul.v1.ConsulClient;

/**
 * A {@link TestRule} that populates Consul with dummy data to be used
 * during the tests.
 *
 * @author Michael Irwin
 */
public class ConsulPopulatingRule implements TestRule {

  public static final String SHARED_TREE_ROOT = "test/project/base";
  public static final String PROD_TREE_ROOT = "test/project/prod";
  public static final String SHARED_DATA_VALUE = "Hello Shared";
  public static final String PROD_DATA_VALUE = "Hello Prod";

  @Override
  public Statement apply(Statement statement, Description description) {
    ConsulClient consulClient = new ConsulClient("localhost");
    consulClient.setKVValue(SHARED_TREE_ROOT + "/io/mikesir87/pinject/consul/TestBean/message", SHARED_DATA_VALUE);
    consulClient.setKVValue(PROD_TREE_ROOT + "/io/mikesir87/pinject/consul/TestBean/message", PROD_DATA_VALUE);
    return statement;
  }
}
