package com.juju.book.springboot.web;

import com.juju.book.springboot.service.posts.WeatherService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final WeatherService weatherService;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/today")
    public String today(Model model) throws IOException, ParseException {
        Calendar cal = Calendar.getInstance();
        DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
        String tempDate = sdFormat.format(cal.getTime());
        System.out.println("hahaha"+tempDate);

        model.addAttribute("weathers", weatherService.getWeather());
        return "today";
    }

}
