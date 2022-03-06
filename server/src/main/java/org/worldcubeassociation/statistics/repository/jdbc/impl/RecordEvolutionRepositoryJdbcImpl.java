package org.worldcubeassociation.statistics.repository.jdbc.impl;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.worldcubeassociation.statistics.dto.EventDto;
import org.worldcubeassociation.statistics.dto.besteverrank.RegionDTO;
import org.worldcubeassociation.statistics.model.RecordEvolution;
import org.worldcubeassociation.statistics.repository.jdbc.RecordEvolutionRepositoryJdbc;
import org.worldcubeassociation.statistics.util.JsonUtil;
import org.worldcubeassociation.statistics.util.StatisticsUtil;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RecordEvolutionRepositoryJdbcImpl implements RecordEvolutionRepositoryJdbc {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final JsonUtil jsonUtil;

    private static final List<Integer> BEST = List.of(0, 9, 99);

    public RecordEvolutionRepositoryJdbcImpl(NamedParameterJdbcTemplate namedJdbcTemplate, JsonUtil jsonUtil) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.jsonUtil = jsonUtil;
    }

    @Override
    @Transactional
    public void upsert(RegionDTO region, String eventId, LocalDate today) {
        MapSqlParameterSource paramsList = new MapSqlParameterSource().addValue(RecordEvolution.Fields.EVENT_ID.name(), eventId).addValue(RecordEvolution.Fields.EVOLUTION.name(), jsonUtil.convertToJson(workData(region, today)));

        namedJdbcTemplate.update(StatisticsUtil.getQuery("recordevolution/upsert"), paramsList);
    }

    @Override
    public int removeAll() {
        return namedJdbcTemplate.update(StatisticsUtil.getQuery("recordevolution/removeAll"), new HashMap<>());

    }

    @Override
    public List<EventDto> getAvailableEvents() {
        return namedJdbcTemplate
                .query(
                        StatisticsUtil.getQuery("recordevolution/getAvailableEvents"),
                        new MapSqlParameterSource(),
                        JdbcTemplateMapperFactory
                                .newInstance()
                                .newRowMapper(EventDto.class)
                );
    }

    private Map<String, Object> workData(RegionDTO region, LocalDate today) {
        Map<String, Object> result = new HashMap<>();
        result.put("date", today.toString());
        for (int index : BEST) {
            maybeAddValue(index, region.getSingles(), result, "best");
            maybeAddValue(index, region.getAverages(), result, "avg");
        }
        return result;
    }

    private void maybeAddValue(int index, List<Integer> results, Map<String, Object> result, String type) {
        if (results.size() > index) {
            result.put(type + (index + 1), results.get(index));
        }
    }
}
