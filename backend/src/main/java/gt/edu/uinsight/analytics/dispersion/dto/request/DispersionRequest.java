package gt.edu.uinsight.analytics.dispersion.dto.request;

import java.util.List;

public class DispersionRequest {

    private Long sectionId;
    private List<Long> studentIds;

    public DispersionRequest() {
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }
}
