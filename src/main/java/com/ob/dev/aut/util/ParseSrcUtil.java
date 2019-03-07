package com.ob.dev.aut.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.ob.dev.aut.model.ApiModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParseSrcUtil {
    /*
    分析指定路径下的代码，得到结构化的api模型数据，同时输出每个接口的ut用例输入输出数据模型
    api模型数据：List<ApiModel>
    ut用例输入输出数据模型: 保存在outModulePath文件
     */
    public static List<ApiModel> parseByPath(String path, String outModulePath) {
        File file = new File(path);
        List<ApiModel> apis = new ArrayList<ApiModel>();
        List<String> bootPath = new ArrayList<String>();
        //获得springboot 文件路径，一般来说一个springboot项目都只有一个
        bootPath = getBootPath(file, bootPath);
        //获得api模型数据，同时输出ut用例输入输出数据模型
        apis = getApis(bootPath.get(0), outModulePath, file, apis);
        return apis;
    }

    //获得api模型数据，同时输出ut用例输入输出数据模型
    private static List<ApiModel> getApis(String bootPath, String outModulePath, File filePath, List<ApiModel> apis) {
        File[] files = filePath.listFiles();
        if (files == null) {
            return apis;
        }
        for (File f : files) {
            //解析java源码文件逻辑
            if (f.isFile()) {
                String path = f.getPath();
                if (path.endsWith(".java") && !path.contains("/test/")) {
                    String fileName = f.getName();
                    fileName = fileName.substring(0, fileName.length() - 5);
                    ClassOrInterfaceDeclaration n = null;
                    CompilationUnit cu = null;
                    try {
                        FileInputStream in = new FileInputStream(f);
                        //使用JavaParser解析java源码得到结构化数据
                        cu = JavaParser.parse(in);
                        //获得java文件的Class或者Interface数据
                        n = cu.getClassByName(fileName).get().asClassOrInterfaceDeclaration();
                    } catch (Exception e) {

                    }
                    //重点分析含@RequestMapping字符串的ClassOrInterface
                    if (cu != null && n != null && n.toString().contains("@RequestMapping")) {
                        //源码文件class前面的@RequestMapping定义
                        //可能有的源码文件class前面没有@RequestMapping定义，则baseApiPath=""
                        String baseApiPath = getBaseApiPath(n);
                        //遍历class的内部方法
                        List<MethodDeclaration> methods = n.getMethods();
                        for (MethodDeclaration method : methods) {
                            //获取方法前面的@RequestMapping定义，转为结构化数据apiPath
                            JSONObject apiPath = getApiPath(method.getAnnotations());
                            //只有在结构化数据apiPath所有参数不为空时，才说明这个方法是API定义
                            if (apiPath != null && apiPath.get("t") != null && apiPath.get("p") != null) {
                                //转化为结构化的ApiModel
                                ApiModel apiModel = new ApiModel();
                                apiModel.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                                apiModel.setBootPath(bootPath);
                                apiModel.setPackageName(cu.getPackageDeclaration().get().getName().toString());
                                apiModel.setFileName(fileName);
                                apiModel.setApiName(method.getNameAsString());
                                apiModel.setApiOutput(method.getType().toString());
                                apiModel.setApiInput(JSON.toJSONString(getApiInput(method.getNameAsString(), method.getDeclarationAsString())));
                                apiModel.setApiPath(baseApiPath + apiPath.getString("p"));
                                apiModel.setApiType(apiPath.getString("t"));
                                //添加到apis
                                apis.add(apiModel);
                                //保存接口的输入输出参数到outModulePath
                                saveModule(apiModel, outModulePath);
                            }
                        }
                    }
                }
            } else {
                getApis(bootPath, outModulePath, f, apis);
            }
        }
        return apis;
    }

    //保存接口的输入输出参数到outModulePath
    static void saveModule(ApiModel apiModel, String outModulePath) {
        String path = outModulePath;
        path += "/" + apiModel.getPackageName().replaceAll("\\.", "/");
        path += "/" + apiModel.getFileName();
        path += "/" + apiModel.getApiType() + "_" + apiModel.getApiName();
        path += "/" + "module.json";
        /*构建module.json的源码
            {
                "in":[
                    {
                        "t":"",
                        "n":"",
                        "v":""
                    }
                ],
                "out":""
            }
         其中:
         t=type
         n=name
         v=value
         */
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("" +
                "{\n" +
                "\t\"in\": [");
        if (apiModel.getApiInput() != null && !"".equalsIgnoreCase(apiModel.getApiInput())) {
            JSONArray jArr = JSON.parseArray(apiModel.getApiInput());
            int token = 1;
            for (Object thisObj : jArr) {
                JSONObject thisJson = (JSONObject) thisObj;
                stringBuilder.append("" +
                        "\n\t\t{\n" +
                        "\t\t\t\"t\":\"" + thisJson.getString("t") + "\",\n" +
                        "\t\t\t\"n\":\"" + thisJson.getString("n") + "\",\n" +
                        "\t\t\t\"v\":\"" + thisJson.getString("dt") + "\"\n" +
                        "\t\t}");
                if (token < jArr.size()) {
                    stringBuilder.append(",");
                }
                token++;
            }

        }
        stringBuilder.append("" +
                "\n\t],\n" +
                "\t\"out\": \"\"\n" +
                "}");
        try {
            //file.createNewFile();
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileWriter writer = new FileWriter(path);

            writer.write(stringBuilder.toString());
            writer.flush();
            writer.close();

        } catch (Exception e) {

        }
    }

    //分析结构化数据NodeList<AnnotationExpr>，获得接口数据
    static JSONObject getApiPath(NodeList<AnnotationExpr> annotations) {
        JSONObject result = new JSONObject();
        for (AnnotationExpr annotation : annotations) {
            //获得接口的请求方法
            if (annotation.toString().contains("RequestMethod.")) {
                if (annotation.toString().contains("RequestMethod.GET")) {
                    result.put("t", "get");
                } else if (annotation.toString().contains("RequestMethod.POST")) {
                    result.put("t", "post");
                } else if (annotation.toString().contains("RequestMethod.PUT")) {
                    result.put("t", "put");
                } else if (annotation.toString().contains("RequestMethod.DELETE")) {
                    result.put("t", "delete");
                } else {
                    result.put("t", "");
                }
                //获得接口路径，例如：/hello/{id}
                //注意这里获取的路径不是API的全路径，只是API路径的一部分
                for (Node child : annotation.getChildNodes()) {
                    String path = child.toString().replaceAll(" ", "");
                    if (path.startsWith("value=\"/")) {
                        path = path.substring("value=\"".length());
                        path = path.replaceAll("\"", "");
                        result.put("p", path);
                    }
                }
            }
        }
        return result;
    }

    //分析结构化数据ClassOrInterfaceDeclaration，获得当前源码文件定义的RequestMapping
    static String getBaseApiPath(ClassOrInterfaceDeclaration n) {
        String baseApiParh = "";
        NodeList<AnnotationExpr> classAnnotations = n.getAnnotations();
        for (AnnotationExpr classAnnotation : classAnnotations) {
            if (classAnnotation.toString().contains("@RequestMapping") && classAnnotation.toString().contains("/")) {
                List<Node> childNodes = classAnnotation.getChildNodes();
                for (Node childNode : childNodes) {
                    if (childNode.toString().startsWith("\"/") && childNode.toString().endsWith("\"")) {
                        return childNode.toString().replaceAll("\"", "");
                    }
                    if (childNode.toString().startsWith("'/") && childNode.toString().endsWith("'")) {
                        return childNode.toString().replaceAll("'", "");
                    }
                }
            }
        }
        return baseApiParh;
    }

    //分析结构化数据declaration，获得接口的输入参数
    public static JSONArray getApiInput(String apiName, String declarationAsString) {
        JSONArray inputList = new JSONArray();
        String inputStr = declarationAsString.substring(declarationAsString.indexOf(apiName) + apiName.length());
        if (inputStr.contains("@RequestParam") || inputStr.contains("@PathVariable") || inputStr.contains("@RequestBody")) {

            inputStr = inputStr.trim();
            inputStr = inputStr.replaceAll("  ", " ");
            inputStr = inputStr.replaceAll("required ", "required");
            inputStr = inputStr.replaceAll(" false", "false");
            inputStr = inputStr.replaceAll(" true", "true");
            inputStr = inputStr.replaceAll("\\( ", "\\(");
            inputStr = inputStr.replaceAll(" \\)", "\\)");
            inputStr = inputStr.replaceAll("  ", " ");
            inputStr = inputStr.replaceAll(" @", "@");
            if (inputStr.endsWith(")")) {
                inputStr = inputStr.substring(0, inputStr.length() - 1);
            }
            if (inputStr.contains(") throws")) {
                inputStr = inputStr.substring(0, inputStr.indexOf(") throws"));
            }
            //System.out.println("inputStr=2===" + inputStr);
            String[] inputArr = inputStr.split(",");

            for (String str : inputArr) {
                str = str.trim();
                boolean hasToken = false;
                //System.out.println("*******str=" + str);
                if (str.indexOf("@RequestParam") > -1) {
                    hasToken = true;
                    str = str.substring(str.indexOf("@RequestParam"));
                }
                if (str.indexOf("@RequestBody") > -1) {
                    hasToken = true;
                    str = str.substring(str.indexOf("@RequestBody"));
                }
                if (str.indexOf("@PathVariable") > -1) {
                    hasToken = true;
                    str = str.substring(str.indexOf("@PathVariable"));
                }

                String[] strArr = str.split(" ");

                //System.out.println("*******hasToken=" + hasToken);
                if (strArr.length > 2 && hasToken) {
                    JSONObject inArgs = new JSONObject();
                    if (strArr[0].toString().contains("PathVariable")) {
                        inArgs.put("t", "var");
                    } else if (strArr[0].toString().contains("RequestParam")) {
                        inArgs.put("t", "query");
                    } else {
                        inArgs.put("t", "body");
                    }

                    inArgs.put("dt", strArr[strArr.length - 2]);
                    inArgs.put("n", strArr[strArr.length - 1]);
                    //System.out.println(inArgs);
                    inputList.add(inArgs);
                }
            }
        }
        //System.out.println("*****=" + inputList);
        if (inputList.size() == 0) {
            System.out.println(apiName + inputStr + ", it is no input args");
        }
        return inputList;
    }

    //获取springboot class
    static List<String> getBootPath(File filePath, List<String> result) {
        File[] files = filePath.listFiles();
        if (files == null) {
            return result;
        }
        for (File f : files) {
            if (f.isFile()) {
                String path = f.getPath();
                if (path.endsWith(".java") && !path.contains("/test/")) {
                    String fileName = f.getName();
                    fileName = fileName.substring(0, fileName.length() - 5);
                    ClassOrInterfaceDeclaration n = null;
                    CompilationUnit cu = null;
                    try {
                        FileInputStream in = new FileInputStream(f);
                        cu = JavaParser.parse(in);
                        n = cu.getClassByName(fileName).get().asClassOrInterfaceDeclaration();
                    } catch (Exception e) {

                    }
                    if (cu != null && n != null && n.toString().contains("@SpringBootApplication")) {

                        String packName = cu.getPackageDeclaration().get().getName().toString();
                        if (packName != null && !"".equalsIgnoreCase(packName)) {
                            result.add(packName + "." + fileName);

                        } else {
                            result.add(fileName);
                        }
                    }
                }
            } else {
                getBootPath(f, result);
            }
        }
        return result;
    }
}
