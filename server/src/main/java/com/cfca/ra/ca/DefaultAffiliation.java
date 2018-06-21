package com.cfca.ra.ca;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description Affiliation 接口的默认实现
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
class DefaultAffiliation implements  Affiliation{
    private final int level;
    private final String name;

    DefaultAffiliation(int level, String name) {
        this.level = level;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLevel() {
        return level;
    }
}
