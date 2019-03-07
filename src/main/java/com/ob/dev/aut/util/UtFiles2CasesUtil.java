package com.ob.dev.aut.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ob.dev.aut.model.UtCase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UtFiles2CasesUtil {
    /*
    获取ut/data下面的指定测试接口的测试用例数据。
    参数说明：
    dataPath:
            该API的测试用例数据文件存放路径，一个json文件表示一个测试用例数据
            例如：ut/data/com/ob/api/RestAppStoreController/get_getAppById路径下有testId1.json文件
     */
    public static List<UtCase> getCases(String dataPath) {

        File file = new File(dataPath);
        File[] files = file.listFiles();
        List<UtCase> cases = new ArrayList<>();
        //判断是否有测试用例数据文件
        //没有时直接返回一个空的list
        if (files == null) {

            return cases;
        }
        //当有文件时，进行遍历
        for (File f : files) {
            if (f.isFile()) {
                String path = f.getPath();
                if (path.endsWith(".json")) {
                    //获得文件名
                    String fileName = f.getName();
                    //去除.json后缀
                    fileName = fileName.substring(0, fileName.length() - 5);
                    //替换掉不是字母或者数字的字符，最终这个会作为测试用例名称的一部分
                    fileName = fileName.replaceAll("[^(A-Za-z0-9)]", "");

                    try {
                        //将测试数据读取为字符串。FileUtils.readFileToString
                        String fileStr = FileUtils.readFileToString(f);
                        //转化为json格式
                        JSONObject fileJson = JSON.parseObject(fileStr);
                        //生成用例数据格式
                        UtCase utCase = new UtCase();
                        //防止用例重名，采用测试数据文件名+ 时间戳的方式来命名用例名
                        utCase.setName(fileName + "_" + new Date().getTime());
                        utCase.setIn(fileJson.getString("in"));
                        utCase.setOut(fileJson.getString("out"));
                        cases.add(utCase);
                    } catch (Exception e) {

                    }
                }
            }
        }
        return cases;
    }

}
