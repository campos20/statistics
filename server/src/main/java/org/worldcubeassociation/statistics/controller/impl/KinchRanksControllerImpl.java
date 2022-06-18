package org.worldcubeassociation.statistics.controller.impl;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.worldcubeassociation.statistics.controller.KinchRanksController;
import org.worldcubeassociation.statistics.dto.sumofranks.SumOfRanksDto;
import org.worldcubeassociation.statistics.dto.sumofranks.SumOfRanksMetaDto;
import org.worldcubeassociation.statistics.request.sumofranks.SumOfRanksListRequest;
import org.worldcubeassociation.statistics.response.PageResponse;
import org.worldcubeassociation.statistics.response.rank.SumOfRanksResponse;
import org.worldcubeassociation.statistics.service.KinchRanksService;

import java.util.List;

@RestController
@AllArgsConstructor
public class KinchRanksControllerImpl implements KinchRanksController {
    private final KinchRanksService kinchRanksService;

    @Override
    public SumOfRanksResponse generate() {
        return kinchRanksService.generate();
    }

    @Override
    public PageResponse<SumOfRanksDto> list(String resultType, String regionType, String region,
                                            SumOfRanksListRequest request) {
        return kinchRanksService.list(resultType, regionType, region, request);
    }

    @Override
    public List<SumOfRanksMetaDto> meta() {
        return kinchRanksService.meta();
    }
}
