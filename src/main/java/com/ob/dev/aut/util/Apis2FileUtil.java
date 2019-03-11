package com.ob.dev.aut.util;

import com.alibaba.fastjson.JSON;
import com.ob.dev.aut.model.ApiModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Apis2FileUtil {
    //将得到的所有测试用例结构化数据写入文件
    public static void mkApisInfoFile(String apisInfoFile, List<ApiModel> apis) {
        File file = new File(apisInfoFile);
        //如果路径不存在就创建
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        //写文件
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(JSON.toJSONString(apis));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
