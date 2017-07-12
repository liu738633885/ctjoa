package com.ctj.oa.model.work.log;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/6/4.
 */

public class LogTemplate {
    private List<Item> rules;

    public class Item {
        public int type_field;
        public String title_field;
        public String[] tip_field;
        public int required_field;//是否必填 0不是必填  1是必填
        public String value;
        public int select_position = -1;
        public List<String> values = new ArrayList<>();
    }

    public void makeData(Check check) {
        List<String> list = new ArrayList<>();
        for (Item item : rules) {
            String x = "{\"" + item.title_field + "\":\"";
            if (item.type_field == 5) {
                String b = item.values.toString().replace("[", "").replace("]", "");
                x += b + "\"}";
                if (item.required_field == 1) {
                    if (item.values == null || item.values.size() == 0) {
                        check.Fail(item.title_field);
                        return;
                    }
                }
            } else if (item.type_field == 8) {
                String b = item.values.toString().replace("[", "").replace("]", "");
                x += b + "\"}";
                if (item.required_field == 1) {
                    if (item.values == null || item.values.size() != 2 || TextUtils.isEmpty(item.values.get(0)) || TextUtils.isEmpty(item.values.get(1))) {
                        check.Fail(item.title_field);
                        return;
                    }
                }
            } else {
                if (TextUtils.isEmpty(item.value)) {
                    x += "\"}";
                } else {
                    x += item.value + "\"}";
                }
                if (item.required_field == 1 && TextUtils.isEmpty(item.value)) {
                    check.Fail(item.title_field);
                    return;
                }
            }
            list.add(x);
        }
        check.Success(list.toString());
        Logger.e("hahah" + list.toString());
    }

    public interface Check {
        void Fail(String title);

        void Success(String date);
    }


    public List<Item> getRules() {
        return rules;
    }

    public void setRules(List<Item> rules) {
        this.rules = rules;
    }
}
