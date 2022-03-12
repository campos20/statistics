package org.worldcubeassociation.statistics.repository.jdbc.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.worldcubeassociation.statistics.dto.besteverrank.Competitor;
import org.worldcubeassociation.statistics.dto.besteverrank.RegionDTO;
import org.worldcubeassociation.statistics.model.ChartRaceData;
import org.worldcubeassociation.statistics.repository.jdbc.ChartRaceRepositoryJdbc;
import org.worldcubeassociation.statistics.util.JsonUtil;
import org.worldcubeassociation.statistics.util.StatisticsUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ChartRaceRepositoryJdbcImpl implements ChartRaceRepositoryJdbc {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final JsonUtil jsonUtil;

    // Up to 15 competitors
    private static final int LIMIT = 15;

    public ChartRaceRepositoryJdbcImpl(NamedParameterJdbcTemplate namedJdbcTemplate, JsonUtil jsonUtil) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.jsonUtil = jsonUtil;
    }

    @Override
    @Transactional
    public void upsert(RegionDTO region, String eventId, LocalDate today) {
        MapSqlParameterSource paramsList = new MapSqlParameterSource().addValue(ChartRaceData.Fields.EVENT_ID.name(), eventId).addValue(ChartRaceData.Fields.DATA.name(), jsonUtil.convertToJson(workData(region, today)));

        namedJdbcTemplate.update(StatisticsUtil.getQuery("chartrace/upsert"), paramsList);
    }

    @Override
    public int removeAll() {
        return namedJdbcTemplate.update(StatisticsUtil.getQuery("chartrace/removeAll"), new HashMap<>());
    }

    private Map<String, Object> workData(RegionDTO region, LocalDate today) {
        Map<String, Object> result = new HashMap<>();
        result.put("date", today.toString());
        List<Competitor> currentCompetitors = region.getCompetitors().stream().sorted(Comparator.comparing(a -> a.getSingle().getCurrent().getRank())).collect(Collectors.toList());
        List<Map<String, Object>> competitors = new ArrayList<>();
        for (int i = 0; i < LIMIT && i < currentCompetitors.size(); i++) {
            Map<String, Object> competitor = new HashMap<>();
            competitor.put("wcaId", currentCompetitors.get(i).getWcaId());
            competitor.put("single", currentCompetitors.get(i).getSingle().getCurrent().getResult());
            competitors.add(competitor);
        }
        result.put("competitors", competitors);
        return result;
    }
}
