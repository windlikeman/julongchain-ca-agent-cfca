package org.bica.julongchain.cfca.ra.command.config;

import org.bica.julongchain.cfca.ra.command.utils.ConfigUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testInit() throws Exception {
        ConfigBean configBean = ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        System.out.println(configBean);
    }

    @Test
    public void testDump() throws Exception {
        ConfigBean configBean = ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        System.out.println(configBean);

        ConfigUtils.update("ca-client/config/ca-client-config-update.yaml", configBean);

        configBean = ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        System.out.println(configBean);
    }
}
