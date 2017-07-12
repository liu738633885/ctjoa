package com.ctj.oa.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 16/7/1.
 */
public class DataMaker {
    public static List<Object> makeObjects(int num) {
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Object o = new Object();
            objects.add(o);
        }
        return objects;
    }

    public static List<String> makeObjectsString(int num) {
        List<String> strs = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            strs.add("第" + i + "项==");
        }
        return strs;
    }
    /*public static List<Club> makeClubs(int num) {
        List<Club> clubs = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Club club = new Club();
            clubs.add(club);
        }
        return clubs;
    }

    public static List<String> makeStrings(int num) {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            strings.add("item" + i);
        }
        return strings;
    }

    public static List<ActivityInfo> makeActivities(int num) {
        List<ActivityInfo> activityInfos = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            ActivityInfo activityInfo = new ActivityInfo();
            activityInfos.add(activityInfo);
        }
        return activityInfos;
    }*/
}
