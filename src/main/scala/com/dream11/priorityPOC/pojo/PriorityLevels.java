package com.dream11.priorityPOC.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PriorityLevels {
    @QuerySqlField(index = true)
    private int priorityLevel;
    @QuerySqlField(index = true)
    private double lowerBound;
    @QuerySqlField(index = true)
    private double upperBound;
}
