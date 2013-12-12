package org.quickocm.parser;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.quickocm.exception.UploadException;
import org.quickocm.model.DummyImportable;
import org.quickocm.model.DummyRecordHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CsvParserMacRenameIssueTest {

  public static final String ENCODING = "UTF-8";

  private CsvParserMacRenameIssue<DummyImportable> csvParser;
  private DummyRecordHandler recordHandler;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    csvParser = new CsvParserMacRenameIssue();
    recordHandler = new DummyRecordHandler();
  }

  @Test
  public void shouldTrimSpacesFromParsedRecords() throws Exception {
    String csvInput =
      "mandatory string field   , mandatoryIntField\n" +
        " Random1               , 23\n" +
        " Random2                , 25\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes(ENCODING));

    csvParser.process(inputStream, DummyImportable.class, recordHandler);

    List<DummyImportable> importedObjects = recordHandler.importedObjects;
    assertEquals(23, importedObjects.get(0).getMandatoryIntField());
    assertEquals("Random1", importedObjects.get(0).getMandatoryStringField());
    assertEquals(25, importedObjects.get(1).getMandatoryIntField());
    assertEquals("Random2", importedObjects.get(1).getMandatoryStringField());
  }

  @Test
  public void shouldParseFromFileAndInvokeHandler() throws Exception {
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("test.csv");

    csvParser.process(inputStream, DummyImportable.class, recordHandler);
    List<DummyImportable> importedObjects = recordHandler.importedObjects;

    final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
    DummyImportable random1 = new DummyImportable() {
      {
        setMandatoryIntField(23);
        setMandatoryStringField("Random1");
        setOptionalStringField(null);
        setOptionalDateField(DateTime.parse("13/02/1989", dateTimeFormatter).toDate());
      }
    };

    DummyImportable random2 = new DummyImportable() {
      {
        setMandatoryIntField(25);
        setMandatoryStringField("Random2");
        setOptionalStringField("with, comma");
        setOptionalDateField(DateTime.parse("20/2/1986", dateTimeFormatter).toDate());
      }
    };

    assertThat(importedObjects.size(), is(2));
    assertThat(importedObjects.get(0), is(random1));
    assertThat(importedObjects.get(1), is(random2));
  }


  @Test
  public void shouldUseHeadersFromCSVToReportMissingMandatoryData() throws Exception {
    String csvInput =
      "Mandatory String Field,mandatoryIntField\n" +
        "RandomString1,2533\n" +
        ",234\n" +
        "RandomString3,2566\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes(ENCODING));

    expectedEx.expect(equalTo(new UploadException("missing.mandatory", "Mandatory String Field", "record.number.2")));

    csvParser.process(inputStream, DummyImportable.class, recordHandler);
  }

  @Test
  public void shouldThrowExceptionIfCsvAndModelHaveDifferentHeaders() throws Exception {
    String csvInput =
      "Mandatory String Field,mandatoryIntegersField\n" +
        "RandomString1,2533\n" +
        "RamdomString2,234\n" +
        "RandomString3,2566\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes(ENCODING));

    expectedEx.expect(equalTo(new UploadException("error.upload.invalid.header", "[mandatoryintegersfield]")));

    csvParser.process(inputStream, DummyImportable.class, recordHandler);
  }

  @Test
  public void shouldUseHeadersFromCSVToReportIncorrectDataTypeError() throws Exception {
    String csvInput =
      "mandatory string field, mandatoryIntField, OPTIONAL INT FIELD\n" +
        "RandomString1, 2533, \n" +
        "RandomString2, 123, random\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes(ENCODING));

    expectedEx.expect(equalTo(new UploadException("incorrect.data.type", "OPTIONAL INT FIELD", "record.number.2")));

    csvParser.process(inputStream, DummyImportable.class, recordHandler);
  }

  @Test
  public void shouldReportMissingHeaders() throws IOException {
    String csvInput =
      "mandatory string field,\n" +
        "RandomString1,2533\n" +
        "RandomString2,abc123\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes(ENCODING));

    expectedEx.expect(equalTo(new UploadException("error.upload.header.missing", "2")));

    csvParser.process(inputStream, DummyImportable.class, recordHandler);
  }

  @Test
  public void shouldReportMoreColumnDataThanHeaders() throws IOException {
    String csvInput =
      "Mandatory String Field,mandatoryIntField,optionalStringField,OPTIONAL INT FIELD\n" +
        "a,1,,,\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes(ENCODING));

    expectedEx.expect(equalTo(new UploadException("extra.columns.than.headers.found", "Mandatory String Field", "record.number.1")));

    csvParser.process(inputStream, DummyImportable.class, recordHandler);
  }

  @Test
  public void shouldReportFewerColumnDataThanHeaders() throws IOException {
    String csvInput =
      "Mandatory String Field,mandatoryIntField,optionalStringField,OPTIONAL INT FIELD\n" +
        "a,1,\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes(ENCODING));

    expectedEx.expect(equalTo(new UploadException("fewer.columns.than.headers.found", "Mandatory String Field", "record.number.1")));

    csvParser.process(inputStream, DummyImportable.class, recordHandler);
  }

  @Test
  public void shouldThrowErrorIfDateFormatIncorrect() throws IOException {
    String csvInput = "mandatory string field   , mandatoryIntField, OPTIONAL DATE FIELD\n" +
      " Random1               , 23, 99/99/99\n" +
      " Random2                , 25, 19/12/2012\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes(ENCODING));

    expectedEx.expect(equalTo(new UploadException("incorrect.date.format", "OPTIONAL DATE FIELD", "record.number.1")));

    csvParser.process(inputStream, DummyImportable.class, recordHandler);
  }

  @Test
  public void shouldUseUserSpecifiedFieldMapping() throws IOException {
    String csvInput = "mandatory string field   , mandatoryIntField, OPTIONAL NESTED FIELD, OPTIONAL DATE FIELD\n" +
      " Random1               , 23, code1, 19/1/1990\n" +
      " Random2                , 25, code2,\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes(ENCODING));

    csvParser.process(inputStream, DummyImportable.class, recordHandler);
    DummyImportable dummyImportable = recordHandler.importedObjects.get(0);
    assertThat(dummyImportable.getDummyNestedField().getCode(), is("code1"));
  }

  @Test
  public void shouldCallRecordHandlerWithSameSupplementaryInfoSuppliedToCsvParserForAllTheCsvRows() throws IOException {
    String csvInput = "mandatory string field   , mandatoryIntField, OPTIONAL NESTED FIELD, OPTIONAL DATE FIELD\n" +
      " Random1               , 23, code1, 19/1/1990\n" +
      " Random2                , 25, code2,\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes(ENCODING));

    class User {
      public String id;
      public String name;
    }

    User uploader = new User();
    uploader.id = "uploader-id";
    uploader.name = "uploader user";

    Map supplymentaryInfo = new HashMap();
    supplymentaryInfo.put("key", "value");
    supplymentaryInfo.put("source", "csv upload");
    supplymentaryInfo.put("uploader", uploader);

    csvParser = new CsvParserMacRenameIssue<DummyImportable>(supplymentaryInfo);

    csvParser.process(inputStream, DummyImportable.class, recordHandler);

    List<Map> fetchedSupplementaryInfos = recordHandler.supplymentaryMaps;
    assertThat(fetchedSupplementaryInfos.size(), is(2));
    assertThat(fetchedSupplementaryInfos.get(0), sameInstance(fetchedSupplementaryInfos.get(1)));
    assertThat((String) fetchedSupplementaryInfos.get(0).get("key"), is("value"));
    assertThat((String) fetchedSupplementaryInfos.get(0).get("source"), is("csv upload"));
    assertThat((User) fetchedSupplementaryInfos.get(0).get("uploader"), is(uploader));

  }

  @Test
  public void shouldSetMultipleNestedValues() throws IOException {
    String csvInput = "mandatory string field   , mandatoryIntField, entity 1 code, entity 2 code\n" +
      " Random1               , 23, code1-1, code1-2\n" +
      " Random2                , 25, code2-1, code2-2\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes(ENCODING));

    csvParser.process(inputStream, DummyImportable.class, recordHandler);
    DummyImportable dummyImportable = recordHandler.importedObjects.get(0);
    assertThat(dummyImportable.getMultipleNestedFields().getEntityCode1(), is("code1-1"));
    assertThat(dummyImportable.getMultipleNestedFields().getEntityCode2(), is("code1-2"));
  }

}
