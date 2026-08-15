package com.williamhsieh.financeapp.repository.region;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.williamhsieh.financeapp.entity.region.Timezone;

public interface TimezoneRepository extends JpaRepository<Timezone, Long> {

    Optional<Timezone> findByIdAndDeletedDateIsNull(Long id);
}
