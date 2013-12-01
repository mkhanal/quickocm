package org.quickocm.processor;

import org.quickocm.model.ModelClass;
import org.quickocm.processor.transformer.HeaderToProcessorTransformer;
import org.supercsv.cellprocessor.*;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.collect;

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

    public CsvCellProcessors() {

    }

    public CsvCellProcessors(Map<String, CellProcessor> typeMappings) {
        this.typeMappings = typeMappings;
    }

    public List<CellProcessor> getProcessors(final ModelClass modelClass, List<String> headers) {
        List<CellProcessor> processors = new ArrayList<CellProcessor>();

        collect(headers, new HeaderToProcessorTransformer(modelClass, typeMappings), processors);

        return processors;
    }
}
