package com.williamhsieh.financeapp.repository.region;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.williamhsieh.financeapp.entity.region.CountryTimezone;

public interface CountryTimezoneRepository extends JpaRepository<CountryTimezone, Long> {

    @Query("""
        select ct
        from CountryTimezone ct
        join fetch ct.timezone t
        where ct.country.id = :countryId
          and ct.deletedDate is null
          and t.deletedDate is null
        order by ct.defaultTimezone desc, t.name asc
        """)
    List<CountryTimezone> findAvailableByCountryId(@Param("countryId") Long countryId);

    @Query("""
        select ct
        from CountryTimezone ct
        join fetch ct.country c
        join fetch ct.timezone t
        where ct.deletedDate is null
          and c.deletedDate is null
          and t.deletedDate is null
        order by c.name asc, ct.defaultTimezone desc, t.name asc
        """)
    List<CountryTimezone> findAllAvailableWithCountryAndTimezone();

    boolean existsByCountry_IdAndTimezone_IdAndDeletedDateIsNull(
      Long countryId,
      Long timezoneId
    );
}
