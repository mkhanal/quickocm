package org.quickocm.processor;

import org.quickocm.model.Field;
import org.quickocm.model.ModelClass;
import org.supercsv.cellprocessor.*;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvCellProcessors {

    private static final String format = "dd/MM/yyyy";
    public static Map<String, CellProcessor> typeMappings = new HashMap<String, CellProcessor>();

    static {
        typeMappings.put("int", new ParseInt());
        typeMappings.put("long", new ParseLong());
        typeMappings.put("boolean", new ParseBool());
        typeMappings.put("double", new ParseDouble());
        typeMappings.put("Date", new StrRegEx("^\\d{1,2}/\\d{1,2}/\\d{4}$", new ParseDate(format))); //second parameter for leniency
        typeMappings.put("String", new Trim());
        typeMappings.put("BigDecimal", new ParseBigDecimal());
    }

    public static List<CellProcessor> getProcessors(ModelClass modelClass, List<String> headers) {
        List<CellProcessor> processors = new ArrayList<CellProcessor>();
        for (String header : headers) {
            Field field = modelClass.findImportFieldWithName(header);
            CellProcessor processor = null;
            if (field != null) {
                processor = chainTypeProcessor(field);
            }
            processors.add(processor);
        }
        return processors;
    }


    private static CellProcessor chainTypeProcessor(Field field) {
        CellProcessor mappedProcessor = typeMappings.get(field.getType());
        return field.isMandatory() ? new NotNull(mappedProcessor) : new Optional(mappedProcessor);
    }
}
