package top.chriszwz.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import top.chriszwz.community.dao.MessageMapper;
import top.chriszwz.community.entity.Message;
import top.chriszwz.community.util.SensitiveFilter;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int user_id, int offset, int limit) {
        return messageMapper.selectConversations(user_id, offset, limit);
    }

    public int findConversationsCount(int user_id) {
        return messageMapper.selectConversationsCount(user_id);
    }

    public List<Message> findLetters(String conversation_id, int offset, int limit) {
        return messageMapper.selectLetters(conversation_id, offset, limit);
    }

    public int findLetterCount(String conversation_id) {
        return messageMapper.selectLetterCount(conversation_id);
    }

    public int findLetterUnreadCount(int user_id, String conversation_id) {
        return messageMapper.selectLetterUnreadCount(user_id, conversation_id);
    }

    //新增消息
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    public void readMessage(List<Integer> ids){
        messageMapper.updateStatus(ids, 1);
    }

}
