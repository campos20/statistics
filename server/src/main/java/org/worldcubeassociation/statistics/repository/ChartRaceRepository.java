package org.worldcubeassociation.statistics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.worldcubeassociation.statistics.model.ChartRaceData;
import org.worldcubeassociation.statistics.repository.jdbc.ChartRaceRepositoryJdbc;

public interface ChartRaceRepository extends JpaRepository<ChartRaceData, String>, ChartRaceRepositoryJdbc {
}
