package com.cfca.ra.command;


/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 命令接口
 * @CodeReviewer
 * @since v3.0.0
 */
public interface IClientCommand {
    /**
     * 执行命令接口
     * @throws CommandException 命令执行遇到错误则抛出异常
     */
    void execute() throws CommandException;

    /**
     * 准备命令接口
     * @param args
     * @throws CommandException 命令执行遇到错误则抛出异常
     */
    void prepare(String[] args)throws CommandException;

}
