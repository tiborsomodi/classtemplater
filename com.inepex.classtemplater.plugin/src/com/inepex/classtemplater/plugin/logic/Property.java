package com.inepex.classtemplater.plugin.logic;

/**
 * The Class Property.
 *
 * @author Eugen Eisler
 */
public class Property {

  /** e.g. String getLabel() */
  Method getter;

  /** e.g. void setLabel(String label) */
  Method setter;

  /** e.g. "label" */
  String name;

  /** e.g. "String" */
  String returnType;

  /** e.g. "String label" */
  Attribute inputAttribute;

  /**
   * Instantiates a new property.
   *
   * @param name the name
   */
  public Property(String name) {

    super();
    this.name = name;
  }

  /**
   * Gets the getter.
   *
   * @return the getter
   */
  public Method getGetter() {

    return this.getter;
  }

  /**
   * Gets the setter.
   *
   * @return the setter
   */
  public Method getSetter() {

    return this.setter;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {

    return this.name;
  }

  /**
   * Sets the getter.
   *
   * @param getter the new getter
   */
  public void setGetter(Method getter) {

    this.getter = getter;
  }

  /**
   * Sets the setter.
   *
   * @param setter the new setter
   */
  public void setSetter(Method setter) {

    this.setter = setter;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {

    this.name = name;
  }

  /**
   * Gets the return type.
   *
   * @return the return type
   */
  public String getReturnType() {

    return this.returnType;
  }

  /**
   * Sets the return type.
   *
   * @param returnType the new return type
   */
  public void setReturnType(String returnType) {

    this.returnType = returnType;
  }

  /**
   * Gets the input attribute.
   *
   * @return the input attribute
   */
  public Attribute getInputAttribute() {

    return this.inputAttribute;
  }

  /**
   * Sets the input attribute.
   *
   * @param inputAttribute the new input attribute
   */
  public void setInputAttribute(Attribute inputAttribute) {

    this.inputAttribute = inputAttribute;
  }

}
