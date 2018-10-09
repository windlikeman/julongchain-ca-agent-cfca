package org.bica.julongchain.cfca.ra.command.utils;

import org.bica.julongchain.cfca.ra.command.CommandException;

import java.security.SecureRandom;

/**
 * @author zhangchong
 * @Create 2018/7/27 10:46
 * @CodeReviewer
 * @Description 产生指定长度的随机字符串
 * @since
 */
public class RandomUtils {
    private static char ch[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', '0', '1' };

    public static synchronized String createRandomString(int length) throws CommandException {

        if (length<0){
            throw new CommandException("RandomUtils@createRandomString : length < 0");
        }
        if (length > 0) {
            SecureRandom random = new SecureRandom();
            int index = 0;
            char[] temp = new char[length];
            int num = random.nextInt();
            for (int i = 0; i < length % 5; i++) {
                temp[index++] = ch[num & 63];
                num >>= 6;
            }
            for (int i = 0; i < length / 5; i++) {
                num = random.nextInt();
                for (int j = 0; j < 5; j++) {
                    temp[index++] = ch[num & 63];
                    num >>= 6;
                }
            }
            return new String(temp, 0, length);
        } else {
            return "";
        }
    }
}
