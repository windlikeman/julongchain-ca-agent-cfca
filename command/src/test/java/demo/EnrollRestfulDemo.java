package demo;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.config.ConfigBean;
import org.bica.julongchain.cfca.ra.command.config.CsrConfig;
import org.bica.julongchain.cfca.ra.command.internal.ClientConfig;
import org.bica.julongchain.cfca.ra.command.internal.CsrResult;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentComms;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentRequest;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentRequestNet;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentResponseNet;
import org.bica.julongchain.cfca.ra.command.utils.ConfigUtils;
import org.bica.julongchain.cfca.ra.command.utils.CsrUtils;
import org.bouncycastle.util.encoders.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangchong
 * @Create 2018/6/26 10:11
 * @CodeReviewer
 * @Description
 * @since
 */
public class EnrollRestfulDemo {
    private static AtomicInteger id = new AtomicInteger(0);
    private static AtomicInteger num = new AtomicInteger(0);
    private static AtomicInteger err = new AtomicInteger(0);
    private static final int numThread = 32;
    private static final ExecutorService executor = Executors.newFixedThreadPool(numThread);
    private static ExecutorCompletionService<EnrollmentResponseNet> mcs = new ExecutorCompletionService<>(executor);
    private static final int total = 500;
    private static long x;
    private static long y;

    public static void main(String[] args) throws Exception {
        try {
            final ConfigBean configBean = loadConfigFile();


            run(configBean);
            statistic();
        } finally {
            executor.shutdown();
        }
    }

    private static void run(ConfigBean configBean) throws CommandException {
        String profile = configBean.getEnrollment().getProfile();
        CsrConfig csrConfig = configBean.getCsr();
        String caName = configBean.getCaname();
        final String username = "admin";
        final String password = "1234";
        final String algo = csrConfig.getKey().getAlgo();

        int index;
        String names;
        CsrResult e;
        List<CsrResult> results = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            index = id.incrementAndGet();
            names = generateNames(index);
            e = CsrUtils.genCSR(algo, names);
            results.add(e);
        }

        x = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            final CsrResult result = results.get(i);
            mcs.submit(() -> {
                try {
                    final String csr = result.getCsr();

                    final EnrollmentRequest.Builder builder = new EnrollmentRequest.Builder(csr, username, password,
                            profile, csrConfig, caName);
                    final EnrollmentRequest enrollmentRequest = builder.build();
                    ClientConfig clientCfg = ClientConfig.INSTANCE;
                    clientCfg.setUrl("http://localhost:8089");
                    clientCfg.setCaName(enrollmentRequest.getCaName());
                    clientCfg.setAdmin(enrollmentRequest.getUsername());
                    clientCfg.setAdminpwd(enrollmentRequest.getPassword());
                    clientCfg.setCsrConfig(enrollmentRequest.getCsrConfig());
                    clientCfg.setEnrollmentRequest(enrollmentRequest);

                    final String userInfo = username + ":" + password;
                    final String basicAuth = "Basic " + Base64.toBase64String(userInfo.getBytes("UTF-8"));
                    final String profile1 = enrollmentRequest.getProfile();
                    final String caName1 = enrollmentRequest.getCaName();
                    final EnrollmentRequestNet enrollmentRequestNet = new EnrollmentRequestNet.Builder(enrollmentRequest.getRequest(), profile1, caName1).build();
                    final EnrollmentComms enrollmentComms = new EnrollmentComms(clientCfg);
                    return enrollmentComms.request(enrollmentRequestNet, basicAuth);
                } catch (Exception exception) {
                    err.incrementAndGet();
                    return null;
                }
            });
        }
    }

    private static void statistic() {
        int success = 0;
        int error = 0;
        for (int i = 0; i < total; i++) {
            try {
                /*
                 * get one complete result from CompletionServer internal
                 * blocking queue
                 */
                mcs.take().get();

                success = num.incrementAndGet();
            } catch (InterruptedException | ExecutionException ignore) {
                error = err.incrementAndGet();
            }
        }
        y = System.currentTimeMillis();
        System.err.println("during " + (y - x) / 1000 + " second");
        System.err.println(String.format("tps=%.2f,  #all=%s    ###err=%s", (success + error) * 1000.0 / (y - x),
                total, err.get()));
    }

    private static ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    private static String generateNames(int index) {
        final String testName = "perf_test" + index;
        return "CN=051@" + testName + "@Z1234567890@53,OU=Individual-3,OU=Local RA,O=CFCA TEST CA,C=CN";
    }
}
