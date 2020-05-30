package com.example.service;

import com.example.dao.FeedDao;
import com.example.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
    @Autowired
    FeedDao feedDao;

    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count){
        return feedDao.getUserFeeds(maxId, userIds, count);
    }

    public boolean addFeed(Feed feed){
        feedDao.addFeed(feed);
        return feed.getId() > 0;
    }

    public Feed getFeedById(int id){
        return feedDao.getFeedById(id);
    }
}
