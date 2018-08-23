package org.bica.julongchain.cfca.ra.command.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import org.bica.julongchain.cfca.ra.command.config.ConfigBean;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description ca-client 命令行的配置类
 * @CodeReviewer
 * @since v3.0.0
 */
public class ConfigUtils {
    public static final String DEFAULT_CFG_TEMPLATE = "#############################################################################\n"
            + "#   This is a configuration file for the fabric-ca-client command.\n" + "#\n" + "#   COMMAND LINE ARGUMENTS AND ENVIRONMENT VARIABLES\n"
            + "#   ------------------------------------------------\n" + "#   Each configuration element can be overridden via command line\n"
            + "#   arguments or environment variables.  The precedence for determining\n" + "#   the value of each element is as follows:\n"
            + "#   1) command line argument\n" + "#      Examples:\n" + "#      a) --url https://localhost:7054\n"
            + "#         To set the fabric-ca server url\n" + "#      b) --tls.client.certfile certfile.pem\n"
            + "#         To set the client certificate for TLS\n" + "#   2) environment variable\n" + "#      Examples:\n"
            + "#      a) FABRIC_CA_CLIENT_URL=https://localhost:7054\n" + "#         To set the fabric-ca server url\n"
            + "#      b) FABRIC_CA_CLIENT_TLS_CLIENT_CERTFILE=certfile.pem\n" + "#         To set the client certificate for TLS\n"
            + "#   3) configuration file\n" + "#   4) default value (if there is one)\n" + "#      All default values are shown beside each element below.\n"
            + "#\n" + "#   FILE NAME ELEMENTS\n" + "#   ------------------\n" + "#   The value of all fields whose name ends with \"file\" or \"files\" are\n"
            + "#   name or names of other files.\n" + "#   For example, see \"tls.certfiles\" and \"tls.client.certfile\".\n"
            + "#   The value of each of these fields can be a simple filename, a\n" + "#   relative path, or an absolute path.  If the value is not an\n"
            + "#   absolute path, it is interpretted as being relative to the location\n" + "#   of this configuration file.\n" + "#\n"
            + "#############################################################################\n" + "\n"
            + "#############################################################################\n" + "# Client Configuration\n"
            + "#############################################################################\n" + "\n"
            + "# URL of the Fabric-ca-server (default: http://localhost:7054)\n" + "url: <<<URL>>>\n" + "\n"
            + "# Membership Service Provider (MSP) directory\n" + "# This is useful when the client is used to enroll a peer or orderer, so\n"
            + "# that the enrollment artifacts are stored in the format expected by MSP.\n" + "mspdir: msp\n" + "\n"
            + "#############################################################################\n" + "#    TLS section for secure socket connection\n" + "#\n"
            + "#  certfiles - PEM-encoded list of trusted root certificate files\n" + "#  client:\n"
            + "#    certfile - PEM-encoded certificate file for when client authentication\n" + "#    is enabled on server\n"
            + "#    keyfile - PEM-encoded key file for when client authentication\n" + "#    is enabled on server\n"
            + "#############################################################################\n" + "tls:\n" + "  # TLS section for secure socket connection\n"
            + "  # Enable TLS (default: false)\n" + "  enabled: false\n" + "  certfiles:\n" + "  client:\n" + "    certfile:\n" + "    keyfile:\n" + "\n"
            + "#############################################################################\n"
            + "#  Certificate Signing Request section for generating the CSR for an\n" + "#  enrollment certificate (ECert)\n" + "#\n"
            + "#  cn - Used by CAs to determine which domain the certificate is to be generated for\n" + "#\n"
            + "#  serialnumber - The serialnumber field, if specified, becomes part of the issued\n"
            + "#     certificate's DN (Distinguished Name).  For example, one use case for this is\n"
            + "#     a company with its own CA (Certificate Authority) which issues certificates\n"
            + "#     to its employees and wants to include the employee's serial number in the DN\n" + "#     of its issued certificates.\n"
            + "#     WARNING: The serialnumber field should not be confused with the certificate's\n"
            + "#     serial number which is set by the CA but is not a component of the\n" + "#     certificate's DN.\n" + "#\n"
            + "#  names -  A list of name objects. Each name object should contain at least one\n"
            + "#    \"C\", \"L\", \"O\", or \"ST\" value (or any combination of these) where these\n" + "#    are abbreviations for the following:\n"
            + "#        \"C\": country\n" + "#        \"L\": locality or municipality (such as city or town name)\n" + "#        \"O\": organization\n"
            + "#        \"OU\": organizational unit, such as the department responsible for owning the key;\n"
            + "#         it can also be used for a \"Doing Business As\" (DBS) name\n" + "#        \"ST\": the state or province\n" + "#\n"
            + "#    Note that the \"OU\" or organizational units of an ECert are always set according\n"
            + "#    to the values of the identities type and affiliation. OUs are calculated for an enroll\n"
            + "#    as OU=<type>, OU=<affiliationRoot>, ..., OU=<affiliationLeaf>. For example, an identity\n"
            + "#    of type \"client\" with an affiliation of \"org1.dept2.team3\" would have the following\n"
            + "#    organizational units: OU=client, OU=org1, OU=dept2, OU=team3\n" + "#\n"
            + "#  hosts - A list of host names for which the certificate should be valid\n" + "#\n"
            + "#############################################################################\n" + "csr:\n" + "  cn: admin\n" + "  serialnumber:\n"
            + "  names: CN=051@testName@Z1234567890@53,OU=Individual-3,OU=Local RA,O=CFCA TEST CA,C=CN\n" + "  hosts:\n" + "    - <<<MYHOST>>>\n" + "  key:\n"
            + "    algo: SM2\n" + "    size: 256\n" + "  ca:\n" + "      pathlen: 0\n" + "      pathlenzero: 0\n" + "      expiry: -1\n" + "\n"
            + "#############################################################################\n"
            + "#  Registration section used to register a new identity with fabric-ca server\n" + "#\n" + "#  name - Unique name of the identity\n"
            + "#  type - Type of identity being registered (e.g. 'peer, app, user')\n" + "#  affiliation - The identity's affiliation\n"
            + "#  maxenrollments - The maximum number of times the secret can be reused to enroll.\n"
            + "#                   Specially, -1 means unlimited; 0 means to use CA's max enrollment\n" + "#                   value.\n"
            + "#  attributes - List of name/value pairs of attribute for identity\n"
            + "#############################################################################\n" + "id:\n" + "  name:\n" + "  type:\n" + "  affiliation:\n"
            + "  maxenrollments: 0\n" + "  attributes:\n" + "   # - name:\n" + "   #   value:\n" + "\n"
            + "#############################################################################\n"
            + "#  Enrollment section used to enroll an identity with fabric-ca server\n" + "#\n"
            + "#  profile - Name of the signing profile to use in issuing the certificate\n" + "#  label - Label to use in HSM operations\n"
            + "#############################################################################\n" + "enrollment:\n" + "  profile: H09358028\n" + "  label:\n"
            + "\n" + "#############################################################################\n"
            + "# Name of the CA to connect to within the fabric-ca server\n"
            + "#############################################################################\n" + "caname: CFCA\n" + "admin: admin\n" + "adminpwd: 1234\n";

    public static ConfigBean load(String configFilePath) throws Exception {
        Yaml yaml = new Yaml();
        return yaml.loadAs(new FileInputStream(configFilePath), ConfigBean.class);
    }

    public static void update(String configFilePath, ConfigBean configBean) throws Exception {
        DumperOptions printOptions = new DumperOptions();
        printOptions.setAllowUnicode(true);
        printOptions.setAllowReadOnlyProperties(true);
        printOptions.setPrettyFlow(true);
        Yaml yaml = new Yaml(printOptions);
        final File file = new File(configFilePath);
        final FileWriter fileWriter = new FileWriter(file);
        yaml.dump(configBean, fileWriter);
    }
}
