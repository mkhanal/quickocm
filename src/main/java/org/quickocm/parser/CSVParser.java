package org.quickocm.parser;

import org.quickocm.Importable;
import org.quickocm.RecordHandler;
import org.quickocm.exception.UploadException;
import org.quickocm.model.ModelClass;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;

import java.io.IOException;
import java.io.InputStream;

public class CSVParser {

    /**
     * Processes a csv input stream for the given type and invokes designated record handler for each field.
     * @param inputStream The input stream of the csv file.
     * @param clazz  The class of which each row is to be made object of.
     * @param recordHandler The Record Handler which is invoked with the parameter of each.
     * @return The row number of the record in csv that has just been processed.
     * @throws UploadException when The csv
     *      1. is empty, 2. is of invalid type 3. has data types that can not be handled
     *      4. has missing mandatory fields 5. has missing headers 6. input stream can not be read
     *      5. has columns with data in invalid format
     */
    public int process(InputStream inputStream, Class clazz, RecordHandler recordHandler)
            throws UploadException {

        CsvBeanReader csvBeanReader = null;
        String[] headers = null;

        try {
            csvBeanReader = new CsvBeanReader(new ModelClass(clazz), inputStream);
            headers = csvBeanReader.getHeaders();
            csvBeanReader.validateHeaders();
            invokeRecordHandlerForEachRow(recordHandler, csvBeanReader);

        } catch (SuperCsvConstraintViolationException e) {
            String error = e.getMessage().contains("^\\d{1,2}/\\d{1,2}/\\d{4}$")
                    ? "incorrect.date.format"
                    : "missing.mandatory";

            throwUploadExceptionForInvalidData(error, headers, e);

        } catch (SuperCsvCellProcessorException processorException) {
            throwUploadExceptionForInvalidData("incorrect.data.type", headers, processorException);
        } catch (SuperCsvException e) {
            String error = csvBeanReader.length() > headers.length
                    ? "extra.columns.than.headers.found"
                    : "fewer.columns.than.headers.found";
            throwUploadExceptionForInvalidData(error, headers, e);
        } catch (IOException e) {
            throw new UploadException(e.getStackTrace().toString());
        }

        return csvBeanReader.getRowNumber() - 1;
    }

    private void invokeRecordHandlerForEachRow(RecordHandler recordHandler, CsvBeanReader csvBeanReader) throws IOException {
        Importable importedModel;
        while ((importedModel = csvBeanReader.read()) != null) {
            recordHandler.execute(importedModel, csvBeanReader.getRowNumber());
        }
    }

    private void throwUploadExceptionForInvalidData(String error, String[] headers, SuperCsvException exception) {
        CsvContext csvContext = exception.getCsvContext();
        String header = headers[csvContext.getColumnNumber() - 1];
        Integer rowNum = csvContext.getRowNumber() - 1;
        throw new UploadException(error, header, "record.number." + rowNum.toString());
    }

}
