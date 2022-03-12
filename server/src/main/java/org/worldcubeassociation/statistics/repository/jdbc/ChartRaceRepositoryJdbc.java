package org.worldcubeassociation.statistics.repository.jdbc;

import org.worldcubeassociation.statistics.dto.besteverrank.RegionDTO;

import java.time.LocalDate;

public interface ChartRaceRepositoryJdbc {
    void upsert(RegionDTO world, String eventId, LocalDate date);

    int removeAll();
}
