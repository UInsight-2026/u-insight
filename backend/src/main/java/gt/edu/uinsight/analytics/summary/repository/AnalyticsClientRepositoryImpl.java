package gt.edu.uinsight.analytics.summary.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import gt.edu.uinsight.analytics.summary.dto.external.CentralTendencyData;
import gt.edu.uinsight.analytics.summary.dto.external.DispersionData;
import gt.edu.uinsight.analytics.summary.dto.external.PositionData;
import gt.edu.uinsight.analytics.summary.dto.external.StudentComparisonData;
import gt.edu.uinsight.analytics.summary.dto.external.TrendData;

@Repository
public class AnalyticsClientRepositoryImpl implements AnalyticsClientRepository {
    @Override
    public CentralTendencyData getCentralTendency(Long sectionId){
        CentralTendencyData data = new CentralTendencyData();
        data.setSectionId(sectionId);
        data.setSampleSize(30);
        data.setMean(72.5);
        data.setMedian(73.0);
        data.setMode(List.of(70.0, 75.0));
        return data;
    }

    @Override
    public PositionData getPosition(Long sectionId){
        PositionData data = new PositionData();
        data.setSectionId(sectionId);
        data.setSampleSize(30);
        data.setPercentiles(Map.of(
            "25", 65.0,
            "50", 73.0,
            "75", 82.5,
            "100", 100.2
        ));
        PositionData.Quartiles quartiles = new PositionData.Quartiles();
        quartiles.setQ1(65.0);
        quartiles.setQ2(73.0);
        quartiles.setQ3(82.5);
        quartiles.setQ4(100.2);

        return data;
    }

    @Override
    public DispersionData getDispersion(Long sectionId){
        DispersionData data = new DispersionData();
        data.setMin(45.0);
        data.setMax(98.0);
        data.setRange(53.0);
        data.setVariance(124.6);
        data.setStandarDeviation(11.16);
        return data;
    }

    @Override
    public TrendData getTrend(Long sectionId){
        TrendData data = new TrendData();
        data.setClassification("NEGATIVE");
        data.setAverageChange(-5.0);

        TrendData.Point p1 = new TrendData.Point();
        p1.setLabel("E1");
        p1.setValue(78.0);

        TrendData.Point p2 = new TrendData.Point();
        p2.setLabel("E2");
        p2.setValue(72.0);

        TrendData.Point p3 = new TrendData.Point();
        p2.setLabel("E3");
        p2.setValue(68.0);

        data.setPoints(List.of(p1, p2, p3));
        return data;
    }

    @Override
    public StudentComparisonData getStudentComparison(Long sectionId){
        StudentComparisonData data = new StudentComparisonData();
        data.setStudentCode("EST-0001");
        data.setStudentAverage(58.0);
        data.setSectionAverage(72.0);
        data.setDifference(-14.0);
        data.setPercentile(20.0);
        data.setTrend("NEGATIVE");
        return data;
    }


}
