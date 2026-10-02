//SEMANA 3
package gt.edu.uinsight.report.dto.filter;


public class PageFilter {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;
    public static final String DEFAULT_SORT = "NEWEST";

    private final int page;
    private final int size;
    private final String sort;

    public PageFilter(Integer page, Integer size, String sort) {
        this.page = (page == null) ? 0 : page;
        this.size = (size == null) ? DEFAULT_SIZE : size;
        this.sort = (sort == null || sort.isBlank()) ? DEFAULT_SORT : sort.toUpperCase();
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getSort() {
        return sort;
    }
}