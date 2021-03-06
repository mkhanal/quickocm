package org.quickocm.parser;

import org.quickocm.RecordHandler;
import org.quickocm.exception.UploadException;
import org.quickocm.model.ModelClass;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * This is the API point which initiates csv parsing and invokes <code>RecordHandler</code> for each file
 */
public class CsvParser<I> {


  /**
   * It is used to pass this info to the <code>RecordHandler</code>'s execute method as extra needed information.
   * This can be useful to pass information that the object can not contain or the CSV can not contain but may
   * have to be generated/referred from context. Such an example is to record the user who triggered the CSV parsing
   * as the creator of the record, also useful for populating audit fields.
   * <p/>
   * The same instance is passed to the RecordHandler for all the rows in CSV being parsed.
   * It is upto the record handler to check for thread-safety and shared resource.
   *
   * @see RecordHandler
   */
  private Map supplementaryInfo = new HashMap();

  /**
   * This api creates a CsvParser that has null <code>supplementaryInfo</code>
   */
  public CsvParser() {
    this(null);
  }

  /**
   * Creates a CsvParser and initializes the supplementaryInfo passed
   *
   * @param supplementaryInfo The supplementaryInfo to be passed to the record handler for each row in CSV.
   */
  public CsvParser(Map supplementaryInfo) {
    this.supplementaryInfo = supplementaryInfo;
  }

  /**
   * Processes a csv input stream for the given type and invokes designated record handler for each field.
   *
   * @param inputStream   The input stream of the csv file.
   * @param clazz         The class of which each row is to be made object of.
   * @param recordHandler The Record Handler which is invoked with the parameter of each.
   * @return The row number of the record in csv that has just been processed.
   * @throws UploadException when The csv
   *                         1. is empty,
   *                         2. is of invalid type
   *                         3. has data types that can not be handled
   *                         4. has missing mandatory fields
   *                         5. has missing headers
   *                         6. input stream can not be read
   *                         7. has columns with data in invalid format
   */
  public int process(InputStream inputStream, Class clazz, RecordHandler<I> recordHandler)
    throws UploadException {

    CsvBeanReader csvBeanReader = null;
    String[] headers = null;

    try {
      csvBeanReader = new CsvBeanReader(new ModelClass(clazz), inputStream);
      headers = csvBeanReader.getHeaders();
      csvBeanReader.validateHeaders();
      invokeRecordHandlerForEachRow(recordHandler, csvBeanReader);

    } catch (SuperCsvConstraintViolationException e) {
      throwUploadExceptionForInvalidData(getErrorMessageForMissingFieldOrInvalidDate(e), headers, e);
    } catch (SuperCsvCellProcessorException processorException) {
      throwUploadExceptionForInvalidData("incorrect.data.type", headers, processorException);
    } catch (SuperCsvException e) {
      throwUploadExceptionForInvalidData(getErrorMessageForHeaderCount(csvBeanReader, headers), headers, e);
    } catch (IOException e) {
      throw new UploadException(e.getStackTrace().toString());
    }

    return csvBeanReader.getRowNumber() - 1;
  }

  private String getErrorMessageForMissingFieldOrInvalidDate(SuperCsvConstraintViolationException e) {
    return e.getMessage().contains("^\\d{1,2}/\\d{1,2}/\\d{4}$")
      ? "incorrect.date.format"
      : "missing.mandatory";
  }

  private String getErrorMessageForHeaderCount(CsvBeanReader csvBeanReader, String[] headers) {
    return csvBeanReader.length() > headers.length
      ? "extra.columns.than.headers.found"
      : "fewer.columns.than.headers.found";
  }

  private void invokeRecordHandlerForEachRow(RecordHandler<I> recordHandler, CsvBeanReader<I> csvBeanReader) throws IOException {
    I importedModel;
    while ((importedModel = csvBeanReader.read()) != null) {
      recordHandler.execute(importedModel, csvBeanReader.getRowNumber(), supplementaryInfo);
    }
  }

  private void throwUploadExceptionForInvalidData(String error, String[] headers, SuperCsvException exception) {
    CsvContext csvContext = exception.getCsvContext();
    String header = headers[csvContext.getColumnNumber() - 1];
    Integer rowNum = csvContext.getRowNumber() - 1;
    throw new UploadException(error, header, "record.number." + rowNum.toString());
  }

  public Map getSupplementaryInfo() {
    return supplementaryInfo;
  }

  public void setSupplementaryInfo(Map supplementaryInfo) {
    this.supplementaryInfo = supplementaryInfo;
  }
}
