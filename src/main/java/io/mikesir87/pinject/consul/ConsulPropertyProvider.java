package io.mikesir87.pinject.consul;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.soulwing.cdi.properties.spi.PropertyResolver;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.KeyValueClient;
import com.ecwid.consul.v1.kv.model.GetValue;

/**
 * A {@link PropertyResolver} that pulls its properties from Consul. See
 * documentation on how to specify the Consul configuration..
 *
 * @author Michael Irwin
 */
public class ConsulPropertyProvider implements PropertyResolver {

  private Map<String, String> properties = new HashMap<>();

  private String treeRootPath;
  private String token;
  private KeyValueClient consulClient;

  public ConsulPropertyProvider() {
    String host = getValue("PINJECT_CONSUL_HOST");
    treeRootPath = getValue("PINJECT_CONSUL_TREE_ROOT");
    token = getValue("PINJECT_CONSUL_TOKEN");

    if (host != null && treeRootPath != null)
      this.consulClient = new ConsulClient(host);
  }

  @Override
  public void init() {
    for (String root : treeRootPath.split(" "))
      populateWithRoot(root);
  }

  private void populateWithRoot(String root) {
    Response<List<GetValue>> keyValuesResponse = (token == null) ?
        consulClient.getKVValues(root) : consulClient.getKVValues(root, token);

    if (keyValuesResponse.getValue() == null)
      return;

    for (GetValue gv : keyValuesResponse.getValue()) {
      String decodedValue = gv.getDecodedValue();
      if (decodedValue == null)
        continue;

      String rootReplacement = (root.endsWith("/")) ?
          root : root + "/";

      String key = gv.getKey()
          .replaceFirst(rootReplacement, "")
          .replaceAll("/", ".");

      properties.put(key, decodedValue);
    }
  }

  @Override
  public void destroy() {
    properties.clear();
  }

  @Override
  public int getPriority() {
    return 0;
  }

  @Override
  public String resolve(String name) {
    return properties.get(name);
  }

  private String getValue(String name) {
    String result = System.getProperty(name);
    if (result != null)
      return result;
    return System.getenv(name);
  }
}
