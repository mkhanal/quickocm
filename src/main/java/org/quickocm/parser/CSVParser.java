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

    public int process(InputStream inputStream, ModelClass modelClass, RecordHandler recordHandler)
            throws UploadException {

        CsvBeanReader csvBeanReader = null;
        String[] headers = null;

        try {
            csvBeanReader = new CsvBeanReader(modelClass, inputStream);
            headers = csvBeanReader.getHeaders();
            csvBeanReader.validateHeaders();
            Importable importedModel;

            while ((importedModel = csvBeanReader.readWithCellProcessors()) != null) {
                recordHandler.execute(importedModel, csvBeanReader.getRowNumber());
            }
        } catch (SuperCsvConstraintViolationException constraintException) {
            if (constraintException.getMessage().contains("^\\d{1,2}/\\d{1,2}/\\d{4}$")) {
                createHeaderException("incorrect.date.format", headers, constraintException);
            }

            createHeaderException("missing.mandatory", headers, constraintException);
        } catch (SuperCsvCellProcessorException processorException) {
            createHeaderException("incorrect.data.type", headers, processorException);
        } catch (SuperCsvException superCsvException) {
            if (csvBeanReader.length() > headers.length) {
                throw new UploadException("incorrect.file.format");
            }

            createDataException("column.do.not.match", headers, superCsvException);
        } catch (IOException e) {
            throw new UploadException(e.getStackTrace().toString());
        }

        return csvBeanReader.getRowNumber() - 1;
    }


    private void createHeaderException(String error, String[] headers, SuperCsvException exception) {
        CsvContext csvContext = exception.getCsvContext();
        String header = headers[csvContext.getColumnNumber() - 1];
        Integer rowNum = csvContext.getRowNumber() - 1;
        throw new UploadException(error, header, "of Record No. ", rowNum.toString());
    }

    private void createDataException(String error, String[] headers, SuperCsvException exception) {
        CsvContext csvContext = exception.getCsvContext();
        Integer rowNum = csvContext.getRowNumber() - 1;
        throw new UploadException(error, headers.toString(), "in Record No. ", rowNum.toString(), csvContext.getRowSource().toString());
    }
}
