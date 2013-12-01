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

    public int process(InputStream inputStream, Class dataTypeClass, RecordHandler recordHandler)
            throws UploadException {

        CsvBeanReader csvBeanReader = null;
        String[] headers = null;

        try {
            csvBeanReader = new CsvBeanReader(new ModelClass(dataTypeClass), inputStream);
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
