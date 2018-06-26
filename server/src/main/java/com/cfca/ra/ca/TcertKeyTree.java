package com.cfca.ra.ca;

import com.cfca.ra.RAServerException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.Key;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchong
 * @create 2018/5/24
 * @Description
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
@Deprecated
public class TcertKeyTree {
    private Key rootKey;

    private Map<String, Key> keys;
    private static final String KEY_PATH_SEP = "/";

    public TcertKeyTree(Key rootKey) {
        this.rootKey = rootKey;
        this.keys = new HashMap<>();
    }

    public Key getKey(List<String> path) throws RAServerException {
        if (path == null || path.size() == 0) {
            return rootKey;
        }

        String pathStr = String.join(KEY_PATH_SEP, path);
        Key key = keys.getOrDefault(pathStr, null);
        if (key != null) {
            return key;
        }

        Key parentKey = getKey(path.subList(0, path.size() - 1));

        String childName = path.get(path.size() - 1);
        key = deriveChildKey(parentKey, childName, pathStr);

        keys.put(pathStr, key);
        return key;
    }

    private Key deriveChildKey(Key parentKey, String childName, String path) throws RAServerException {
        try {
            KeySpec keyspecbc = new PBEKeySpec(childName.toCharArray(), parentKey.getEncoded(), 1000, 128);
            SecretKeyFactory factorybc = SecretKeyFactory.getInstance("PBEWITHHMACSHA256", "BC");
            return factorybc.generateSecret(keyspecbc);
        } catch (Exception e) {
            final String message = String.format("Failed to derive key : %s", path);
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_DERIVE_CHILD_KEY, message, e);
        }
    }
}
