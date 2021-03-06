package org.quickocm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation marks that a field is to mapped from a row's field in the csv file
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportField {
  /**
   * Marks if the field is mandatory or not.
   * The default value is false
   */
  boolean mandatory() default false;

  /**
   * States the data type of a field. By default it is <code>String</code>
   * Each data type needs to have a handler registered.
   *
   * @return
   */
  String type() default "String";

  /**
   * The name of the header that should be mapped to this name
   *
   * @return
   */
  String name() default "";

  /**
   * Reflects if the property is a direct setter for this object, or a nested field
   *
   * @return
   */
  String nested() default "";
}
