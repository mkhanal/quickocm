package org.quickocm;

/**
 * Implement this class and the implementation should be passed to the <code>CsvParser</code>'s parse method.
 * Each record handler is meant to process one type of object that is maps to a row of the CSV file.
 */

public interface RecordHandler<I extends Importable> {

  /**
   * execute method is called for each row of the csv after it parses a row into an Importable type of instance.
   *
   * @param importable the object parsed equivalent to a csv row
   * @param rowNumber the row number of the line in csv
   */
  public void execute(I importable, int rowNumber);
}
