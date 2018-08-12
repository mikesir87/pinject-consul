# pinject-consul

This project extends [Pinject](https://github.com/soulwing/pinject) by providing a `PropertyResolver` that resolves properties using [Hashicorp Consul](https://consul.io).

## Installation

Add this to your `pom.xml` dependencies. That's it! Pinject will start using the resolver automatically. 

```xml
<dependency>
  <groupId>io.mikesir87</groupId>
  <artifactId>pinject-consul</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Configuration

### Configuring the property resolver

The following properties are needed to configure the property resolver. Properties can be provided either through environment variables or system properties. If both are set, system properties take precedence.

- `PINJECT_CONSUL_HOST` - the hostname for the Consul service
- `PINJECT_CONSUL_TREE_ROOT` - a path to the root of properties. See example below. Multiple paths can be provided using a space. Each path will override any values set in a previous path.
- `PINJECT_CONSUL_TOKEN` - an optional token to be used in the request (if using ACLs)


### Defining properties in Consul

Pinject looks up names using a path that consisting of the fully qualified path name, ending with the property. The ConsulPropertyProvider uses the same approach. Each segment in the path is repesented by a folder, with the final key containing the value. Here is an example.

Imagine we have the following Java class:

```java
package io.mikesir87.app;

import javax.inject.Inject;
import org.soulwing.cdi.properties.Property;

public class Example {
  
  @Inject
  @Property
  private String message;
    
  @Inject
  @Property
  private String name;
}
```

To set the `message` property, I would have a key located at `io/mikesir87/app/Example/message`. If it's value was "Hello world", that value would be injected into the property.


## Understanding `PINJECT_CONSUL_TREE_ROOT`

For each of the examples below, let's pretend I have an application that has properties that are shared across all deployment environments. But, I also want to support overrides unique to a specific environment. My Consul tree structure might look something like this:

```text
  - example-app/
    - shared/io/mikesir87/app/Example/message
    - dev/io/mikesir87/app/Example/name
    - staging/io/mikesir87/app/Example/name
    - prod/io/mikesir87/app/Example/
      - message
      - name
``` 

You'll see that the `message` is defined in the `shared`, but each environment defines it's own `name` value. Prod sets both `message` and `name`.

For the dev tier, I can set the value of `PINJECT_CONSUL_TREE_ROOT` as `example-app/shared example-app/dev`. When the property resolver starts, it will first load the entire tree for `example-app/shared`. Then, it'll load and overlay any additional properties and overrides from `example-app/dev`. In this case, it will use `message` from `example-app/shared` and `name` from `example-app/dev`.

For production, if I set `PINJECT_CONSUL_TREE_ROOT` to `example-app/shared example-app/prod`, the values for `message` and `name` will be the values defined in the `example-app/prod` tree, as they are both overriding the values provided in `example-app/shared`.


## Contributing

Find an issue? Have a suggestion or request? Create an issue and start the conversation.

Developing locally should be a fairly straight forward process. Clone the project and load it into your favorite IDE. 

**Note:** running the integration tests (which are on by default) requires Docker to be installed and running. That's because the test starts a Consul container, populates it, and uses it for tests.
