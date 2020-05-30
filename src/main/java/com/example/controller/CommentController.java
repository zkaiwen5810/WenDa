package com.example.controller;

import com.example.async.EventModel;
import com.example.async.EventProducer;
import com.example.async.EventType;
import com.example.model.Comment;
import com.example.model.EntityType;
import com.example.model.HostHolder;
import com.example.model.Question;
import com.example.service.CommentService;
import com.example.service.QuestionService;
import com.example.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/addComment"}, method = { RequestMethod.POST })
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            if (hostHolder.get() != null) {
                comment.setUserId(hostHolder.get().getId());
            } else {
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
            }
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            commentService.addComment(comment);

            Question q = questionService.selectById(questionId);

            eventProducer.fireEvent(new EventModel().setType(EventType.COMMENT)
                    .setActorId(hostHolder.get().getId())
                    .setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId)
                    .setEntityOwnerId(q.getUserId()));

            int count = commentService.getCommentCount(questionId, EntityType.ENTITY_QUESTION);
            questionService.updateCommentCount(questionId, count);

        } catch (Exception e){
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }
}
