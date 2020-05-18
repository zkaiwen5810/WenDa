package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//@Controller
public class IndexController {

    @RequestMapping(path = {"/", "index"})
    @ResponseBody
    public String index(){
        return "Index home";
    }


    @RequestMapping(path = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("userId") int userId,
                          @PathVariable("groupId") String groupId,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", required = false) String key){
        return String.format("Profile page of user : %s / %d : %d | %s", groupId, userId, type, key);
    }


    @RequestMapping(path = {"/vm"}, method = {RequestMethod.GET})
    public String template(Model model){
        model.addAttribute("value1", "vvvv1");
        List<String> colors = Arrays.asList(new String[]{"RED", "BLUE", "GREEN"});
        model.addAttribute("colors", colors);

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 4; i++){
            map.put(String.valueOf(i), String.valueOf(i * i));
        }
        model.addAttribute("map", map);
        return "home";
    }

    @RequestMapping(path = {"/request"}, method = {RequestMethod.GET})
    @ResponseBody
    public String request(Model model, HttpServletResponse response,
                          HttpServletRequest request,
                          HttpSession session){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(String.format("%s : %s <br>", name, request.getHeader(name)));
        }
        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()){
                sb.append(String.format("Cookie %s : %s<br>", cookie.getName(), cookie.getValue()));
            }
        }
        sb.append(request.getMethod() + "<br>");
        sb.append(request.getQueryString() + "<br>");
        sb.append(request.getRequestURI() + "<br>");


        response.addHeader("nowcodeId", "helonode");
        return sb.toString();
    }


    @RequestMapping(path = {"/redirect/{code}"}, method = {RequestMethod.GET})
    public RedirectView request(@PathVariable(value = "code") int code){
        RedirectView rd = new RedirectView("/", false);
        if(code == 301){
            rd.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return rd;
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key") String key){
        if ("admin".equals(key)){
            return "hello admin";
        }
        throw new IllegalArgumentException("参数不对");
    }


    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e){
        return "error : " + e.getMessage();
    }
}

