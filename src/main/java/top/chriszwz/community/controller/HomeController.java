package top.chriszwz.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.chriszwz.community.entity.DiscussPost;
import top.chriszwz.community.entity.Page;
import top.chriszwz.community.entity.User;
import top.chriszwz.community.service.DiscussPostService;
import top.chriszwz.community.service.UserService;
import top.chriszwz.community.util.CommunityUtil;

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
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost discussPost : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("discussPost", discussPost);
                User user = userService.findById(discussPost.getUser_id());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

    //ajax示例
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name,int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"success");
    }


}
