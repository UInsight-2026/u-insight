package gt.edu.uinsight.analytics.individual.dto.response;

public record StudentTrendResponse(
        String studentCode,
        String trend,
        Double averageChange
) {
}
