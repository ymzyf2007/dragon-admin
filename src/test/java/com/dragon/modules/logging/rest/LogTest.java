package com.dragon.modules.logging.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testQuery() throws Exception {
        // perform: 执行请求
        // MockMvcRequestBuilders.get("/url"): 构造一个get请求
        mockMvc.perform(MockMvcRequestBuilders.get("/api/logs")
        .contentType(MediaType.APPLICATION_JSON))
                // 期望的结果状态 200
                .andExpect(MockMvcResultMatchers.status().isOk());
                // 期望的返回结果集合有3个元素 ， $: 返回结果
//                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
    }

    @Test
    public void queryUserLog() throws Exception {
        // perform: 执行请求
        // MockMvcRequestBuilders.get("/url"): 构造一个get请求
        mockMvc.perform(MockMvcRequestBuilders.get("/api/logs/user")
                .contentType(MediaType.APPLICATION_JSON))
                // 期望的结果状态 200
                .andExpect(MockMvcResultMatchers.status().isOk());
        // 期望的返回结果集合有3个元素 ， $: 返回结果
//                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
    }

}
