package com.lyy.client.rpcutil;

import com.alibaba.fastjson.JSON;
import com.lyy.client.anno.RemoteClass;
import com.lyy.client.model.Result;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ServiceProxy<T> implements InvocationHandler {

    private T target;

    public ServiceProxy(T target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RemoteClass remoteClass = method.getDeclaringClass().getAnnotation(RemoteClass.class);
        if (remoteClass == null) {
            throw new Exception("远程类标志未指定");
        }

        List<String> argTypeList = new ArrayList<>();
        if (args != null) {
            for (Object obj : args) {
                argTypeList.add(obj.getClass().getName());
            }
        }

        String argTypes = JSON.toJSONString(argTypeList);
        String argValues = JSON.toJSONString(args);

        System.out.println("即将请求：" + remoteClass.value() + "，函数名：" + method.getName() + "，请求参数类型：" + argTypes + "，请求值：" + argValues);
        Result result = HttpUtil.callRemoteService(remoteClass.value(), method.getName(), argTypes, argValues);

        if (result.isSuccess()) {
            return JSON.parseObject(result.getResultValue(), Class.forName(result.getResultType()));
        } else {
            throw new Exception("远程调用异常：" + result.getMessage());

        }
    }
}