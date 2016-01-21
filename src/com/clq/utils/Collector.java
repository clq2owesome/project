package com.clq.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 计算效率的工具
 * @author chenliqiang
 * @update date 2015-07-16
 */
public class Collector {
    List<CollectItem> items = new ArrayList<CollectItem>();
    long start;
    CollectItem item;

    public void start(String string) {
        start = System.currentTimeMillis();
        item = new CollectItem(string);
    }

    public void stop() {
        item.usedMillis = System.currentTimeMillis() - start;
        items.add(item);
        item = null;
    }
    
    public String show() {
    	return this.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, c = items.size(); i < c; ++i) {
            sb.append(items.get(i).toString());
            if (i < c - 1) sb.append('\n');
        }
        return sb.toString();
    }
    

    class CollectItem {
        String key;
        long usedMillis;

        public CollectItem(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("#Item-").append(key+"#");
            sb.append(" use ").append(usedMillis);
            sb.append(" ms ");

            return sb.toString();
        }
    }
}

