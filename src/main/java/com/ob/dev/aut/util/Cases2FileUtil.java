package com.ob.dev.aut.util;

import com.alibaba.fastjson.JSON;
import com.ob.dev.aut.model.ApiModel;
import com.ob.dev.aut.model.ArgsModel;
import com.ob.dev.aut.model.UtCase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Cases2FileUtil {
    //创建测试代码文件
    public static void mkTestFile(String baseTestPath, ApiModel api, List<UtCase> cases) {
        //如果api没有测试用例，直接返回
        if (cases.size() == 0) {
            return;
        }
        //测试代码文件名
        String fileName = api.getFileName() + api.getApiType().toUpperCase() + api.getApiName() + "Test";
        //测试代码字符串
        StringBuilder testCaseStringBuilder = new StringBuilder();
        //生成测试代码字符串
        testCaseStringBuilder = getTestCaseFileStr(fileName, testCaseStringBuilder, api, cases);
        //测试代码文件路径
        String filePath = "/" + api.getPackageName().replaceAll("\\.", "/") + "/" + fileName + ".java";
        //写测试代码文件到磁盘逻辑
        File file = new File(baseTestPath + filePath);
        //如果路径不存在就创建
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        //是否覆盖已有测试用例文件的系统参数，在执行时候 -Drewrite.tests设置
        String rw = System.getProperty("rewrite.tests");
        try {
            //file.createNewFile();
            //判断是否需要写Ut文件
            boolean writeUt = false;
            if (file.exists()) {
                if (rw != null && "true".equalsIgnoreCase(rw)) {
                    writeUt = true;
                }
            } else {
                writeUt = true;
            }
            //写测试文件逻辑
            if (writeUt) {
                FileWriter writer = new FileWriter(file);
                writer.write(testCaseStringBuilder.toString());
                writer.flush();
                writer.close();
            }
            //将每个测试文件都写到备份的ut/cases路径下
            String utFileStr = baseTestPath.substring(0, baseTestPath.length() - "/src/test/java".length()) + "/src/cases/ut/backup" + filePath;
            File utFile = new File(utFileStr);
            if (!utFile.getParentFile().exists()) {
                utFile.getParentFile().mkdirs();
            }
            FileWriter writer = new FileWriter(utFile);
            writer.write(testCaseStringBuilder.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static StringBuilder getTestCaseFileStr(String fileName, StringBuilder testCaseStringBuilder, ApiModel api, List<UtCase> cases) {
        //书写java代码的第一行，package
        testCaseStringBuilder.append("package ");
        testCaseStringBuilder.append(api.getPackageName());
        testCaseStringBuilder.append(";\n");
        testCaseStringBuilder.append("\n");
        //书写注释
        testCaseStringBuilder.append("/*\n");
        //注释：是自动创建的
        testCaseStringBuilder.append("Auto created by OB_Tester\n");
        //注释：创建的时间
        testCaseStringBuilder.append("Date:" + new Date() + "\n");
        //注释：常用断言
        testCaseStringBuilder.append("" +
                "Assert examples:\n" +
                "assertArrayEquals(\"fail msg\", expected, actual);\n" +
                "assertEquals(\"fail msg\", expected, actual);\n" +
                "assertTrue(\"fail msg\", true);\n" +
                "assertFalse(\"fail msg\", false);\n" +
                "assertNotNull(\"fail msg\", new Object());\n" +
                "assertNull(\"fail msg\", null);\n" +
                "assertNotSame(\"fail msg\", new Object(), new Object());\n" +
                "assertSame(\"fail msg\", aNumber, aNumber);\n" +
                "assertThat(\"albumen\", both(containsString(\"a\")).and(containsString(\"b\")));\n" +
                "assertThat(Arrays.asList(\"one\", \"two\", \"three\"), hasItems(\"one\", \"three\"));\n" +
                "assertThat(Arrays.asList(new String[] { \"fun\", \"ban\", \"net\" }), everyItem(containsString(\"n\")));\n" +
                "assertThat(\"good\", allOf(equalTo(\"good\"), startsWith(\"good\")));\n" +
                "assertThat(\"good\", not(allOf(equalTo(\"bad\"), equalTo(\"good\"))));\n" +
                "assertThat(\"good\", anyOf(equalTo(\"bad\"), equalTo(\"good\")));\n" +
                "assertThat(7, not(CombinableMatcher.<Integer> either(equalTo(3)).or(equalTo(4))));\n" +
                "assertThat(new Object(), not(sameInstance(new Object())));\n" +
                "fail(\"fail msg\");\n");
        testCaseStringBuilder.append("*/\n");
        testCaseStringBuilder.append("\n");
        //书写import
        testCaseStringBuilder = getImport(testCaseStringBuilder, api.getBootPath());
        //@RunWith
        testCaseStringBuilder.append("@RunWith(SpringJUnit4ClassRunner.class)\n");
        //@WebAppConfiguration
        testCaseStringBuilder.append("@WebAppConfiguration\n");
        //@ContextConfiguration必须声明
        testCaseStringBuilder.append("@ContextConfiguration(classes = " + api.getBootPath() + ".class)\n");
        //书写class
        testCaseStringBuilder.append("public class " + fileName + " {\n");
        //将待测的功能类注入@Autowired
        //定义MockMvc
        //定义@Before执行
        testCaseStringBuilder.append("" +
                "    @Autowired\n" +
                "    private " + api.getFileName() + " restController;\n" +
                "    private MockMvc mockMvc;\n" +
                "    \n" +
                "    @Before\n" +
                "    public void setUp() throws Exception {\n" +
                "        mockMvc = MockMvcBuilders.standaloneSetup(restController).build();\n" +
                "    }\n" +
                "    @After\n" +
                "    public void tearDown() throws Exception {\n" +
                "    }\n");
        //根据输入的测试用例，书写测试用例执行语句
        for (UtCase utCase : cases) {
            testCaseStringBuilder.append("" +
                    "    @Test\n" +
                    "    public void " + utCase.getName() + "() throws Exception {\n");
            testCaseStringBuilder.append("" +
                    "        RequestBuilder questbuild = " + getRequestStr(api, utCase) + "\n");
            testCaseStringBuilder.append("" +
                    "        ResultActions r = this.mockMvc.perform(questbuild);\n" +
                    "        MvcResult mvcResult = r.andReturn();\n" +
                    "        String result = mvcResult.getResponse().getContentAsString();\n");
            //书写断言，返回result不能为空
            testCaseStringBuilder.append("" +
                    "        assertNotNull(\"responseContent is null\",result);\n");
            //书写断言，判断返回result与测试用例的预期值是否相等
            testCaseStringBuilder = getAssertStr(utCase, testCaseStringBuilder);
            testCaseStringBuilder.append("    }\n");
        }
        testCaseStringBuilder.append("}\n");

        return testCaseStringBuilder;
    }

    static StringBuilder getAssertStr(UtCase utCase, StringBuilder strBuilder) {
        if (utCase.getOut() != null) {
            String value = utCase.getOut();
            /*书写断言语句。
            这里的逻辑是，判断返回字符串result和utCase设置的预期out,可能有三种情况
            1、都是{}
            2、都是[]
            3、不满足以上1或者2的场景
            */
            strBuilder.append("" +
                    "        String expected = \"" + value + "\";\n" +
                    "        if (result != null && !\"\".equalsIgnoreCase(result) && expected != null && !\"\".equalsIgnoreCase(expected)) {\n" +
                    "            if ((result.trim().startsWith(\"{\") && result.trim().endsWith(\"}\"))) {\n" +
                    "                if((expected.trim().startsWith(\"{\") && expected.trim().endsWith(\"}\"))){\n" +
                    "                    assertEquals(\"actual is not queals expected\", JSON.parseObject(expected),JSON.parseObject(result));\n" +
                    "                }\n" +
                    "            }else if ((result.trim().startsWith(\"[\") && result.trim().endsWith(\"]\"))) {\n" +
                    "                if((expected.trim().startsWith(\"[\") && expected.trim().endsWith(\"]\"))){\n" +
                    "                    assertEquals(\"actual is not queals expected\", JSON.parseArray(expected),JSON.parseArray(result));\n" +
                    "                }\n" +
                    "            }else{\n" +
                    "                assertEquals(\"actual is not queals expected\", expected,result);\n" +
                    "            }\n" +
                    "        }else{\n" +
                    "            assertEquals(\"actual is not queals expected\", expected,result);\n" +
                    "        }\n");

        }
        return strBuilder;
    }

    static String getRequestStr(ApiModel api, UtCase utCase) {
        /*
        书写MockMvcRequestBuilders语句，例如：MockMvcRequestBuilders.get(uri);
        关键点是将apiPath转化为可用的uri,有3种情况：
        1、apiPath含pathVar（{}结构）的，要替换成实际值，例如：/{id}替换成/1
        2、apiPath含query参数的，给MockMvcRequestBuilders增加.param()
        3、apiPath是queryBody输入的，给MockMvcRequestBuilders增加.content()
         */
        String result = "MockMvcRequestBuilders.";
        result += api.getApiType().toLowerCase();
        result += "(";
        String uri = api.getApiPath();
        String queryStr = "";
        String bodyStr = "";
        if (utCase != null && utCase.getIn() != null) {
            for (ArgsModel utArg : utCase.getIn()) {
                if (utArg.getT().equalsIgnoreCase("var")) {
                    uri = uri.replace("{" + utArg.getN() + "}", utArg.getV());
                } else if (utArg.getT().equalsIgnoreCase("body")) {
                    if ("".equalsIgnoreCase(bodyStr)) {
                        bodyStr += ".contentType(MediaType.APPLICATION_JSON)";
                    }
                    bodyStr += ".content(" + JSON.toJSONString(utArg.getV()) + ")";
                } else {
                    queryStr += ".param(\"" + utArg.getN() + "\",\"" + utArg.getV() + "\")";
                }
            }
        }
        result += "\"" + uri + "\")";
        result += queryStr;
        result += bodyStr;
        result += ";";
        return result;
    }

    static StringBuilder getImport(StringBuilder importStringBuilder, String bootPath) {
        //默认import
        importStringBuilder.append("import com.alibaba.fastjson.JSON;\n" +
                "import com.alibaba.fastjson.JSONObject;\n" +
                "import org.junit.After;\n" +
                "import org.junit.Before;\n" +
                "import org.junit.Test;\n" +
                "import org.springframework.http.MediaType;\n" +
                "import org.junit.runner.RunWith;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.test.context.ContextConfiguration;\n" +
                "import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;\n" +
                "import org.springframework.test.context.web.WebAppConfiguration;\n" +
                "import org.springframework.test.web.servlet.MockMvc;\n" +
                "import org.springframework.test.web.servlet.MvcResult;\n" +
                "import org.springframework.test.web.servlet.ResultActions;\n" +
                "import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;\n" +
                "import org.springframework.test.web.servlet.setup.MockMvcBuilders;\n" +
                "import org.springframework.test.web.servlet.RequestBuilder;\n" +
                "import static org.junit.Assert.fail;\n" +
                "import static org.junit.Assert.assertNotNull;\n" +
                "import static org.junit.Assert.assertEquals;\n");
        importStringBuilder.append("\n");
        importStringBuilder.append("//import springboot class\n");
        //这里很关键，import springboot class
        importStringBuilder.append("import " + bootPath + ";\n\n");
        return importStringBuilder;
    }
}
