package com.cfca.ra.command.internal.enroll;


import com.cfca.ra.command.CommandException;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description EnrollID数据库接口
 * @CodeReviewer
 * @since
 */
public interface IEnrollIdStore {

    void updateEnrollIdStore(String enrollmentID, String userName) throws CommandException;

    String getUserName(String enrollmentId) throws CommandException;

}
