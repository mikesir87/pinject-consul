package io.mikesir87.pinject.consul;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import static io.mikesir87.pinject.consul.rules.ConsulPopulatingRule.PROD_DATA_VALUE;
import static io.mikesir87.pinject.consul.rules.ConsulPopulatingRule.PROD_TREE_ROOT;
import static io.mikesir87.pinject.consul.TestBean.DEFAULT_VALUE;
import static io.mikesir87.pinject.consul.rules.ConsulPopulatingRule.SHARED_DATA_VALUE;
import static io.mikesir87.pinject.consul.rules.ConsulPopulatingRule.SHARED_TREE_ROOT;

import javax.enterprise.inject.spi.DefinitionException;
import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import io.mikesir87.pinject.consul.rules.CaptureWeldExceptionRule;
import io.mikesir87.pinject.consul.rules.ConsulDockerRule;
import io.mikesir87.pinject.consul.rules.ConsulPopulatingRule;
import io.mikesir87.pinject.consul.rules.EnvProperties;
import io.mikesir87.pinject.consul.rules.SystemPropertySettingRule;

/**
 * Integration test of the {@link ConsulPropertyProvider} that actually
 * launches a Consul container, populates it, and then starts the Weld
 * container.
 *
 * @author Michael Irwin
 */
public class ConsulPropertyProviderIT {

  @ClassRule
  public static ConsulDockerRule consulContainer = new ConsulDockerRule();

  @Rule
  public TestRule chain = RuleChain
      .outerRule(new SystemPropertySettingRule())
      .around(new ConsulPopulatingRule())
      .around(new CaptureWeldExceptionRule())
      .around(WeldInitiator.from((new Weld()).enableDiscovery()).inject(this).build());

  @Inject
  private TestBean testBean;

  @Test
  @EnvProperties
  public void testBeanInjection() {
    assertThat(testBean.getMessage(), is(equalTo(PROD_DATA_VALUE)));
  }

  @Test
  @EnvProperties(treeRoot = SHARED_TREE_ROOT)
  public void testWithDifferentRoot() {
    assertThat(testBean.getMessage(), is(equalTo(SHARED_DATA_VALUE)));
  }

  @Test
  @EnvProperties(treeRoot = SHARED_TREE_ROOT + " " + PROD_TREE_ROOT)
  public void testWithMultipleRoots() {
    assertThat(testBean.getMessage(), is(equalTo(PROD_DATA_VALUE)));
  }

  @Test
  @EnvProperties(treeRoot = "another/root")
  public void testWithBadRoot() {
    assertThat(testBean.getMessage(), is(equalTo(DEFAULT_VALUE)));
  }

  @Test(expected = DefinitionException.class)
  @EnvProperties(host = "another-host")
  public void testWithBadHost() {
    fail("Shouldn't have gotten here, as it should have prevented weld from bootstrapping");
  }

  @Test
  @EnvProperties(treeRoot = PROD_TREE_ROOT + "/")
  public void usingTreeRootWithEndingSlash() {
    assertThat(testBean.getMessage(), is(equalTo(PROD_DATA_VALUE)));
  }
}