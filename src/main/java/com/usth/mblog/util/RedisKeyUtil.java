package com.usth.mblog.util;

public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String DAYRANK = "dayRank";
    private static String POST = "post";
    private static String POSTID = "post.id";
    private static String POSTTITLE = "post.title";
    private static String POSTVIEWCOUNT = "post.viewCount";
    private static String WEEKRANK = "weekRank";

    public static String getDayRankKey(String date) {
        return DAYRANK + SPLIT + date;
    }

    public static String getPostKey(Object id) {
        return POST + SPLIT + id;
    }

    public static String getWeekRankKey() {
        return WEEKRANK + SPLIT;
    }

    public static String getPostIdKey() {
        return POSTID;
    }

    public static String getPostTitleKey() {
        return POSTTITLE;
    }

    public static String getPostViewCountKey() {
        return POSTVIEWCOUNT;
    }
}
