package org.worldcubeassociation.statistics.service;

import org.worldcubeassociation.statistics.dto.besteverrank.RegionDTO;

import java.time.LocalDate;

public interface ChartRaceService {
    void registerStep(RegionDTO world, String eventId, LocalDate date);

    void removeAll();
}
