package org.bica.julongchain.cfca.ra.repository;

import org.apache.commons.lang.StringUtils;
import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.ca.Attribute;
import org.bica.julongchain.cfca.ra.ca.CAInfo;
import org.bica.julongchain.cfca.ra.client.RAClientUtil;
import org.bica.julongchain.cfca.ra.po.RegistryUserPo;
import org.bica.julongchain.cfca.ra.register.DefaultUser;
import org.bica.julongchain.cfca.ra.register.IUser;
import org.bica.julongchain.cfca.ra.register.UserInfo;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchong
 * @Create 2018/7/13 17:04
 * @CodeReviewer
 * @Description CA 对应注册命令的注册信息库
 * @since v3.0.0
 */
@Component
public class MongodbRegistryStore implements IRegistryStore {
    private static final Logger logger = LoggerFactory.getLogger(MongodbRegistryStore.class);
    private final RegistryUserRepository registryUserRepository;
    private final CAInfo caInfo;
    private final String caName;

    @Autowired
    public MongodbRegistryStore(CAInfo caInfo, final RegistryUserRepository registryUserRepository) {
        this.registryUserRepository = registryUserRepository;
        this.caInfo = caInfo;
        this.caName = caInfo.getName();
        logger.info("MongodbRegistryStore@init : caName={}", caName);
    }


    @Override
    public IUser getUser(String id, String[] attrs) throws RAServerException {
        final long startTime = System.currentTimeMillis();
        try {
            final RegistryUserPo oneByName = registryUserRepository.findOneByCaNameAndName(caName, id);
            return doForward(oneByName);
        } finally {
            final long runTime = System.currentTimeMillis() - startTime;
            if (runTime > RAClientUtil.warningTime) {
                logger.warn("MongodbRegistryStore@getUser : runTime={}, caName={}, id={}, attrs={}",
                        runTime, caName, id, attrs);
            }
        }
    }

    @Override
    public void insertUser(UserInfo user) throws RAServerException {
        final long startTime = System.currentTimeMillis();
        try {
            final String name = user.getName();
            final String s = name + ":" + user.getPass();
            final String s1 = Base64.toBase64String(s.getBytes("UTF-8"));
            UserInfo newUserInfo = new UserInfo(user, s1);

            RegistryUserPo registryUserPo = doBackward(this.caName, newUserInfo);
            registryUserRepository.save(registryUserPo);
        } catch (UnsupportedEncodingException e) {
            throw new RAServerException("register service failed to insert user into register store", e);
        } finally {
            final long runTime = System.currentTimeMillis() - startTime;
            if (runTime > RAClientUtil.warningTime) {
                logger.warn("MongodbRegistryStore@insertUser : runTime={}, caName={}, user={}",
                        runTime, caName, user);
            }
        }
    }

    @Override
    public boolean containsUser(String userName, String[] attrs) throws RAServerException {
        final long startTime = System.currentTimeMillis();
        try {
            return registryUserRepository.existsByCaNameAndName(this.caName, userName);
        } finally {
            final long runTime = System.currentTimeMillis() - startTime;
            if (runTime > RAClientUtil.warningTime) {
                logger.warn("MongodbRegistryStore@containsUser : runTime={}, id={}, attrs={}",
                        runTime, caName, userName, attrs);
            }
        }
    }

    private IUser doForward(RegistryUserPo oneByName) throws RAServerException {
        if (Objects.isNull(oneByName)) {
            throw new RAServerException("MongodbRegistryStore@doForward : failed by po is null");
        }
        String name = oneByName.getName();
        String pass = oneByName.getPass();
        String type = oneByName.getType();
        String affiliation = oneByName.getAffiliation();
        List<Attribute> attributes = oneByName.getAttributes();
        int maxEnrollments = oneByName.getMaxEnrollments();
        int state = oneByName.getState();
        UserInfo userInfo = new UserInfo(name, pass, type, affiliation, attributes, maxEnrollments, state);
        return new DefaultUser(userInfo);
    }

    private RegistryUserPo doBackward(String caName, UserInfo newUserInfo) throws RAServerException {
        if (StringUtils.isBlank(caName) || Objects.isNull(newUserInfo)) {
            throw new RAServerException("MongodbRegistryStore@doBackward : failed by vo(caName,newUserInfo) is null");
        }
        String name = newUserInfo.getName();
        String pass = newUserInfo.getPass();
        String type = newUserInfo.getType();
        String affiliation = newUserInfo.getAffiliation();
        List<Attribute> attributes = newUserInfo.getAttributes();
        int maxEnrollments = newUserInfo.getMaxEnrollments();
        int state = newUserInfo.getState();
        return new RegistryUserPo(caName, name, pass, type, affiliation, attributes, maxEnrollments, state);
    }
}
