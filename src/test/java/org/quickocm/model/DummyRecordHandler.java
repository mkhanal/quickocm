package org.quickocm.model;

import org.quickocm.RecordHandler;

import java.util.ArrayList;
import java.util.List;

public class DummyRecordHandler implements RecordHandler<DummyImportable> {

  private List<DummyImportable> importedObjects = new ArrayList<DummyImportable>();

  @Override
  public void execute(DummyImportable importable, int rowNumber) {
    this.importedObjects.add(importable);
  }

  public List<DummyImportable> getImportedObjects() {
    return importedObjects;
  }
}
