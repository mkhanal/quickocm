package org.quickocm.processor;

import org.junit.Test;
import org.quickocm.model.DummyImportable;
import org.quickocm.model.ModelClass;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class CsvCellProcessorsTest {

    @Test
    public void shouldGetProcessors() throws Exception {

        List<String> headers = asList("Mandatory String Field", "mandatoryIntField", "optionalStringField",
                "OPTIONAL NESTED FIELD", "entity 1 code", "entity 2 code");
        final ModelClass modelClass = new ModelClass(DummyImportable.class);

        List<CellProcessor> returnedProcessors = CsvCellProcessors.getProcessors(modelClass, headers);

        assertThat(returnedProcessors.size(), is(6));
    }
}