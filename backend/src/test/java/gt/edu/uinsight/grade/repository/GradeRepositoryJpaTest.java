package gt.edu.uinsight.grade.repository;

import gt.edu.uinsight.evaluation.entity.Evaluation;
import gt.edu.uinsight.grade.model.Grade;
import gt.edu.uinsight.grade.model.GradeStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GradeRepositoryJpaTest {

    @Test
    void mapsCanonicalGradeTableWithoutPersistedStatusAndFindsGradesByEvaluationSection() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.connection.url", "jdbc:h2:mem:grade-canonical;DB_CLOSE_DELAY=-1")
                .applySetting("hibernate.connection.username", "sa")
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                .build();

        try (SessionFactory sessionFactory = new MetadataSources(registry)
                .addAnnotatedClass(Grade.class)
                .addAnnotatedClass(Evaluation.class)
                .buildMetadata()
                .buildSessionFactory()) {
            Long evaluationId;
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                Evaluation evaluation = new Evaluation(41L, "Parcial", "EXAM",
                        LocalDate.of(2026, 10, 14), new BigDecimal("100.00"),
                        new BigDecimal("30.00"), "ACTIVE");
                session.persist(evaluation);
                evaluationId = evaluation.getId();
                session.persist(new Grade(evaluationId, 11L, new BigDecimal("87.50"),
                        LocalDateTime.now()));
                session.getTransaction().commit();
            }

            try (Session session = sessionFactory.openSession()) {
                List<Grade> byStudent = session.createQuery(
                        "from Grade g where g.studentId = :studentId", Grade.class)
                        .setParameter("studentId", 11L)
                        .getResultList();
                List<Grade> bySection = session.createQuery("""
                        select g from Grade g
                        where g.evaluationId in (
                            select e.id from Evaluation e where e.sectionId = :sectionId
                        )
                        """, Grade.class)
                        .setParameter("sectionId", 41L)
                        .getResultList();

                assertThat(byStudent).singleElement()
                        .satisfies(grade -> assertThat(grade.getScore()).isEqualByComparingTo("87.50"));
                assertThat(bySection).hasSize(1);
                assertThat(bySection.getFirst().getEvaluationId()).isEqualTo(evaluationId);
                Grade updatedGrade = byStudent.getFirst();
                updatedGrade.setScore(new BigDecimal("90.00"));
                session.beginTransaction();
                session.merge(updatedGrade);
                session.getTransaction().commit();
            }

            try (Session session = sessionFactory.openSession()) {
                Grade updatedGrade = session.createQuery(
                        "from Grade g where g.studentId = :studentId", Grade.class)
                        .setParameter("studentId", 11L)
                        .getSingleResult();
                assertThat(updatedGrade.getStatus()).isEqualTo(GradeStatus.UPDATED);
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
