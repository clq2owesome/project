package com.clq.core;

/**
 * 联合主键
 * @author chenliqiang
 * @update date 2015-09-06
 *
 */
public class Mid {
	Object[] ids;

    public Mid(Object ...ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < ids.length - 1; i ++) {
            buf.append(ids[i].toString()).append('-');
        }
        buf.append(ids[ids.length - 1]);
        return buf.toString();
    }
}
