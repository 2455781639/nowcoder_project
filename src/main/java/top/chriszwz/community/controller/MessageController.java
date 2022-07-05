package top.chriszwz.community.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import top.chriszwz.community.entity.Message;
import top.chriszwz.community.entity.Page;
import top.chriszwz.community.entity.User;
import top.chriszwz.community.service.MessageService;
import top.chriszwz.community.service.UserService;
import top.chriszwz.community.util.CommunityConstant;
import top.chriszwz.community.util.CommunityUtil;
import top.chriszwz.community.util.HostHolder;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();

        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationsCount(user.getId()));

        //会话列表
        List<Message> conversations = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversationsList = new ArrayList<>();
        if(conversationsList != null){
            for(Message message: conversations){
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversation_id()));
                map.put("UnreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversation_id()));
                int target_id = message.getFrom_id() == user.getId() ? message.getTo_id() : message.getFrom_id();
                map.put("target", userService.findById(target_id));
                conversationsList.add(map);
            }
        }
        model.addAttribute("conversations", conversationsList);

        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversation_id}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversation_id") String conversation_id, Page page, Model model){
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversation_id);
        page.setRows(messageService.findLetterCount(conversation_id));

        //消息列表
        List<Message> letterList = messageService.findLetters(conversation_id, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letterListMap = new ArrayList<>();
        if(letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findById(message.getFrom_id()));
                letterListMap.add(map);
            }
        }
        model.addAttribute("letterList", letterListMap);

        //查询私信目标
        model.addAttribute("target", getLetterTarget(conversation_id));

        //设置已读
        List<Integer> list = getLetterIds(letterList);
        if(!list.isEmpty()){
            messageService.readMessage(list);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversation_id){
        String[] ids = conversation_id.split("_");
        int d0 = Integer.parseInt(ids[0]);
        int d1 = Integer.parseInt(ids[1]);

        if(hostHolder.getUser().getId() == d0) {
            return userService.findById(d1);
        } else if(hostHolder.getUser().getId() == d1) {
            return userService.findById(d0);
        } else {
            return null;
        }
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> list = new ArrayList<>();
        if(letterList != null){
            for(Message message: letterList){
                if(hostHolder.getUser().getId() == message.getTo_id() && message.getStatus() == 0){
                    list.add(message.getId());
                }
            }
        }
        return list;
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content){
        User target = userService.findUserByName(toName);
        if(target == null){
            System.out.println("目标用户不存在");
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }
        Message message = new Message();
        message.setFrom_id(hostHolder.getUser().getId());
        message.setTo_id(target.getId());
        if(message.getFrom_id() < message.getTo_id()){
            message.setConversation_id(message.getFrom_id() + "_" + message.getTo_id());
        } else {
            message.setConversation_id(message.getTo_id() + "_" + message.getFrom_id());
        }
        message.setContent(content);
        message.setCreate_time(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();

        //查询评论类的通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVo = new HashMap<>();
        if(message != null){
            messageVo.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user", userService.findById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("unread", unread);
            model.addAttribute("commentNotice", messageVo);
        }

        //查询点赞类的通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVo = new HashMap<>();
        if(message != null){
            messageVo.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user", userService.findById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVo.put("unread", unread);
            model.addAttribute("likeNotice", messageVo);
        }

        //查询关注类的通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVo = new HashMap<>();
        if(message != null){
            messageVo.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user", userService.findById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unread", unread);
            model.addAttribute("followNotice", messageVo);
        }

        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @RequestMapping(path = "notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model){
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if(noticeList != null){
            for(Message notice: noticeList){
                Map<String, Object> noticeVo = new HashMap<>();
                noticeVo.put("notice", notice);

                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

                noticeVo.put("user", userService.findById((Integer) data.get("userId")));
                noticeVo.put("entityType", data.get("entityType"));
                noticeVo.put("entityId", data.get("entityId"));
                noticeVo.put("postId", data.get("postId"));

                //通知作者
                noticeVo.put("fromUser", userService.findById(notice.getFrom_id()));

                noticeVoList.add(noticeVo);
            }
        }
        model.addAttribute("notices", noticeVoList);

        //设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }
}
