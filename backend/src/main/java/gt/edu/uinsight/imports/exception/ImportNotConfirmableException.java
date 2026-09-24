package gt.edu.uinsight.imports.exception;

import gt.edu.uinsight.imports.entity.EstadoImportacion;

public class ImportNotConfirmableException extends RuntimeException {

    public ImportNotConfirmableException(Long importId, EstadoImportacion estadoActual) {
        super("La importación " + importId + " no se puede confirmar porque su estado actual es "
                + estadoActual + " (se requiere VALIDADO).");
    }
}
