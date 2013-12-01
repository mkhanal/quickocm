package org.quickocm.processor.transformer;

import org.apache.commons.collections.Transformer;
import org.quickocm.model.Field;
import org.quickocm.model.ModelClass;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.Map;

public class HeaderToProcessorTransformer implements Transformer {
    private final ModelClass modelClass;
    private Map<String, CellProcessor> typeMappings;

    public HeaderToProcessorTransformer(ModelClass modelClass, Map<String, CellProcessor> typeMappings) {
        this.modelClass = modelClass;
        this.typeMappings = typeMappings;
    }

    @Override
    public CellProcessor transform(Object headerObject) {
        String header = (String) headerObject;
        Field field = modelClass.findImportFieldWithName(header);

        if (field == null) return null;

        CellProcessor mappedProcessor = typeMappings.get(field.getType());
        return field.isMandatory() ? new NotNull(mappedProcessor) : new Optional(mappedProcessor);
    }

}