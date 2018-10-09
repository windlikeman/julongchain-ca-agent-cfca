package org.bica.julongchain.cfca.ra.command;

import org.bica.julongchain.cfca.ra.command.utils.StringUtils;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 命令异常类
 * @CodeReviewer
 * @since v3.0.0
 */
public class CommandException extends Exception {

    private static final long serialVersionUID = 8383137635212345989L;

    public CommandException() {
        super();
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandException(String message) {
        super(message);
    }

    public CommandException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (StringUtils.isEmpty(message)) {
            message = "Unknown error message";
        }
        return message;
    }

    @Override
    public String toString() {
        String message = this.getMessage();
        Throwable cause = super.getCause();
        if (cause != null) {
            message += " - " + cause.toString();
        }
        return message;
    }
}
