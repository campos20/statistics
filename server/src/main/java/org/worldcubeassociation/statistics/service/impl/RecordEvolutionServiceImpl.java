package org.worldcubeassociation.statistics.service.impl;

import org.springframework.stereotype.Service;
import org.worldcubeassociation.statistics.dto.besteverrank.RegionDTO;
import org.worldcubeassociation.statistics.service.RecordEvolutionService;

import java.time.LocalDate;

@Service
public class RecordEvolutionServiceImpl implements RecordEvolutionService {
    public RecordEvolutionServiceImpl(){

    }
    @Override
    public void registerEvolution(RegionDTO region, LocalDate today) {

    }
}
