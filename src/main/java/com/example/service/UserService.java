package com.example.service;


import com.example.dao.LoginTicketDao;
import com.example.dao.UserDao;
import com.example.model.LoginTicket;
import com.example.model.User;
import com.example.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    UserDao userDao;


    @Autowired
    LoginTicketDao loginTicketDao;

    public Map<String, String> register(String name, String password){
        Map<String, String> map = new HashMap();

        if (StringUtils.isBlank(name)){
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)){
            map.put("msg", "密码不能为空");
            return map;
        }

        User user = userDao.selectByName(name);
        if (user != null){
            map.put("msg", "该用户已存在");
            return map;
        }

        user = new User();
        user.setName(name);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                        new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password + user.getSalt()));
        userDao.addUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    public Map<String, String> login(String name, String password){
        Map<String, String> map = new HashMap();

        if (StringUtils.isBlank(name)){
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)){
            map.put("msg", "密码不能为空");
            return map;
        }

        User user = userDao.selectByName(name);
        if (user == null){
            map.put("msg", "该用户不存在");
            return map;
        }

        if (!user.getPassword().equals(WendaUtil.MD5(password + user.getSalt()))){
            map.put("msg", "密码错误");
            return map;
        }
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }


    public void logout(String ticket){

        loginTicketDao.updateStatus(ticket, 1);
    }
    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date now = new Date();
        now.setTime(3600 * 24 * 100 * 1000 + now.getTime());
        loginTicket.setExpired(now);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDao.addTicket(loginTicket);
        return loginTicket.getTicket();
    }
    public User getUser(int id){
        return userDao.selectById(id);
    }

    public User selectByName(String name){
        return userDao.selectByName(name);
    }
}

