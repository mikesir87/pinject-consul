package io.mikesir87.pinject.consul.rules;

import static io.mikesir87.pinject.consul.rules.ConsulPopulatingRule.PROD_TREE_ROOT;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be applied to individual test cases that allows for
 * modification of environment variables. This should occur before the Weld
 * container starts, allowing manipulation of the values used by the property
 * resolver.
 *
 * @author Michael Irwin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnvProperties {

  /**
   * The value to set for the PINJECT_CONSUL_HOST env variable.
   * @return The value to set for the PINJECT_CONSUL_HOST env variable.
   */
  String host() default "localhost";

  /**
   * The value to set for the PINJECT_CONSUL_TREE_ROOT env variable.
   * @return The value to set for the PINJECT_CONSUL_TREE_ROOT env variable.
   */
  String treeRoot() default PROD_TREE_ROOT;
}
