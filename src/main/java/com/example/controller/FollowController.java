package com.example.controller;

import com.example.async.EventModel;
import com.example.async.EventProducer;
import com.example.async.EventType;
import com.example.model.*;
import com.example.service.CommentService;
import com.example.service.FollowService;
import com.example.service.QuestionService;
import com.example.service.UserService;
import com.example.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);
    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;


    @RequestMapping(path = {"/followUser"}, method = { RequestMethod.POST })
    @ResponseBody
    public String follow(@RequestParam("userId") int userId){
        if (hostHolder.get() == null){
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.follow(hostHolder.get().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel().setType(EventType.FOLLOW)
                .setActorId(hostHolder.get().getId())
                .setEntityType(EntityType.ENTITY_USER).setEntityId(userId)
                .setEntityOwnerId(userId));

        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweesCount(hostHolder.get().getId(), EntityType.ENTITY_USER)));
    }


    @RequestMapping(path = {"/unfollowUser"}, method = { RequestMethod.POST })
    @ResponseBody
    public String unfollow(@RequestParam("userId") int userId){
        if (hostHolder.get() == null){
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.unfollow(hostHolder.get().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel().setType(EventType.UNFOLLOW)
                .setActorId(hostHolder.get().getId())
                .setEntityType(EntityType.ENTITY_USER).setEntityId(userId)
                .setEntityOwnerId(userId));

        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweesCount(hostHolder.get().getId(), EntityType.ENTITY_USER)));
    }

    @RequestMapping(path = {"/followQuestion"}, method = { RequestMethod.POST })
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        if (hostHolder.get() == null){
            return WendaUtil.getJSONString(999);
        }
        Question q = questionService.selectById(questionId);
        if (q == null){
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.follow(hostHolder.get().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel().setType(EventType.FOLLOW)
                .setActorId(hostHolder.get().getId())
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId)
                .setEntityOwnerId(q.getUserId()));
        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.get().getHeadUrl());
        info.put("name", hostHolder.get().getName());
        info.put("id", hostHolder.get().getId());
        info.put("count", followService.getFolloweesCount(hostHolder.get().getId(), EntityType.ENTITY_QUESTION));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(path = {"/unfollowQuestion"}, method = { RequestMethod.POST })
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        if (hostHolder.get() == null){
            return WendaUtil.getJSONString(999);
        }
        Question q = questionService.selectById(questionId);
        if (q == null){
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.follow(hostHolder.get().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel().setType(EventType.FOLLOW)
                .setActorId(hostHolder.get().getId())
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId)
                .setEntityOwnerId(q.getUserId()));
        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.get().getHeadUrl());
        info.put("name", hostHolder.get().getName());
        info.put("id", hostHolder.get().getId());
        info.put("count", followService.getFolloweesCount(hostHolder.get().getId(), EntityType.ENTITY_QUESTION));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);

    }

    @RequestMapping(path = {"/user/{uid}/followees"}, method = { RequestMethod.GET })
    public String followees(Model model, @PathVariable("uid") int uid){

        List<Integer> followeeIds = followService.getFollowees(uid, EntityType.ENTITY_USER, 0, 10);
        if (hostHolder.get() != null){
            model.addAttribute("followees", getUsersInfo(hostHolder.get().getId(), followeeIds));
        }else{
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweesCount(uid, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(uid));
        return "followees";
    }

    @RequestMapping(path = {"/user/{uid}/followers"}, method = { RequestMethod.GET })
    public String followers(Model model, @PathVariable("uid") int uid){

        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, uid, 0, 10);
        if (hostHolder.get() != null){
            model.addAttribute("followers", getUsersInfo(hostHolder.get().getId(), followerIds));
        }else{
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowersCount(EntityType.ENTITY_USER, uid));
        model.addAttribute("curUser", userService.getUser(uid));
        return "followers";
    }

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds){
        List<ViewObject> usersInfos = new ArrayList<>();
        for (Integer uid : userIds){
            User user = userService.getUser(uid);
            if (user == null){
                continue;
            }

            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("followerCount", followService.getFollowersCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweesCount(uid, EntityType.ENTITY_USER));

            if (localUserId != 0){
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            }else{
                vo.set("followed", false);
            }
            usersInfos.add(vo);
        }
        return usersInfos;
    }
}
