package com.cfca.ra.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.BaseRequest;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description 消息管理类
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public interface IMessageStore {
    boolean containsMessage(int messageId) throws RAServerException;

    void updateMessage(int messageId, BaseRequest s) throws RAServerException;
}
