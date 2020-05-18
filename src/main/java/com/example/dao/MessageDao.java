package com.example.dao;

import com.example.model.Comment;
import com.example.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDao {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, conversion_id, created_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId}, #{toId}, #{content}, #{hasRead}, #{conversionId}, #{createdDate})"})
    int addMessage(Message message);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME,
            "where conversion_id=#{conversionId} order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversionDetail(@Param("conversionId") String conversionId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    //SELECT *, Count(id) AS id from (SELECT * FROM message ORDER BY created_date DESC) tt GROUP BY conversion_id ORDER BY created_date LIMIT 0, 10
    @Select({"select", INSERT_FIELDS, ", Count(id) as id from (select * from ", TABLE_NAME,
            "where from_id = #{userId} or to_id = #{userId} order by created_date desc) tt  GROUP BY conversion_id ORDER BY created_date LIMIT #{offset}, #{limit}"})
    List<Message> getConversionList(@Param("userId") int userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    @Select({"select count(id) from", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversion_id=#{conversationId}"})
    int getConversionUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);
}
