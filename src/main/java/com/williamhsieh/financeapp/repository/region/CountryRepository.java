package com.williamhsieh.financeapp.repository.region;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.williamhsieh.financeapp.entity.region.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findAllByDeletedDateIsNullOrderByNameAsc();

    Optional<Country> findByIdAndDeletedDateIsNull(Long id);
}
