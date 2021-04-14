package com.juju.book.springboot.web.dto;

import com.juju.book.springboot.domain.weather.Weather;
import lombok.Getter;

@Getter
public class WeatherListResponseDto {

    private Long id;
    private String date;
    private String time;
    private String pop;//강수확률
    private String sky;//하늘상태
    private String t3h;//기온

    public WeatherListResponseDto(Weather entity) {
        this.id = entity.getId();
        this.date = entity.getDate();
        this.time = entity.getTime();
        this.pop = entity.getPop();
        this.sky = entity.getSky();
        this.t3h = entity.getT3h();
    }

}
