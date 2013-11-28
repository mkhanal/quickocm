package org.quickocm.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Predicate;
import org.quickocm.Importable;
import org.quickocm.annotation.ImportField;
import org.quickocm.annotation.ImportFields;
import org.quickocm.exception.UploadException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelClass {
    private Class<? extends Importable> clazz;
    private List<Field> importFields;
    private boolean acceptExtraHeaders = false;

    public ModelClass(Class<? extends Importable> clazz) {
        this.clazz = clazz;
        importFields = fieldsWithImportFieldAnnotation();
    }

    public void validateHeaders(List<String> headers) {
        validateNullHeaders(headers);
        List<String> lowerCaseHeaders = lowerCase(headers);
        if (!acceptExtraHeaders) validateInvalidHeaders(lowerCaseHeaders);
        validateMandatoryFields(lowerCaseHeaders);
    }

    public String[] getFieldNameMappings(String[] headers) {
        List<String> fieldMappings = new ArrayList<String>();
        for (String header : headers) {
            Field importField = findImportFieldWithName(header);
            if (importField != null) {
                String nestedProperty = importField.getNested();
                if (nestedProperty.isEmpty()) {
                    fieldMappings.add(importField.getField().getName());
                } else {
                    fieldMappings.add(importField.getField().getName() + "." + nestedProperty);
                }
            } else {
                fieldMappings.add(null);
            }

        }
        return fieldMappings.toArray(new String[fieldMappings.size()]);
    }

    public Field findImportFieldWithName(final String name) {
        Object result = CollectionUtils.find(importFields, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                Field field = (Field) object;
                return field.hasName(name);
            }
        });
        return (Field) result;
    }

    private List<Field> fieldsWithImportFieldAnnotation() {
        List<java.lang.reflect.Field> fieldsList = Arrays.asList(clazz.getDeclaredFields());
        List<Field> result = new ArrayList<Field>();
        for (java.lang.reflect.Field field : fieldsList) {
            if (field.isAnnotationPresent(ImportField.class)) {
                result.add(new Field(field, field.getAnnotation(ImportField.class)));
            }

            if (field.isAnnotationPresent(ImportFields.class)) {
                final ImportFields importFields = field.getAnnotation(ImportFields.class);
                for (ImportField importField : importFields.importFields()) {
                    result.add(new Field(field, importField));
                }
            }
        }

        return result;
    }

    private void validateNullHeaders(List<String> headers) throws UploadException {
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i) == null) {
                String missingHeaderPosition = i + 1 + "";
                throw new UploadException("error.upload.header.missing", missingHeaderPosition);
            }
        }
    }

    private void validateMandatoryFields(List<String> headers) {
        List<String> missingFields = findMissingFields(headers);

        if (!missingFields.isEmpty()) {
            throw new UploadException("error.upload.missing.mandatory.columns", missingFields.toString());
        }
    }

    private void validateInvalidHeaders(List<String> headers) {
        List<String> fieldNames = getAllImportedFieldNames();
        List invalidHeaders = ListUtils.subtract(headers, lowerCase(fieldNames));
        if (!invalidHeaders.isEmpty()) {
            throw new UploadException("error.upload.invalid.header", invalidHeaders.toString());
        }
    }

    private List<String> findMissingFields(List<String> headers) {
        List<String> missingFields = new ArrayList<String>();
        for (Field field : importFields) {
            if (field.isMandatory()) {
                String fieldName = field.getName();
                if (!headers.contains(fieldName.toLowerCase())) {
                    missingFields.add(fieldName);
                }
            }
        }
        return missingFields;
    }

    private List<String> lowerCase(List<String> headers) {
        List<String> lowerCaseHeaders = new ArrayList<String>();
        for (String header : headers) {
            lowerCaseHeaders.add(header.toLowerCase());
        }
        return lowerCaseHeaders;
    }

    private List<String> getAllImportedFieldNames() {
        List<String> outputCollection = new ArrayList<String>();
        for (Field field : importFields) {
            outputCollection.add(field.getName());
        }
        return outputCollection;
    }

    public Class<? extends Importable> getClazz() {
        return clazz;
    }


    public boolean isAcceptExtraHeaders() {
        return acceptExtraHeaders;
    }

    public void setAcceptExtraHeaders(boolean acceptExtraHeaders) {
        this.acceptExtraHeaders = acceptExtraHeaders;
    }


}
