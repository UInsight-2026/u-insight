package com.uinsight.academic.section.repository;

import com.uinsight.academic.section.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    boolean existsByCode(String code);

    Optional<Section> findByCode(String code);
}