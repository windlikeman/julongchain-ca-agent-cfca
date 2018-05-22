package com.cfca.ra.ca.repository;

import com.cfca.ra.RAServerException;

import java.util.Map;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description EnrollID数据库接口
 * @CodeReviewer
 * @since
 */
public interface IEnrollIdStore {

    void updateEnrollIdStore(String enrollmentID, String id) throws RAServerException;

    String getEnrollmentId(String id) throws RAServerException;

}
