package org.worldcubeassociation.statistics.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Type;
import org.worldcubeassociation.statistics.dto.chartrace.ChartRaceStepDto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Data
@Entity
@FieldNameConstants(asEnum = true)
@EqualsAndHashCode(callSuper = true)
public class ChartRaceData extends BaseEntity {
    @Id
    @Column(name = "event_id")
    private String eventId;

    @Type(type = "json")
    @Column(columnDefinition = "json", name = "data")
    private List<ChartRaceStepDto> data;
}