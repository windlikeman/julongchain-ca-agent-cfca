package org.bica.julongchain.cfca.ra.po;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bica.julongchain.cfca.ra.ca.Attribute;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author zhangchong
 * @Create 2018/7/27 18:47
 * @CodeReviewer
 * @Description
 * @since
 */
public class RegistryUserPoTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getCaName() {
        String caName = "CFCA";
        String name = "admin";
        String pass = "YWRtaW46MTIzNA==";
        String type = "client";
        String affiliation = "";
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("hf.Registrar.Roles", "client,user,peer,validator,auditor", false));
        attributes.add(new Attribute("hf.Registrar.DelegateRoles", "client,user,validator,auditor", false));
        attributes.add(new Attribute("hf.Revoker", "true", false));
        int maxEnrollments = -1;
        int state = 0;
        final RegistryUserPo registryUserPo = new RegistryUserPo(caName, name, pass, type, affiliation, attributes, maxEnrollments, state);
        final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        System.out.println(gson.toJson(registryUserPo));
    }
}