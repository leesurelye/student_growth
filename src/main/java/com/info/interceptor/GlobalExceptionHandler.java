package com.info.interceptor;


import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.info.common.ReturnValue;
import com.info.common.sysenum.StateMsg;
import com.info.util.LogUtil;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author : yue
 * @Date : 2020/5/31 / 23:29
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String  jsonErrorHandler(HttpServletRequest req, Throwable t) throws Exception {
        ReturnValue<String> rtn = new ReturnValue<>();

        rtn.setStateMsg(StateMsg.StateMsg_500);
        rtn.setSystemerrormsg(t.getMessage());

        StringWriter stringWriter= new StringWriter();

        String jsonCallback = req.getParameter("callback");

        PrintWriter writer= new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer= stringWriter.getBuffer();

            String returnJson = JSON.toJSONString(rtn);
        if (!StringUtils.isEmpty(jsonCallback)) {
            returnJson = jsonCallback + "(" + returnJson + ")";
        }

        LogUtil.error(buffer.toString());

        return returnJson;
    }

}

