package top.chriszwz.community.util;

public class RedisKeyUtil {

    private static final String SPILT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    // 实体点赞key
    public static String getEntityLikeKey(int entityType, int entityId) {// entityType:entityId
        return PREFIX_ENTITY_LIKE + SPILT + entityType + SPILT + entityId;// 例如-> like:entity:1:2
    }

}
