package gt.edu.uinsight.imports.util;

public final class ImportLogEvents {

    public static final String IMPORT_STARTED = "IMPORT_STARTED";

    public static final String RESOURCE_CREATED = "RESOURCE_CREATED";
    public static final String IMPORT_VALIDATED = "IMPORT_VALIDATED";
    public static final String IMPORT_CONFIRMED = "IMPORT_CONFIRMED";

    public static final String BUSINESS_RULE_REJECTED = "BUSINESS_RULE_REJECTED";

    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String INVALID_CSV_FILE = "INVALID_CSV_FILE";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    private ImportLogEvents() {
    }
}
