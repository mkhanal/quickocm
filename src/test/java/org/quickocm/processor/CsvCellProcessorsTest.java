package org.quickocm.processor;

import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.quickocm.model.DummyImportable;
import org.quickocm.model.ModelClass;
import org.quickocm.processor.transformer.HeaderToProcessorTransformer;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvCellProcessorsTest {

    @Mocked
    HeaderToProcessorTransformer headerToProcessorTransformer;
    @Mocked
    CellProcessor mandatoryStringProcessor;
    @Mocked
    CellProcessor mandatoryIntProcessor;
    @Mocked
    CellProcessor optionalStringProcessor;
    @Mocked
    CellProcessor optionalNestedProcessor;
    @Mocked
    CellProcessor entity1Processor;
    @Mocked
    CellProcessor entity2Processor;


    @Test
    public void shouldGetProcessors() throws Exception {

        List<String> headers = asList("Mandatory String Field", "mandatoryIntField", "optionalStringField",
                "OPTIONAL NESTED FIELD", "entity 1 code", "entity 2 code");
        final ModelClass modelClass = new ModelClass(DummyImportable.class);

        final Map<String, CellProcessor> typeMappings = new HashMap<String, CellProcessor>();
        List<CellProcessor> testProcessors = asList(mandatoryStringProcessor, mandatoryIntProcessor,
                optionalStringProcessor, optionalNestedProcessor, entity1Processor, entity2Processor);


        new Expectations() {
            {
                new HeaderToProcessorTransformer(modelClass, typeMappings);
                result = headerToProcessorTransformer;
            }
        };

        new NonStrictExpectations() {
            {
                headerToProcessorTransformer.transform("Mandatory String Field");
                result = mandatoryStringProcessor;
                headerToProcessorTransformer.transform("mandatoryIntField");
                result = mandatoryIntProcessor;
                headerToProcessorTransformer.transform("optionalStringField");
                result = optionalStringProcessor;
                headerToProcessorTransformer.transform("OPTIONAL NESTED FIELD");
                result = optionalNestedProcessor;
                headerToProcessorTransformer.transform("entity 1 code");
                result = entity1Processor;
                headerToProcessorTransformer.transform("entity 2 code");
                result = entity2Processor;

            }
        };

        List<CellProcessor> returnedProcessors = new CsvCellProcessors(typeMappings).getProcessors(modelClass, headers);

        assertThat(returnedProcessors.size(), is(6));
        assertTrue(CollectionUtils.isEqualCollection(returnedProcessors, testProcessors));

    }
}