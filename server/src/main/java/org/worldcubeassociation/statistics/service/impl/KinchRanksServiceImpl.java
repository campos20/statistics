package org.worldcubeassociation.statistics.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.worldcubeassociation.statistics.dto.EventDto;
import org.worldcubeassociation.statistics.dto.sumofranks.SumOfRanksDto;
import org.worldcubeassociation.statistics.dto.sumofranks.SumOfRanksMetaDto;
import org.worldcubeassociation.statistics.dto.sumofranks.SumOfRanksRegionDto;
import org.worldcubeassociation.statistics.exception.InvalidParameterException;
import org.worldcubeassociation.statistics.repository.jdbc.KinchRanksRepositoryJdbc;
import org.worldcubeassociation.statistics.request.sumofranks.SumOfRanksListRequest;
import org.worldcubeassociation.statistics.response.PageResponse;
import org.worldcubeassociation.statistics.response.rank.SumOfRanksResponse;
import org.worldcubeassociation.statistics.service.EventService;
import org.worldcubeassociation.statistics.service.KinchRanksService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class KinchRanksServiceImpl implements KinchRanksService {
    private final KinchRanksRepositoryJdbc kinchRanksRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public SumOfRanksResponse generate() {
        log.info("Generate sum of ranks");

        deleteExistingData();

        SumOfRanksResponse response = new SumOfRanksResponse();

        response.setWorldRank(kinchRanksRepository.generateWorldRank());
//        response.setContinentRank(kinchRanksRepository.generateContinentRank());
//        response.setCountryRank(kinchRanksRepository.generateCountryRank());

        log.info("Update regional ranks");
        kinchRanksRepository.updateRanks();

        log.info("Insert meta inserted");
        response.setMeta(kinchRanksRepository.insertMeta());

        return response;
    }

    private void deleteExistingData() {
        log.info("Deleting existing data");

        int deletedSor = kinchRanksRepository.deleteAll();
        log.info("{} SOR deleted", deletedSor);

        int metaDeleted = kinchRanksRepository.deleteAllMeta();
        log.info("{} meta deleted", metaDeleted);
    }

    @Override
    public PageResponse<SumOfRanksDto> list(String resultType, String regionType, String region,
                                            SumOfRanksListRequest request) {
        int page = request.getPage();
        int pageSize = request.getPageSize();
        String wcaId = request.getWcaId();

        if (!StringUtils.isBlank(wcaId)) {
            page = kinchRanksRepository.getWcaIdPage(resultType, regionType, region, pageSize, wcaId)
                    .orElseThrow(() -> new InvalidParameterException(
                            String.format("WCA ID %s not found for region type %s and region %s", wcaId, regionType,
                                    region)));
        }
        List<SumOfRanksDto> sor = kinchRanksRepository.list(resultType, regionType, region, page, pageSize);

        // MySql does a very poor job when orgnizing json_arrayagg. We need to correct it here.
        sor.forEach(s -> s.setEvents(s.getEvents().stream().sorted(Comparator.comparing(e -> e.getEvent().getRank()))
                .collect(Collectors.toList())));

        PageResponse<SumOfRanksDto> response = new PageResponse<>();
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setContent(sor);
        return response;
    }

    @Override
    public List<SumOfRanksMetaDto> meta() {
        List<SumOfRanksMetaDto> response = kinchRanksRepository.getResultTypes();

        // MySql does not a good job when ordering json_arrayagg.
        // We need to make sure that is organized for consistency

        response.forEach(r -> {
            r.setRegionGroups(r.getRegionGroups().stream()
                    .sorted(Comparator.comparing(a -> (a.getRegions().size())))
                    .collect(Collectors.toList()));

            r.getRegionGroups().forEach(rg -> rg.setRegions(rg.getRegions().stream().sorted(Comparator.comparing(
                    SumOfRanksRegionDto::getRegion)).collect(Collectors.toList())));

            r.setAvailableEvents(r.getAvailableEvents().stream().sorted(Comparator.comparing(EventDto::getRank))
                    .collect(Collectors.toList()));
        });

        return response;
    }
}
