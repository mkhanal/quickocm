package org.quickocm.model;

import org.quickocm.annotation.ImportField;

public class Field {
    java.lang.reflect.Field field;
    private boolean mandatory;
    private String name;
    private String nested;
    private String type;

    public Field(java.lang.reflect.Field field, ImportField annotation) {
        this.field = field;
        this.mandatory = annotation.mandatory();
        this.name = annotation.name().isEmpty() ? field.getName() : annotation.name();
        this.nested = annotation.nested();
        this.type = annotation.type();
    }

    public boolean hasName(String name) {
        return this.name.equalsIgnoreCase(name);
    }

    public java.lang.reflect.Field getField() {
        return field;
    }

    public void setField(java.lang.reflect.Field field) {
        this.field = field;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNested() {
        return nested;
    }

    public void setNested(String nested) {
        this.nested = nested;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
