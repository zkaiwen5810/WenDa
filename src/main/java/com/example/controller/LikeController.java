package com.example.controller;

import com.example.async.EventModel;
import com.example.async.EventProducer;
import com.example.async.EventType;
import com.example.model.Comment;
import com.example.model.EntityType;
import com.example.model.HostHolder;
import com.example.service.CommentService;
import com.example.service.LikeService;
import com.example.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    CommentService commentService;


    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(@RequestParam(value = "commentId") int commentId){
        if (hostHolder.get() == null){
            return WendaUtil.getJSONString(999);
        }
        Comment comment = commentService.getCommentById(commentId);


        eventProducer.fireEvent(new EventModel().setType(EventType.LIKE).setActorId(hostHolder.get().getId()).setEntityId(commentId).setEntityType(EntityType.ENTITY_COMMENT)
                                .setExt("questionId", String.valueOf(comment.getEntityId()))
                                .setEntityOwnerId(comment.getUserId()));
        long likeCount = likeService.like(hostHolder.get().getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = "/dislike", method = RequestMethod.POST)
    @ResponseBody
    public String dislike(@RequestParam(value = "commentId") int commentId){
        if (hostHolder.get() == null){
            return WendaUtil.getJSONString(999);
        }

        long likeCount = likeService.disLike(hostHolder.get().getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }

}
