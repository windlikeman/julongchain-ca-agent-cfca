package demo;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.internal.BaseClientCommand;
import com.cfca.ra.command.internal.CsrResult;
import com.cfca.ra.command.internal.reenroll.ReenrollCommand;
import com.cfca.ra.command.internal.reenroll.ReenrollmentRequest;
import com.cfca.ra.command.utils.ConfigUtils;
import com.cfca.ra.command.utils.MyStringUtils;
import com.cfca.ra.command.utils.PemUtils;
import com.google.gson.Gson;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

import static demo.CsrUtils.genCSR;
import static demo.CsrUtils.storeMyPrivateKey;

public class ReenrollCommandDemo {

    private ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_LOAD_CONFIG_FAILED, e);
        }
    }

    public static void main(String[] args) throws Exception {
        final ReenrollCommandDemo reenrollCommandDemo = new ReenrollCommandDemo();
        reenrollCommandDemo.testReenroll();
    }


    private void testReenroll() throws Exception{
        final ConfigBean configBean = loadConfigFile();
        String profile = configBean.getEnrollment().getProfile();
        CsrConfig csrConfig = configBean.getCsr();
        String caName = configBean.getCaname();
        //"test", "dGVzdDoxMjM0"// "test2":"dGVzdDI6MTIzNA=="

        final String algo = csrConfig.getKey().getAlgo();
        final String names = csrConfig.getNames();
        final CsrResult result = genCSR(algo, names);
        storeMyPrivateKey(result);

        final ReenrollmentRequest.Builder builder = new ReenrollmentRequest.Builder(result.getCsr(), "test4","dGVzdDQ6MTIzNA==", profile, csrConfig, caName);
        final ReenrollmentRequest reenrollmentRequest = builder.build();
        String[] args = new String[]{"enroll", "-h", "localhost", "-p", "8089", "-a", new Gson().toJson(reenrollmentRequest)};
        ReenrollCommand reenrollCommand=new ReenrollCommand();
        reenrollCommand.prepare(args);
        reenrollCommand.execute();
    }

}