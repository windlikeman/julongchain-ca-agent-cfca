package org.bica.julongchain.cfca.ra.repository;

import org.bica.julongchain.cfca.ra.po.RegistryUserPo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhangchong
 * @create 2018/7/27
 * @Description 注册用户的服务
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
@Repository
public interface RegistryUserRepository extends MongoRepository<RegistryUserPo, String> {

    RegistryUserPo findOneByCaNameAndName(String caName, String name);

    boolean existsByName(String name);

    boolean existsByCaNameAndName(String caName, String name);
}
