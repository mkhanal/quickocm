package org.quickocm;

public interface RecordHandler<I extends Importable> {
    public void execute(I importable, int rowNumber);
}
