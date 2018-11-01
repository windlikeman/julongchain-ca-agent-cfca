package org.bica.julongchain.cfca.ra.repository;

import org.bica.julongchain.cfca.ra.po.EnrollCertPo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 签发证书的服务
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
@Repository
public interface EnrollCertRepository extends MongoRepository<EnrollCertPo, String> {

    boolean existsById(String id);

    EnrollCertPo findById(String id);

    void deleteByCaNameAndSerialNo(String caName, String serialNo);

}
