//package com.cfca.ra.repository;
//
//import com.cfca.ra.RAServerException;
//import com.cfca.ra.register.IUser;
//import com.cfca.ra.utils.MyFileUtils;
//import com.google.gson.Gson;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
///**
// * @author zhangchong
// * @create 2018/5/16
// * @Description CA 用户的数据库 UserStore 信息
// * @CodeReviewer
// * @since v3.0.0
// */
//public enum UserStore implements IUserStore {
//    /**
//     * 单例实例
//     */
//    CFCA("CFCA") {
//        private Map<String, String> userStore;
//
//        private String getServerHomeDir() {
//            String homeDir = String.join(File.separator, System.getProperty("user.dir"), caName);
//            return MyFileUtils.getAbsFilePath(homeDir);
//        }
//
//        @Override
//        public void updateUserStore(IUser user, String secret) throws RAServerException {
//            final String name = user.getName();
//            userStore.put(name, secret);
//            logger.info("updateUserStore:[{} : {}]", name, secret);
//            updateUserFile();
//        }
//
//        private void updateUserFile() throws RAServerException {
//            if (userStore != null) {
//                final String s = new Gson().toJson(userStore);
//                final String filename = String.join(File.separator, getServerHomeDir(), "user.dat");
//                try {
//                    FileUtils.writeStringToFile(new File(filename), s);
//                } catch (IOException e) {
//                    throw new RAServerException(RAServerException.REASON_CODE_USER_STORE_UPDATE_EXCEPTION, e);
//                }
//            }
//
//        }
//
//        @Override
//        public boolean containsUser(String user) throws RAServerException {
//            if (userStore == null) {
//                userStore = loadUserFile();
//            }
//            return userStore.containsKey(user);
//        }
//
//        @Override
//        public String getUser(String user) throws RAServerException {
//            if (userStore == null) {
//                userStore = loadUserFile();
//            }
//            return userStore.getOrDefault(user, "");
//        }
//
//        private Map<String, String> loadUserFile() throws RAServerException {
//            final Map<String, String> usrStore = new HashMap<String, String>() {{
//                put("admin", "YWRtaW46MTIzNA==");
//            }};
//
//            try {
//                final String filename = String.join(File.separator, getServerHomeDir(), "user.dat");
//                File file = new File(filename);
//                if (file.exists()) {
//                    final String s = FileUtils.readFileToString(file);
//                    if (StringUtils.isBlank(s) || s.trim().equalsIgnoreCase("null")) {
//                        file.delete();
//                        return usrStore;
//                    }
//                    final Map map = new Gson().fromJson(s, Map.class);
//                    Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
//                    Map.Entry<String, String> entry;
//                    while (it.hasNext()) {
//                        entry = it.next();
//                        usrStore.put(entry.getKey(), entry.getValue());
//                    }
//                }
//                return usrStore;
//            } catch (IOException e) {
//                throw new RAServerException(RAServerException.REASON_CODE_USER_STORE_LOAD_EXCEPTION, e);
//            }
//        }
//    };
//
//    protected static final Logger logger = LoggerFactory.getLogger(UserStore.class);
//    protected final String caName;
//
//    UserStore(String caName) {
//        this.caName = caName;
//    }
//
//
//}
