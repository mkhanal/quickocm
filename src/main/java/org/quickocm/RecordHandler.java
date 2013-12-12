package org.quickocm;

import java.util.Map;

/**
 * Implement this class and the implementation should be passed to the <code>CsvParser</code>'s parse method.
 * Each record handler is meant to process one type of object that is maps to a row of the CSV file.
 */

public interface RecordHandler<I> {

  /**
   * execute method is called for each row of the csv after it parses a row into an equivalent object.
   *
   * @param imported  the object parsed equivalent to a csv row
   * @param rowNumber the row number of the line in csv
   * @param supplementaryInfo the extra information passed by the record-parser.
   */
  public void execute(I imported, int rowNumber, Map supplementaryInfo);
}
