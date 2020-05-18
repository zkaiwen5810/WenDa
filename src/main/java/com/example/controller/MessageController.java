package com.example.controller;

import com.example.model.*;

import com.example.service.MessageService;

import com.example.service.UserService;
import com.example.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/msg/list"}, method = { RequestMethod.GET })
    public String getConversionList(Model model){
        if (hostHolder.get() == null){
            return "redirect:/reglogin";
        }
        int localUserId = hostHolder.get().getId();
        List<Message> conversionList = messageService.getConversionList(localUserId, 0, 10);
        List<ViewObject> conversions = new ArrayList<>();
        for (Message message : conversionList){
            ViewObject vo = new ViewObject();
            vo.set("conversation", message);
            int targetId = message.getFromId() == localUserId ? message.getToId() : localUserId;
            vo.set("user", userService.getUser(targetId));
            vo.set("unread", messageService.getConversionUnreadCount(localUserId, message.getConversionId()));
            conversions.add(vo);
        }
        model.addAttribute("conversations", conversions);
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = { RequestMethod.GET })
    public String getConversionDetail(Model model, @RequestParam("conversationId") String conversationId){
        try{
            List<Message> messageList = messageService.getConversionDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message message : messageList){
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                vo.set("user", userService.getUser(message.getFromId()));
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
        }catch (Exception e){
            logger.error("获取详情失败" + e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path = {"/msg/addMessage"}, method = { RequestMethod.POST })
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content){
        try {

            if (hostHolder.get() == null){
                return WendaUtil.getJSONString(999, "未登录");
            }

            User user = userService.selectByName(toName);
            if (user == null){
                return WendaUtil.getJSONString(1, "用户不存在");
            }

            Message message = new Message();
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.get().getId());
            message.setToId(user.getId());
            message.setContent(content);
            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);

        } catch (Exception e){
            logger.error("发送消息失败" + e.getMessage());
            return WendaUtil.getJSONString(1, "发信失败");

        }

    }
}
