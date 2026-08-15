package com.williamhsieh.financeapp.repository.region;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.williamhsieh.financeapp.entity.region.Language;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    List<Language> findAllByActiveTrueAndDeletedDateIsNullOrderByNameAsc();

    Optional<Language> findByIdAndActiveTrueAndDeletedDateIsNull(Long id);
}
