package com.cfca.ra.repository;

import com.cfca.ra.RAServerException;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description EnrollID数据库接口
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public interface IEnrollIdStore {

    void updateEnrollIdStore(String enrollmentID, String id) throws RAServerException;

    String getEnrollmentId(String id) throws RAServerException;

}
