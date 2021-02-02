package org.verwandlung.voj.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.xdevapi.JsonArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.verwandlung.voj.web.controller.AccountsController;
import org.verwandlung.voj.web.model.ProblemTag;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FpsProblemService {

    @Autowired
    private ProblemService problemService;

    //将fps转换为问题类型，调用这个就行了
    public Map<String, Object> FPStoProblems(String filepath) {
        Document doc;
        Map<String, Object> result = null;
        doc = parseXML(filepath);	//能够解析xml
        System.out.println(doc.toString());
        NodeList itemList = doc.getElementsByTagName("item");
        for (int i = 0; i < itemList.getLength(); i++) {    //遍历所有问题，逐个加入
            result = itemToProblem(itemList.item(i));
        }
        return result;
    }

    //用来将转换为problem类，在这里传到后端，这是多个问题中的其中一个
    public Map<String, Object> itemToProblem(Node item) {
        // TODO Auto-generated method stub
        int index = 0;
        String problemName = null,timeLimit = null,memoryLimit = null,description = null,inputFormat = null,
                outputFormat = null,inputSample = null,outputSample = null,hint = null;
        List<testCase> testCaseList = new ArrayList<>();
        NodeList ch = item.getChildNodes();
        for (int i = 0; i < ch.getLength(); i++) {
            Node e = ch.item(i);
            String name = e.getNodeName();
            String value = e.getTextContent();
            if (name.equalsIgnoreCase("title")) {
                problemName = value;
            }
            else if (name.equalsIgnoreCase("time_limit")) {
                timeLimit = value;
            }
            else if (name.equalsIgnoreCase("memory_limit")) {
                memoryLimit = value;
            }
            else if (name.equalsIgnoreCase("description")) {
                description = value;
            }
            else if (name.equalsIgnoreCase("input")) {
                inputFormat = "".equals(value)?"无":value;
            }
            else if (name.equalsIgnoreCase("output")) {
                outputFormat = "".equals(value)?"无":value;
            }
            else if (name.equalsIgnoreCase("sample_input")) {
                inputSample = "".equals(value)?"无":value;
            }
            else if (name.equalsIgnoreCase("sample_output")) {
                outputSample = "".equals(value)?"无":value;
            }
            else if (name.equalsIgnoreCase("hint")) {
                hint = value;
            }
            else if (name.equalsIgnoreCase("test_input")) {
                testCase testCase = new testCase();
                testCase.setInput(value);
                testCaseList.add(testCase);
            }
            else if (name.equalsIgnoreCase("test_output")) {
                testCase testCase = testCaseList.get(index);
                testCase.setOutput(value);
                testCaseList.set(index,testCase);
                index++;
            }
        }
        String testCases = JSONObject.toJSONString(testCaseList);
        LOGGER.debug(String.format("problemName=%s, timeLimit=%s, memoryLimit=%s, description=%s, inputFormat=%s," +
                        "outputFormat=%s, inputSample=%s, outputSample=%s, hint=%s, testCase=%s",
                problemName,timeLimit,memoryLimit,description,inputFormat,outputFormat,inputSample,outputSample,hint,testCases));
        Map<String, Object> result = problemService.createProblem(problemName, Integer.parseInt(timeLimit)*1000,
                Integer.parseInt(memoryLimit)*1024, description, hint, inputFormat, outputFormat, inputSample,
                outputSample, testCases,"[]","[]", true, true);
        return result;
    }

    //语法分析xml，解析xml
    public Document parseXML(String filepath) {
        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(filepath);
            doc.normalize();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * 日志记录器.
     */
    private static final Logger LOGGER = LogManager.getLogger(AccountsController.class);
}

class testCase{
    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    private String input;
    private String output;
}
