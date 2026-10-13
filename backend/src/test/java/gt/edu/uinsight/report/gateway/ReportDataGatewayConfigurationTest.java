package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.academicperiod.repository.AcademicPeriodRepository;
import gt.edu.uinsight.enrollment.repository.EnrollmentRepository;
import gt.edu.uinsight.report.mock.MockDataGateway;
import gt.edu.uinsight.section.repository.SectionRepository;
import gt.edu.uinsight.teacher.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ReportDataGatewayConfigurationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(MockDataGateway.class, JpaReportDataGateway.class)
            .withBean(SectionRepository.class, () -> mock(SectionRepository.class))
            .withBean(AcademicPeriodRepository.class, () -> mock(AcademicPeriodRepository.class))
            .withBean(TeacherRepository.class, () -> mock(TeacherRepository.class))
            .withBean(EnrollmentRepository.class, () -> mock(EnrollmentRepository.class))
            .withBean(RiskGateway.class, () -> mock(RiskGateway.class));

    @Test
    void sinPropiedadSoloSeInyectaElGatewaySimulado() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(ReportDataGateway.class);
            assertThat(context.getBean(ReportDataGateway.class)).isInstanceOf(MockDataGateway.class);
        });
    }

    @Test
    void modoMockExplicitoNoCreaElGatewayJpa() {
        runner.withPropertyValues("c5.data-source=mock").run(context -> {
            assertThat(context).hasSingleBean(ReportDataGateway.class);
            assertThat(context).doesNotHaveBean(JpaReportDataGateway.class);
        });
    }

    @Test
    void modoJpaArrancaSinElPuertoDeAllanYTieneUnSoloGateway() {
        runner.withPropertyValues("c5.data-source=jpa", "c5.alert-source=none").run(context -> {
            assertThat(context).hasSingleBean(ReportDataGateway.class);
            assertThat(context.getBean(ReportDataGateway.class)).isInstanceOf(JpaReportDataGateway.class);
            assertThat(context.getBean(ReportDataGateway.class).findAlerts(null)).isEmpty();
        });
    }

    @Test
    void modoJpaAceptaElPuertoDeAlertasCuandoSeIntegre() {
        runner.withPropertyValues("c5.data-source=jpa")
                .withBean(AlertQueryPort.class, () -> mock(AlertQueryPort.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(ReportDataGateway.class);
                    assertThat(context).hasSingleBean(AlertQueryPort.class);
                });
    }
}
