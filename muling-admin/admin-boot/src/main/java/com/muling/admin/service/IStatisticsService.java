package com.muling.admin.service;


import com.muling.admin.pojo.dto.StatisticsDTO;
import com.muling.common.protocol.StatisticsRequest;

public interface IStatisticsService {

    void calculateFix();

    StatisticsDTO query(StatisticsRequest statisticsRequest);

}
