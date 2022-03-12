package org.worldcubeassociation.statistics.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.worldcubeassociation.statistics.dto.besteverrank.RegionDTO;
import org.worldcubeassociation.statistics.repository.ChartRaceRepository;
import org.worldcubeassociation.statistics.service.ChartRaceService;

import java.time.LocalDate;

@Slf4j
@Service
public class ChartRaceServiceImpl implements ChartRaceService {
    private final ChartRaceRepository chartRaceRepository;

    public ChartRaceServiceImpl(ChartRaceRepository chartRaceRepository) {
        this.chartRaceRepository = chartRaceRepository;
    }

    @Override
    public void registerStep(RegionDTO world, String eventId, LocalDate date) {
        chartRaceRepository.upsert(world, eventId, date);
    }

    @Override
    public void removeAll() {
        log.info("Delete all existing record evolution");
        int deleted = chartRaceRepository.removeAll();
        log.info("{} results deleted", deleted);
    }
}
