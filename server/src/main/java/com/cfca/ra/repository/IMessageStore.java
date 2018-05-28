package com.cfca.ra.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.BaseRequest;

public interface IMessageStore {
    boolean containsMessage(int messageId) throws RAServerException;

    void updateMessage(int messageId, BaseRequest s) throws RAServerException;
}
