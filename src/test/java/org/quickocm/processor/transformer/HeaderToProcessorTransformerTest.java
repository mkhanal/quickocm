//package org.quickocm.processor.transformer;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.quickocm.model.DummyImportable;
//import org.quickocm.model.ModelClass;
//import org.supercsv.cellprocessor.Optional;
//import org.supercsv.cellprocessor.constraint.NotNull;
//import org.supercsv.cellprocessor.ift.CellProcessor;
//import org.supercsv.util.CsvContext;
//
//import java.util.HashMap;
//
//import static junit.framework.Assert.assertTrue;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.Matchers.nullValue;
//
//public class HeaderToProcessorTransformerTest {
//
//    private HashMap<String, CellProcessor> typeMappings;
//    private MyCellProcessor intCellProcessor;
//    private MyCellProcessor stringCellProcessor;
//    private HeaderToProcessorTransformer headerToProcessorTransformer;
//
//    @Before
//    public void setUp() throws Exception {
//        typeMappings = new HashMap<String, CellProcessor>();
//        intCellProcessor = new MyCellProcessor();
//        stringCellProcessor = new MyCellProcessor();
//        typeMappings.put("int", intCellProcessor);
//        typeMappings.put("String", stringCellProcessor);
//        headerToProcessorTransformer = new HeaderToProcessorTransformer(
//                new ModelClass(DummyImportable.class), typeMappings);
//    }
//
//    @Test
//    public void shouldReturnCorrespondingOptionalProcessorForGivenHeaderForOptionalField() throws Exception {
//        CellProcessor processor = headerToProcessorTransformer.transform("OPTIONAL INT FIELD");
//        assertTrue(processor instanceof Optional);
//
//        processor.execute("abc", new CsvContext(0, 0, 0));
//        assertThat(intCellProcessor.executed, is(true));
//        assertThat(stringCellProcessor.executed, is(false));
//    }
//
//    @Test
//    public void shouldReturnCorrespondingNotNullProcessorForGivenHeaderForMandatoryField() throws Exception {
//        CellProcessor processor = headerToProcessorTransformer.transform("Mandatory String Field");
//        assertTrue(processor instanceof NotNull);
//
//        processor.execute("abc", new CsvContext(0, 0, 0));
//        assertThat(stringCellProcessor.executed, is(true));
//        assertThat(intCellProcessor.executed, is(false));
//    }
//
//    @Test
//    public void shouldReturnNullIfGivenHeaderDoesNotExistAsModelField() throws Exception {
//        assertThat(headerToProcessorTransformer.transform("some random header"), is(nullValue()));
//    }
//
//    private class MyCellProcessor implements CellProcessor {
//        public boolean executed = false;
//
//        @Override
//        public MyCellProcessor execute(Object value, CsvContext context) {
//            executed = true;
//            return this;
//        }
//    }
//}
