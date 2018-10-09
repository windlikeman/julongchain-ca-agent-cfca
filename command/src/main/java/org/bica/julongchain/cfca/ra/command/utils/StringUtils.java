package org.bica.julongchain.cfca.ra.command.utils;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 字符串工具类
 * @CodeReviewer
 * @since v3.0.0
 */
public class StringUtils {

    private StringUtils() {
    }

    /**
     * 检查一个 CharSequence 是否是空格, 空 ("") 或者 null.
     *
     * @param cs
     *            待检查的 CharSequence , 可能是 null
     * @return {@code true} 如果 CharSequence 是 null, 空或者空格
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!(Character.isWhitespace(cs.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查一个 CharSequence 是否为空 (""), 是否为null 是否是空格.
     *
     * @param cs
     *            待检查的 CharSequence , 可能是 null
     * @return {@code true} 如果 cs 不会空 也不为null 也不为空格
     */
    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

    /**
     * <p>
     * 判断字符串是否为空或者为空串
     * </p>
     *
     * @param cs
     *            待检查的CharSequence, 可能为 null
     * @return {@code true} 如果 CharSequence is empty or null
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * <p>
     * 检查 一个CharSequence 是否不会空 ("") 而且不为 null.
     * </p>
     *
     * @param cs
     *            待检查的 CharSequence, 可能为 null
     * @return {@code true} 如果 CharSequence 不为空或者不为 null
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 如果末尾是 cutset, 截取不包含它的剩余字符串
     * 
     * @param str
     * @param cutset
     * @return
     */
    public static String trimRight(String str, String cutset) {
        str = str.trim();
        if (str.endsWith(cutset)) {
            str = str.substring(0, str.length() - cutset.length());
        }
        return str;
    }
}
