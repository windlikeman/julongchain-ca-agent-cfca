package org.bica.julongchain.cfca.ra;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 命令异常类
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class RAServerException extends Exception {
    private static final long serialVersionUID = 8383137633372345975L;

    private String message;

    private Throwable cause;

    public RAServerException(final Throwable cause) {
        super(warning(null));
        this.cause = cause;
        this.message = cause.getMessage();
    }

    public RAServerException(final String message, final Throwable cause) {
        super(warning(message));
        this.message = message;
        this.cause = cause;
    }

    public RAServerException(final String message) {
        super(warning(message));
        this.message = message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String getMessage() {
        if (!StringUtils.isEmpty(this.message)){
            return this.message;
        }
        return "Unknown error message";
    }

    @Override
    public String toString() {
        String message = getMessage();
        if (cause != null) {
            message += " - " + cause.toString();
        }
        return message;
    }

    private static final String warning(String note) {

        String warning;
        if (note == null) {
            warning = "Unknown error message";
        } else {
            warning = note;
        }
        return warning;
    }
}
