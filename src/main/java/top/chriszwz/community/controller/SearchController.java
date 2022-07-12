package top.chriszwz.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.chriszwz.community.entity.DiscussPost;
import top.chriszwz.community.entity.Page;
import top.chriszwz.community.service.ElasticsearchService;
import top.chriszwz.community.service.LikeService;
import top.chriszwz.community.service.UserService;
import top.chriszwz.community.util.CommunityConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Model model, Page page) {
        //搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult = elasticsearchService.searchDiscussPosts(keyword, page.getCurrent() - 1, page.getLimit());
        //聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(searchResult != null){
            for(DiscussPost post : searchResult){
                Map<String, Object> map = new HashMap<>();
                //帖子
                map.put("post", post);
                //用户
                map.put("user", userService.findById(post.getUser_id()));
                //点赞数量
                map.put("likeCount", likeService.getLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        //分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return "/site/search";
    }
}
