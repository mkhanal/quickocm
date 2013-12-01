package org.quickocm.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.quickocm.exception.UploadException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ModelClassTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldThrowEmptyCsvExceptionIfEmptyFile() throws Exception {
        List<String> headers = null;
        ModelClass modelClass = new ModelClass(DummyImportable.class);

        expectedEx.expect(is(new UploadException("error.upload.csv.empty")));
        modelClass.validateHeaders(headers);
    }

    @Test
    public void shouldNotThrowExceptionWhileValidatingHeadersWithMismatchCase() throws UploadException {
        List<String> headers = new ArrayList<String>();
        headers.add("MANDAtory String Field");
        headers.add("mandatoryIntFIELD");
        ModelClass modelClass = new ModelClass(DummyImportable.class);
        modelClass.validateHeaders(headers);
    }

    @Test
    public void shouldThrowExceptionIfHeaderDoesNotHaveCorrespondingFieldInModel() {
        List<String> headers = new ArrayList<String>() {{
            add("not existing field");
            add("mandatory string field");
            add("mandatoryIntField");
        }};

        expectedEx.expect(equalTo(new UploadException("error.upload.invalid.header", "[not existing field]")));

        ModelClass modelClass = new ModelClass(DummyImportable.class);
        modelClass.validateHeaders(headers);
    }

    @Test
    public void shouldThrowExceptionIfHeaderIsNull() {
        List<String> headers = new ArrayList<String>() {{
            add("mandatory string field");
            add(null);
            add("mandatoryIntField");
        }};

        expectedEx.expect(equalTo(new UploadException("error.upload.header.missing", "2")));

        ModelClass modelClass = new ModelClass(DummyImportable.class);
        modelClass.validateHeaders(headers);
    }


    @Test
    public void shouldFindFieldNameGivenTheHeader() {
        List<String> headers = new ArrayList<String>() {{
            add("mandatory string field");
            add("mandatoryIntField");
        }};

        ModelClass modelClass = new ModelClass(DummyImportable.class);
        final String[] mappings = modelClass.getFieldNameMappings(headers.toArray(new String[headers.size()]));

        assertThat(mappings[0], is("mandatoryStringField"));
        assertThat(mappings[1], is("mandatoryIntField"));
    }

    @Test
    public void shouldThrowExceptionForMissingMandatoryHeaders() {
        List<String> headers = new ArrayList<String>() {{
            add("optionalStringField");
        }};

        expectedEx.expect(equalTo(new UploadException("error.upload.missing.mandatory.columns", "[Mandatory String Field, mandatoryIntField]")));

        ModelClass modelClass = new ModelClass(DummyImportable.class);
        modelClass.validateHeaders(headers);
    }
}
