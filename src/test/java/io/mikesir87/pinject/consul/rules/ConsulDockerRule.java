package io.mikesir87.pinject.consul.rules;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import com.arakelian.docker.junit.DockerRule;
import com.arakelian.docker.junit.model.ImmutableDockerConfig;
import com.spotify.docker.client.messages.PortBinding;

/**
 * Custom {@link DockerRule} that creates a Consul container and waits for
 * it to up and responding. Note that this rule is going to expose Consul
 * on its standard HTTP port, 8500. Therefore, if you are currently using
 * the port, the rule will fail.
 *
 * @author Michael Irwin
 */
public class ConsulDockerRule extends DockerRule {

  private static final Integer CONSUL_HTTP_PORT = 8500;

  public ConsulDockerRule() {
    super(ImmutableDockerConfig.builder()
        .name("consul-junit")
        .image("consul")
        .ports(CONSUL_HTTP_PORT.toString())
        .alwaysRemoveContainer(true)
        .addHostConfigurer(hc ->
            hc.portBindings(
                Collections.singletonMap(
                    CONSUL_HTTP_PORT.toString(),
                    Collections.singletonList(PortBinding.create("0.0.0.0", CONSUL_HTTP_PORT.toString()))
                )
            )
        )
        .addStartedListener(container -> {
          container.waitForPort("0.0.0.0", CONSUL_HTTP_PORT, 5, TimeUnit.SECONDS);
          container.waitForLog("Node info in sync");
        }).build());
  }
}
