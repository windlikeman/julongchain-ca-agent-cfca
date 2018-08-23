package org.bica.julongchain.cfca.ra.command.internal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws Exception {
        String homedir = "./";
        final Client client = new Client(ClientConfig.INSTANCE, homedir);
        // System.out.println(client.genCSR("SM2",
        // "CN=051@testName@Z1234567890@53,OU=Individual-3,OU=Local RA,O=CFCA TEST CA,C=CN").getCsr());
    }

    @Test
    public void testbuildSigner() throws Exception {
        byte[] cert = new byte[] { 1, 2, 3, 3 };
        Client client = new Client(null, "homedir");
        Signer ecert = new Signer(null, cert, client);
        final Identity id = new Identity("hello", ecert, client);
        System.out.println(id);
        System.out.println(ecert);
    }

}
