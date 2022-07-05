package top.chriszwz.community.dao;

import org.apache.ibatis.annotations.Mapper;
import top.chriszwz.community.entity.Message;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户会话列表,针对每个会话返回最新一条消息
    List<Message> selectConversations(int user_id, int offset, int limit);

    //查询当前用户的会话数量
    int selectConversationsCount(int user_id);

    //查询某个会话包含的消息列表
    List<Message> selectLetters(String conversation_id, int offset, int limit);

    //查询某个会话的消息数量
    int selectLetterCount(String conversation_id);

    //查询未读私信数量
    int selectLetterUnreadCount(int user_id, String conversation_id);

    //新增消息
    int insertMessage(Message message);

    //更新消息状态
    int updateStatus(List<Integer> ids, int status);

    //查询某个主题下最新的通知
    Message selectLatestNotice(int user_id, String topic);

    //查询某个主题包含的通知的数量
    int selectNoticeCount(int user_id, String topic);

    //查询未读的通知的数量
    int selectNoticeUnreadCount(int user_id, String topic);

    //查询某个主题包含的通知列表
    List<Message> selectNotices(int user_id, String topic, int offset, int limit);

}
