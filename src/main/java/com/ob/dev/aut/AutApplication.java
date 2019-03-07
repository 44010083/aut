package com.ob.dev.aut;


import com.ob.dev.aut.model.ApiModel;
import com.ob.dev.aut.model.UtCase;
import com.ob.dev.aut.util.CreateTestFilesUtil;
import com.ob.dev.aut.util.ParseSrcUtil;
import com.ob.dev.aut.util.UtFiles2CasesUtil;

import java.io.File;
import java.util.List;

public class AutApplication {
    /*
    对指定的路径的源码进行分析的入口。
    如果执行时，带了-Dprj.root参数，则分析prj.root路径下项目的源码；
    如果执行时，没有-Dprj.root参数，则分析当前路径下项目的源码；
    要求：
    项目下必须要有src路径
     */
    public static void main(String[] args) {
        //userDir为源码根目录
        String userDir = "";
        //判断，执行时，是否使用了-Dprj.root
        String prjRoot = System.getProperty("prj.root");
        boolean hasPrjRoot = false;
        if (prjRoot != null && !"".equalsIgnoreCase(prjRoot)) {
            hasPrjRoot = true;
            userDir = prjRoot;
        } else {
            hasPrjRoot = false;
            userDir = System.getProperty("user.dir");
        }
        //userDir ="E:\\test\\zuul_eureka_feign-master\\hello";
        userDir = userDir.replaceAll("\\\\", "/");
        userDir = userDir.replaceAll("//", "/");
        if (userDir.endsWith("/")) {
            userDir = userDir.substring(0, userDir.length() - 1);
        }
        File file = new File(userDir + "/src");
        boolean doCreateUt = false;
        if (file.exists()) {
            doCreateUt = true;
        } else {
            doCreateUt = false;
            if (hasPrjRoot) {
                System.out.println("********* error: you set prj.root=" + prjRoot);
                System.out.println("but it has no src subdirectory");
            } else {
                System.out.println("no src subdirectory,you can run with java -Dprj.root=");
            }

        }
        if (doCreateUt) {
            String path = userDir + "/src/main/java";
            String testsPath = userDir + "/src/test/java";
            String dataPath = userDir + "/ut/data";
            String modulePath = userDir + "/ut/module";


            if (System.getProperty("rewrite.tests") != null && "true".equalsIgnoreCase(System.getProperty("rewrite.tests"))) {
                System.out.println("********* warn: rewrite testfile when has the same");
            } else {
                System.out.println("********* info: if you want to rewrite testfile when has the same,run with java -Drewrite.tests=true");
            }
            //分析源码文件，获得api接口
            List<ApiModel> apis = ParseSrcUtil.parseByPath(path, modulePath);

            //生成每个API的ut代码
            for (ApiModel apiModel : apis) {
                String apiDataPath = dataPath + "/" + apiModel.getPackageName().replaceAll("\\.", "/");
                apiDataPath += "/" + apiModel.getFileName() + "/" + apiModel.getApiType() + "_" + apiModel.getApiName();
                //获得api的测试用例数据
                List<UtCase> cases = UtFiles2CasesUtil.getCases(apiDataPath);
                //生成测试代码文件
                CreateTestFilesUtil.mkTestFile(testsPath, apiModel, cases);
            }
        }

    }


}
