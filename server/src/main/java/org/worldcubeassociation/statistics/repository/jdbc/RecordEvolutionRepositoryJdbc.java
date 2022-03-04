package org.worldcubeassociation.statistics.repository.jdbc;

import org.worldcubeassociation.statistics.dto.besteverrank.RegionDTO;

import java.time.LocalDate;

public interface RecordEvolutionRepositoryJdbc {
    void upsert(RegionDTO region, LocalDate today);

    int removeAll();
}
