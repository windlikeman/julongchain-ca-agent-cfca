package com.cfca.ra.ca;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description Affiliation 接口类,定义用户权限角色的获取接口
 * @CodeReviewer
 * @since v3.0.0
 */
public interface Affiliation {

    /**
     *
     * @return name
     */
    String getName();

    /**
     *
     * @return 返回用户权限级别
     */
    int getLevel();

}
