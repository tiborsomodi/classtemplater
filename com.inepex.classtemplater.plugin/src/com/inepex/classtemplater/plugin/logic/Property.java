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

  /** e.g. "String" */
  String inputType;

  /** e.g. "label" */
  String inputName;

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
   * Gets the getter of th property, e.g. 'String getLabel()'
   * 
   * @return the getter
   */
  public Method getGetter() {

    return this.getter;
  }

  /**
   * Gets the setter of the property, e.g. 'void setLabel(String label)'
   * 
   * @return the setter
   */
  public Method getSetter() {

    return this.setter;
  }

  /**
   * Gets the name of the property e.g. 'label'
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

    if (this.returnType == null) {
      this.returnType = this.getGetter() != null ? getGetter().getReturnType() : getSetterAttribute().getType();
    }
    return this.returnType;
  }

  private Attribute getSetterAttribute() {

    return this.getSetter().getParameters().get(0);
  }

  public String getInputType() {

    if (this.inputName == null) {
      this.inputType = this.getSetter() != null ? getSetterAttribute().getType() : this.getReturnType();
    }
    return this.inputType;
  }

  public String getInputName() {

    if (this.inputName == null) {
      this.inputName = this.getSetter() != null ? getSetterAttribute().getName() : this.name;
    }
    return this.inputName;
  }
}
