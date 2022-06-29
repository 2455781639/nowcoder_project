package top.chriszwz.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.chriszwz.community.entity.Comment;
import top.chriszwz.community.entity.DiscussPost;
import top.chriszwz.community.entity.Page;
import top.chriszwz.community.entity.User;
import top.chriszwz.community.service.CommentService;
import top.chriszwz.community.service.DiscussPostService;
import top.chriszwz.community.service.UserService;
import top.chriszwz.community.util.CommunityConstant;
import top.chriszwz.community.util.CommunityUtil;
import top.chriszwz.community.util.HostHolder;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    //添加帖子
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();

        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登陆哦!");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUser_id(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreate_time(new Date());
        discussPostService.addDiscussPost(discussPost);

        //报错情况统一处理
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    //帖子详情
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPostDetail(Model model, @PathVariable("discussPostId") int discussPostId, Page page) {
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        User user = userService.findById(discussPost.getUser_id());
        model.addAttribute("discussPost", discussPost);
        model.addAttribute("user", user);

        //查询评论信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getComment_count());

        //评论： 帖子的评论
        //回复： 其他人的回复
        //评论列表： 先查询帖子的评论，再查询其他人的回复
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());

        //查询回复信息
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                //查询回复信息
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findById(comment.getUser_id()));

                //回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findById(reply.getUser_id()));

                        // 回复目标
                        User target = reply.getTarget_id() == 0 ? null: userService.findById(reply.getTarget_id());
                        replyVo.put("target", target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                //回复数量
                commentVo.put("replyCount", commentService.findCommentCountByEntity(ENTITY_TYPE_COMMENT, comment.getId()));
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }




}