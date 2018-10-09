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
import org.bica.julongchain.cfca.ra.command.utils.RandomUtils;

import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangchong
 * @Create 2018/6/26 10:11
 * @CodeReviewer
 * @Description
 * @since
 */
public class EnrollStableDemo {
    //    private static final int total = 500;
    private static final int interval = 5;
    private static AtomicInteger success = new AtomicInteger(0);
    private static AtomicInteger num = new AtomicInteger(0);
    private static AtomicInteger err = new AtomicInteger(0);
    private static final int numThread = 32;
    private static final ExecutorService executor = Executors.newFixedThreadPool(numThread);
    private static ExecutorCompletionService<EnrollmentResponseNet> mcs = new ExecutorCompletionService<>(executor);
    private static AtomicInteger all = new AtomicInteger(0);
    private static long startTime;
    private static long endTime;

    public static void main(String[] args) throws Exception {
        try {


            new Thread(() -> {
                final ConfigBean configBean;
                try {
                    configBean = loadConfigFile();
                    work(configBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();

            statistic();

        } finally {
            executor.shutdown();
        }
    }

    private static void work(ConfigBean configBean) throws CommandException {
        String profile = configBean.getEnrollment().getProfile();
        CsrConfig csrConfig = configBean.getCsr();
        String caName = configBean.getCaname();
        final String username = "admin";
        final String password = "1234";
        final String algo = csrConfig.getKey().getAlgo();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.err.println("Java Finished...");
            }
        }));

        startTime = System.currentTimeMillis();

        while (true) {

            final CsrResult result = CsrUtils.genCSR(algo, "CN=vic,C=CN");

            mcs.submit(() -> {
                all.incrementAndGet();
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

                    final String basicAuth = "Basic YWRtaW46MTIzNA==:e1oiQeVrS2";
                    final String profile1 = enrollmentRequest.getProfile();
                    final String caName1 = enrollmentRequest.getCaName();
                    final EnrollmentRequestNet enrollmentRequestNet =
                            new EnrollmentRequestNet.Builder(enrollmentRequest.getRequest(), profile1, caName1).build();
                    final EnrollmentComms enrollmentComms = new EnrollmentComms(clientCfg);
                    final EnrollmentResponseNet request = enrollmentComms.request(enrollmentRequestNet, basicAuth);
                    success.incrementAndGet();
                    return request;
                } catch (CommandException exception) {
                    err.incrementAndGet();
                    return null;
                }
            });
        }
    }

    private static void statistic() throws InterruptedException {

        while (true) {
            TimeUnit.SECONDS.sleep(interval);
            endTime = System.currentTimeMillis();
            System.err.println("during " + interval + " second");
            final int total = all.get();
            System.err.println(String.format("tps=%.2f,  #all=%s    ###err=%s", (double) (total * 1000 /
                            (endTime - startTime)),
                    total + 2010232, err.get()));
        }
    }

    private static ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        } catch (Exception e) {
            throw new CommandException("the enrollment command failed to initiallize with config file", e);
        }
    }

}
