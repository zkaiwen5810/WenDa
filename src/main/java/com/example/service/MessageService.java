package com.example.service;

import com.example.dao.MessageDao;
import com.example.model.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {


    @Autowired
    MessageDao messageDao;

    @Autowired
    SensitiveService sensitiveService;

    public int addMessage(Message message){
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDao.addMessage(message) > 0 ? message.getId() : 0;
    }

    public List<Message> getConversionDetail(String conversionId, int offset, int limit){
        return messageDao.getConversionDetail(conversionId, offset, limit);
    }

    public List<Message> getConversionList(int userId, int offset, int limit){
        return messageDao.getConversionList(userId, offset, limit);
    }

    public int getConversionUnreadCount(int userId, String conversationId){
        return messageDao.getConversionUnreadCount(userId, conversationId);
    }
}
