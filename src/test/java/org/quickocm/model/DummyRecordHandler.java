package org.quickocm.model;

import org.quickocm.RecordHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DummyRecordHandler implements RecordHandler<DummyImportable> {

  public List<DummyImportable> importedObjects = new ArrayList<DummyImportable>();
  public List<Map> supplymentaryMaps = new ArrayList<Map>();

  @Override
  public void execute(DummyImportable importable, int rowNumber, Map supplementaryMap) {
    importedObjects.add(importable);
    supplymentaryMaps.add(supplementaryMap);
  }
}
