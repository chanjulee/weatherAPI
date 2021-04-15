package com.juju.book.springboot.web;

import com.juju.book.springboot.domain.weather.Weather;
import com.juju.book.springboot.service.weather.WeatherService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/weather/{city}")
    public String today(Model model, @PathVariable String city) throws IOException, ParseException {
        int x = 0; int y = 0;
        switch (city) {
            case "용인시":
                x=62; y=120;
                break;
            case "서울시":
                x=60; y=127;
                break;
            case "부산시":
                x=98; y=76;
                break;
            case "제주시":
                x=52; y=38;
                break;
        }
        List<Weather> getWeatherList = weatherService.getWeather(x, y);
        model.addAttribute("city", city); //도시 이름
        model.addAttribute("date", getWeatherList.get(6).getDate()); //조회 날짜
        model.addAttribute("min", getWeatherList.get(6).getTmn()); //최저 기온
        model.addAttribute("max", getWeatherList.get(6).getTmx()); //최고 기온
        model.addAttribute("weathers", getWeatherList); //시간별 날씨
        return "weather";
    }

}
