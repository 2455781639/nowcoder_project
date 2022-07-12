package top.chriszwz.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.chriszwz.community.entity.Comment;
import top.chriszwz.community.entity.DiscussPost;
import top.chriszwz.community.entity.Event;
import top.chriszwz.community.event.EventProducer;
import top.chriszwz.community.service.CommentService;
import top.chriszwz.community.service.DiscussPostService;
import top.chriszwz.community.util.CommunityConstant;
import top.chriszwz.community.util.HostHolder;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUser_id(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreate_time(new Date());
        commentService.addComment(comment);

        //触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntity_type())
                .setEntityId(comment.getEntity_id())
                .setData("postId", discussPostId);
        if(comment.getEntity_type() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntity_id());
            event.setEntityUserId(target.getUser_id());
        } else if(comment.getEntity_type() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntity_id());
            event.setEntityUserId(target.getUser_id());
        }
        eventProducer.fireEvent(event);

        if(comment.getEntity_type() == ENTITY_TYPE_POST){
            //触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
