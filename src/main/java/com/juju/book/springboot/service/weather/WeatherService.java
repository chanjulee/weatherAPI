package com.juju.book.springboot.service.weather;

import com.juju.book.springboot.domain.weather.Weather;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WeatherService {

    public List<Weather> getWeather(int x, int y) throws IOException, ParseException {

/*
        오늘 날짜 받아오기
*/
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1); //어제 날짜 기준
        DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
        String tempDate = sdFormat.format(cal.getTime());
        String tempTime = "2359"; //전날 23시 부터 조회 하면 오늘과 내일의 날씨 알 수 있음!

/*
        JSON데이터를 요청하는 URLstr을 만들기
*/
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst";
        String serviceKey = "KOLCxiL+ohja+r8cBbT6I3nUIAKGQItSQ/8awh5oyOwXUFDYUB9vlHZWLGLORbZrjQwzNV2VKoqCkRGZs28IIg==";
        String pageNo = "1";
        String numOfRows = "250"; // 한 페이지 결과 수
        String data_type = "JSON"; // 타입 xml, json 등등 ...
        String nx = Integer.toString(x); // 위도
        String ny = Integer.toString(y); // 경도

        StringBuilder urlBuilder = new StringBuilder(apiUrl); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + URLEncoder.encode(serviceKey, "UTF-8")); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode(data_type, "UTF-8")); /*JSON*/
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(tempDate, "UTF-8")); /*현재 날짜*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(tempTime, "UTF-8")); /*06시 발표(정시단위)*/
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));

/*
        Get 방식으로 전송하여 파라미터 받아 오자!
*/
        URL url = new URL(urlBuilder.toString());
        System.out.println(url); //url 주소
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String result = sb.toString();
        System.out.println(sb.toString());

/*
        JSON 파일 파싱 작업
*/
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = (JSONObject) jsonParser.parse(result);
        JSONObject parse_response = (JSONObject) jsonObj.get("response"); //response 키를 가지고 데이터 파싱
        JSONObject parse_body = (JSONObject) parse_response.get("body");// response 로부터 body 찾기
        JSONObject parse_items = (JSONObject) parse_body.get("items");// body 로부터 items 찾기
        JSONArray parse_item = (JSONArray) parse_items.get("item");//items 로부터 item 배열 받아오기

        String category; // 기준 날짜와 기준시간을 VillageWeather 객체에 저장합니다.
        String dateTemp = "";
        String timeTemp = "";
        String popTemp = "";
        String skyTemp = "";
        String t3hTemp = "";
        String tmnTemp = "100"; //아침 최저기온
        String tmxTemp = "-100"; //낮 최고기온

        JSONObject item;
        List<Weather> weatherList = new ArrayList<>();
        for (int i = 0; i < parse_item.size(); i++) {
            item = (JSONObject) parse_item.get(i);

            category = (String) item.get("category"); //item 카테고리
            String fcstDate = (String) item.get("fcstDate"); //측정 날짜
            String fcstTime = (String) item.get("fcstTime"); //측정 시간
            String fcstValue = (String) item.get("fcstValue"); //item 값

            //오늘 날씨만 출력하자 //내일로 넘어가면 break
            if (!dateTemp.equals(fcstDate.toString())) {
                if (!dateTemp.equals("")) {
                    Weather weatherTemp = new Weather();
                    weatherTemp.setDate(dateTemp);
                    weatherTemp.setTime(timeTemp);
                    weatherTemp.setPop(popTemp);
                    weatherTemp.setSky(skyTemp);
                    weatherTemp.setT3h(t3hTemp);
                    weatherTemp.setTmn(tmnTemp);
                    weatherTemp.setTmx(tmxTemp);
                    weatherList.add(weatherTemp);
                    break;
                }
            }

            //시간 바뀔 때마다 배열에 저장
            if (!timeTemp.equals(fcstTime)) {
                if (!timeTemp.equals("")) {
                    Weather weatherTemp = new Weather();
                    weatherTemp.setDate(dateTemp);
                    weatherTemp.setTime(timeTemp);
                    weatherTemp.setPop(popTemp);
                    weatherTemp.setSky(skyTemp);
                    weatherTemp.setT3h(t3hTemp);
                    weatherTemp.setTmn(tmnTemp);
                    weatherTemp.setTmx(tmxTemp);
                    weatherList.add(weatherTemp);
                }
                dateTemp = fcstDate;
                timeTemp = fcstTime;
            }

            switch (category) {
                case "POP": //강수 확률
                    popTemp = fcstValue + "%";
                    break;
                case "SKY": //하늘 상태
                    int code = Integer.parseInt(fcstValue);
                    if (code == 1) {
                        skyTemp = "맑음";
                    } else if (code == 3) {
                        skyTemp = "구름많음";
                    } else if (code == 4) {
                        skyTemp = "흐림";
                    }
                    break;
                case "T3H": //기온
                    t3hTemp = fcstValue + "°C";
                    break;
                case "TMN": //최저기온
                    if (Float.parseFloat(fcstValue) < Float.parseFloat(tmnTemp)) {
                        tmnTemp = fcstValue;
                    }
                    break;
                case "TMX": //최고기온
                    if (Float.parseFloat(fcstValue) > Float.parseFloat(tmxTemp)) {
                        tmxTemp = fcstValue;
                    }
                    break;
            }
        }
        return weatherList;

    }

}
