package org.quickocm.model;

import org.quickocm.Importable;
import org.quickocm.annotation.ImportField;
import org.quickocm.annotation.ImportFields;

import java.util.Date;

public class DummyImportable implements Importable {

    @ImportField(mandatory = true, name = "Mandatory String Field", type = "String")
    String mandatoryStringField;

    @ImportField(mandatory = true, type = "int")
    int mandatoryIntField;

    @ImportField
    String optionalStringField;

    @ImportField(type = "int", name = "OPTIONAL INT FIELD")
    int optionalIntField;

    @ImportField(type = "Date", name = "OPTIONAL DATE FIELD")
    Date optionalDateField;

    @ImportField(type = "String", name = "OPTIONAL NESTED FIELD", nested = "code")
    DummyNestedField dummyNestedField;

    @ImportFields(importFields = {
            @ImportField(type = "String", name = "entity 1 code", nested = "entityCode1"),
            @ImportField(type = "String", name = "entity 2 code", nested = "entityCode2")})
    DummyNestedField multipleNestedFields;

    String nonAnnotatedField;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DummyImportable)) return false;

        DummyImportable that = (DummyImportable) o;

        if (mandatoryIntField != that.mandatoryIntField) return false;
        if (optionalIntField != that.optionalIntField) return false;
        if (dummyNestedField != null ? !dummyNestedField.equals(that.dummyNestedField) : that.dummyNestedField != null)
            return false;
        if (mandatoryStringField != null ? !mandatoryStringField.equals(that.mandatoryStringField) : that.mandatoryStringField != null)
            return false;
        if (multipleNestedFields != null ? !multipleNestedFields.equals(that.multipleNestedFields) : that.multipleNestedFields != null)
            return false;
        if (nonAnnotatedField != null ? !nonAnnotatedField.equals(that.nonAnnotatedField) : that.nonAnnotatedField != null)
            return false;
        if (optionalDateField != null ? !optionalDateField.equals(that.optionalDateField) : that.optionalDateField != null)
            return false;
        if (optionalStringField != null ? !optionalStringField.equals(that.optionalStringField) : that.optionalStringField != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mandatoryStringField != null ? mandatoryStringField.hashCode() : 0;
        result = 31 * result + mandatoryIntField;
        result = 31 * result + (optionalStringField != null ? optionalStringField.hashCode() : 0);
        result = 31 * result + optionalIntField;
        result = 31 * result + (optionalDateField != null ? optionalDateField.hashCode() : 0);
        result = 31 * result + (dummyNestedField != null ? dummyNestedField.hashCode() : 0);
        result = 31 * result + (multipleNestedFields != null ? multipleNestedFields.hashCode() : 0);
        result = 31 * result + (nonAnnotatedField != null ? nonAnnotatedField.hashCode() : 0);
        return result;
    }

    public String getMandatoryStringField() {
        return mandatoryStringField;
    }

    public void setMandatoryStringField(String mandatoryStringField) {
        this.mandatoryStringField = mandatoryStringField;
    }

    public int getMandatoryIntField() {
        return mandatoryIntField;
    }

    public void setMandatoryIntField(int mandatoryIntField) {
        this.mandatoryIntField = mandatoryIntField;
    }

    public String getOptionalStringField() {
        return optionalStringField;
    }

    public void setOptionalStringField(String optionalStringField) {
        this.optionalStringField = optionalStringField;
    }

    public int getOptionalIntField() {
        return optionalIntField;
    }

    public void setOptionalIntField(int optionalIntField) {
        this.optionalIntField = optionalIntField;
    }

    public Date getOptionalDateField() {
        return optionalDateField;
    }

    public void setOptionalDateField(Date optionalDateField) {
        this.optionalDateField = optionalDateField;
    }

    public DummyNestedField getDummyNestedField() {
        return dummyNestedField;
    }

    public void setDummyNestedField(DummyNestedField dummyNestedField) {
        this.dummyNestedField = dummyNestedField;
    }

    public DummyNestedField getMultipleNestedFields() {
        return multipleNestedFields;
    }

    public void setMultipleNestedFields(DummyNestedField multipleNestedFields) {
        this.multipleNestedFields = multipleNestedFields;
    }

    public String getNonAnnotatedField() {
        return nonAnnotatedField;
    }

    public void setNonAnnotatedField(String nonAnnotatedField) {
        this.nonAnnotatedField = nonAnnotatedField;
    }
}

