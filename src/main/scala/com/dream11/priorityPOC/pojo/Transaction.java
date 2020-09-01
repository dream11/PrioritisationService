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
public class Transaction {

    @QuerySqlField(index = true)
    private String messageId;
    private JobType jobType;
    private Source source;
    private Integer executionGuaranteed;
    @QuerySqlField(index = true)
    private Integer priorityLevel;
}
