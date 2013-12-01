package org.quickocm.parser;

import org.quickocm.Importable;
import org.quickocm.model.ModelClass;
import org.quickocm.processor.CsvCellProcessors;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.dozer.CsvDozerBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.Arrays.asList;

public class CsvBeanReader {

    private ModelClass modelClass;
    private CsvDozerBeanReader dozerBeanReader;
    private CellProcessor[] processors;
    private String[] headers;

    public CsvBeanReader(ModelClass modelClass, InputStream inputStream) throws IOException {
        this(modelClass, inputStream, new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE)
                .surroundingSpacesNeedQuotes(true).build());
    }

    public CsvBeanReader(ModelClass modelClass, InputStream inputStream, CsvPreference csvPreference) throws IOException {
        this.modelClass = modelClass;
        configureDozerBeanReader(inputStream, csvPreference);
        configureProcessors();
    }

    public Importable read() throws IOException {
        return dozerBeanReader.read(modelClass.getClazz(), processors);
    }

    public int getRowNumber() {
        return dozerBeanReader.getRowNumber();
    }

    public int length() {
        return dozerBeanReader.length();
    }

    public void validateHeaders() {
        modelClass.validateHeaders(asList(headers));
    }

    public String[] getHeaders() {
        return headers;
    }

    private void configureDozerBeanReader(InputStream inputStream, CsvPreference csvPreference) throws IOException {
        dozerBeanReader = new CsvDozerBeanReader(new BufferedReader(new InputStreamReader(inputStream)), csvPreference);
        headers = dozerBeanReader.getHeader(true);
        String[] mappings = modelClass.getFieldNameMappings(headers);
        dozerBeanReader.configureBeanMapping(modelClass.getClazz(), mappings);
    }

    private void configureProcessors() {
        List<CellProcessor> cellProcessors = new CsvCellProcessors().getProcessors(modelClass, asList(headers));
        processors = cellProcessors.toArray(new CellProcessor[cellProcessors.size()]);
    }
}
