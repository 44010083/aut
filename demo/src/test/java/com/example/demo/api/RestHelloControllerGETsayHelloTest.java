package com.example.demo.api;

/*
Auto created by OB_Tester
Date:Wed Mar 13 10:15:30 CST 2019
Assert examples:
assertArrayEquals("fail msg", expected, actual);
assertEquals("fail msg", expected, actual);
assertTrue("fail msg", true);
assertFalse("fail msg", false);
assertNotNull("fail msg", new Object());
assertNull("fail msg", null);
assertNotSame("fail msg", new Object(), new Object());
assertSame("fail msg", aNumber, aNumber);
assertThat("albumen", both(containsString("a")).and(containsString("b")));
assertThat(Arrays.asList("one", "two", "three"), hasItems("one", "three"));
assertThat(Arrays.asList(new String[] { "fun", "ban", "net" }), everyItem(containsString("n")));
assertThat("good", allOf(equalTo("good"), startsWith("good")));
assertThat("good", not(allOf(equalTo("bad"), equalTo("good"))));
assertThat("good", anyOf(equalTo("bad"), equalTo("good")));
assertThat(7, not(CombinableMatcher.<Integer> either(equalTo(3)).or(equalTo(4))));
assertThat(new Object(), not(sameInstance(new Object())));
fail("fail msg");
*/

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.RequestBuilder;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

//import springboot class
import com.example.demo.DemoApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = com.example.demo.DemoApplication.class)
public class RestHelloControllerGETsayHelloTest {
    @Autowired
    private RestHelloController restController;
    private MockMvc mockMvc;
    
    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(restController).build();
    }
    @After
    public void tearDown() throws Exception {
    }
    @Test
    public void testCase1_1552443330046() throws Exception {
        RequestBuilder questbuild = MockMvcRequestBuilders.get("/hello");
        ResultActions r = this.mockMvc.perform(questbuild);
        MvcResult mvcResult = r.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull("responseContent is null",result);
        String expected = "hello";
        if (result != null && !"".equalsIgnoreCase(result) && expected != null && !"".equalsIgnoreCase(expected)) {
            if ((result.trim().startsWith("{") && result.trim().endsWith("}"))) {
                if((expected.trim().startsWith("{") && expected.trim().endsWith("}"))){
                    assertEquals("actual is not queals expected", JSON.parseObject(expected),JSON.parseObject(result));
                }
            }else if ((result.trim().startsWith("[") && result.trim().endsWith("]"))) {
                if((expected.trim().startsWith("[") && expected.trim().endsWith("]"))){
                    assertEquals("actual is not queals expected", JSON.parseArray(expected),JSON.parseArray(result));
                }
            }else{
                assertEquals("actual is not queals expected", expected,result);
            }
        }else{
            assertEquals("actual is not queals expected", expected,result);
        }
    }
}
