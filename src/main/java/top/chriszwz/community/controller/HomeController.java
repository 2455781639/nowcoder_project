package top.chriszwz.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.chriszwz.community.dao.elasticsearch.DiscussPostRepository;
import top.chriszwz.community.entity.DiscussPost;
import top.chriszwz.community.entity.Page;
import top.chriszwz.community.entity.User;
import top.chriszwz.community.service.DiscussPostService;
import top.chriszwz.community.service.LikeService;
import top.chriszwz.community.service.MessageService;
import top.chriszwz.community.service.UserService;
import top.chriszwz.community.util.CommunityConstant;
import top.chriszwz.community.util.CommunityUtil;
import top.chriszwz.community.util.HostHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* 
 * @Description: 首页
 * @Author: Chris(张文卓)
 * @Date: 2022/6/24 9:22
 */
@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        // 方法调用栈， SpringMVC会自动实例化Model和Page，并将Page注入到Model
        // 故可以在thymeleaf中可以直接访问page中的数据.
        page.setRows(discussPostService.findDiscussPostRows(0));// 设置总记录数
        page.setPath("/index");// 设置分页路径

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost discussPost : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("discussPost", discussPost);
                User user = userService.findById(discussPost.getUser_id());
                map.put("user", user);

                // 点赞数
                long likeCount = likeService.getLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }

    // 拒绝访问时的提示页面
    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "/error/404";
    }
}
