package top.chriszwz.community.util;

public class RedisKeyUtil {

    private static final String SPILT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";

    // 实体点赞key
    public static String getEntityLikeKey(int entityType, int entityId) {// entityType:entityId
        return PREFIX_ENTITY_LIKE + SPILT + entityType + SPILT + entityId;// 例如-> like:entity:1:2
    }

    //某个用户的赞
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPILT + userId;
    }

    //某个用户关注的实体
    // followee:userId:entityType -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPILT + userId + SPILT + entityType;
    }

    //某个实体的粉丝
    // follower:entityType:entityId -> zset(userId, now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPILT + entityType + SPILT + entityId;
    }

    //登录验证码
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPILT + owner;
    }

    //登陆凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPILT + ticket;
    }

    //用户信息
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPILT + userId;
    }

    //单日uv
    public static String getUVKey(String date) {
        return PREFIX_UV + SPILT + date;
    }

    //区间uv
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPILT + startDate + SPILT + endDate;
    }

    //单日活跃用户
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPILT + date;
    }

    //区间活跃用户
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPILT + startDate + SPILT + endDate;
    }


}
