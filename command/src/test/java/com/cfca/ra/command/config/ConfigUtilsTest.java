package com.cfca.ra.command.config;

import com.cfca.ra.command.utils.ConfigUtils;
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
        ConfigBean configBean = ConfigUtils.load("config/ca-client-config.yaml");
        System.out.println(configBean);
    }
}
