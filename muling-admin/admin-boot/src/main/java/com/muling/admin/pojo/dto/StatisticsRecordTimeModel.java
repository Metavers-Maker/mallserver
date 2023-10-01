package com.muling.admin.pojo.dto;

import com.muling.common.util.DateUtil;

import java.util.Date;

import static com.muling.admin.service.impl.StatisticsRecordHelper.YYYY_MM_DD_HH_MM_SS;

public class StatisticsRecordTimeModel {


    private Date dayBeginTimeDate;

    private Date monthBeginTimeDate;


    private Date dayEndTimeDate;

    private Date monthEndTimeDate;

    private String dayBeginTime;

    private String monthBeginTime;

    private String dayEndTime;

    private String monthEndTime;

    public Date getDayEndTimeDate() {
        return dayEndTimeDate;
    }

    public StatisticsRecordTimeModel setDayEndTimeDate(Date dayEndTimeDate) {
        this.dayEndTimeDate = dayEndTimeDate;
        this.dayEndTime = DateUtil.format(dayEndTimeDate, YYYY_MM_DD_HH_MM_SS);
        return this;
    }

    public Date getMonthEndTimeDate() {
        return monthEndTimeDate;
    }

    public StatisticsRecordTimeModel setMonthEndTimeDate(Date monthEndTimeDate) {
        this.monthEndTimeDate = monthEndTimeDate;
        this.monthEndTime = DateUtil.format(monthEndTimeDate, YYYY_MM_DD_HH_MM_SS);
        return this;
    }

    public String getDayEndTime() {
        return dayEndTime;
    }

    public String getMonthEndTime() {
        return monthEndTime;
    }

    public Date getDayBeginTimeDate() {
        return dayBeginTimeDate;
    }

    public StatisticsRecordTimeModel setDayBeginTimeDate(Date dayBeginTimeDate) {
        this.dayBeginTimeDate = dayBeginTimeDate;
        this.dayBeginTime = DateUtil.format(dayBeginTimeDate, YYYY_MM_DD_HH_MM_SS);
        return this;
    }


    public Date getMonthBeginTimeDate() {
        return monthBeginTimeDate;
    }

    public StatisticsRecordTimeModel setMonthBeginTimeDate(Date monthBeginTimeDate) {
        this.monthBeginTimeDate = monthBeginTimeDate;
        this.monthBeginTime = DateUtil.format(monthBeginTimeDate, YYYY_MM_DD_HH_MM_SS);
        return this;
    }

    public String getDayBeginTime() {
        return dayBeginTime;
    }


    public String getMonthBeginTime() {
        return monthBeginTime;
    }

}
