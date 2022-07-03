package top.chriszwz.community.controller;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import top.chriszwz.community.annotation.LoginRequired;
import top.chriszwz.community.entity.User;
import top.chriszwz.community.service.FollowService;
import top.chriszwz.community.service.LikeService;
import top.chriszwz.community.service.UserService;
import top.chriszwz.community.util.CommunityConstant;
import top.chriszwz.community.util.CommunityUtil;
import top.chriszwz.community.util.HostHolder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error","您还没有选择图片!");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();//获取文件后缀名
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件后缀名不合法!");
            return "/site/setting";
        }

        //生成随机文件名
        fileName = CommunityUtil.generateUUID() + "." + suffix;
        File file = new File(uploadPath + "/" + fileName);
        //上传文件
        try {
            headerImage.transferTo(file);
        } catch (Exception e) {
            logger.error("上传文件失败!" + e.getMessage());
            throw new RuntimeException("上传文件失败!" + e.getMessage());//Controller会处理一切抛出的异常
        }
        //更新用户头像路径(web路径)
        int userId = hostHolder.getUser().getId();
        String headerUrl = domain + "/user/header/" + fileName;
        userService.updateHeaderUrl(userId, headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放路径
        String path = uploadPath + "/" + fileName;
        //获取文件后缀名
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        //设置响应头
        response.setContentType("image/" + suffix);
        //获取文件输入流
        try(
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(path);
        ){
            byte[] bytes = new byte[1024];
            int len = 0;
            while((len = fis.read(bytes)) != -1){
                os.write(bytes, 0, len);
            }
        } catch (Exception e) {
            logger.error("获取文件失败!" + e.getMessage());
        }
    }

    //修改密码
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword, Model model){
        if(StringUtils.isBlank(oldPassword)){
            model.addAttribute("oldPasswordMsg","密码不能为空!");
            System.out.println("密码new不能为空!");
            return "/site/setting";
        }
        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg","密码不能为空!");
            System.out.println("密码old不能为空!");
            return "/site/setting";
        }
        if(StringUtils.isBlank(confirmPassword)){
            model.addAttribute("confirmPasswordMsg","密码不能为空!");
            System.out.println("密码confirm不能为空!");
            return "/site/setting";
        }
        if(!newPassword.equals(confirmPassword)){
            model.addAttribute("confirmPasswordMsg","两次密码不一致!");
            System.out.println("两次密码不一致!");
            return "/site/setting";
        }
        int userId = hostHolder.getUser().getId();
        if(userService.updatePassword(userId, oldPassword, newPassword)){
            return "redirect:/index";
        }else{
            model.addAttribute("oldPasswordMsg","密码错误!");
            System.out.println("密码错误!");
            return "/site/setting";
        }
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);
        //用户点赞的数量
        int likeCount = likeService.getUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        //关注数量
        long followeeCount = followService.getFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数量
        long followerCount = followService.getFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        //是否关注
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.isFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }
}