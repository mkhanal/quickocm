package org.quickocm.model;

import org.quickocm.Importable;
import org.quickocm.RecordHandler;

import java.util.ArrayList;
import java.util.List;

public class DummyRecordHandler implements RecordHandler<DummyImportable> {

    private List<Importable> importedObjects = new ArrayList<Importable>();

    @Override
    public void execute(DummyImportable importable, int rowNumber) {
        this.importedObjects.add(importable);
    }

    public List<Importable> getImportedObjects() {
        return importedObjects;
    }
}
