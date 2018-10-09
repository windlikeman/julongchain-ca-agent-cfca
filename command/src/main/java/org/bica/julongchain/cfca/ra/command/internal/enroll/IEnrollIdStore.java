package org.bica.julongchain.cfca.ra.command.internal.enroll;


import org.bica.julongchain.cfca.ra.command.CommandException;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description EnrollID数据库接口
 * @CodeReviewer
 * @since v3.0.0
 */
public interface IEnrollIdStore {

    /**
     * 更新证书与用户绑定关系
     * 
     * @param enrollmentID
     *            证书ID
     * @param userName
     *            用户名
     * @throws CommandException
     *             遇到错误则返回
     */
    void updateEnrollIdStore(String enrollmentID, String userName) throws CommandException;

    /**
     * 得到证书绑定的用户名
     * 
     * @param enrollmentId
     *            证书ID
     * @return 证书绑定的用户名
     * @throws CommandException
     *             遇到错误则返回
     */
    String getUserName(String enrollmentId) throws CommandException;

}
