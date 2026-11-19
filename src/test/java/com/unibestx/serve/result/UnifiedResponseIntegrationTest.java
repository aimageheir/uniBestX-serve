package com.unibestx.serve.result;

import com.unibestx.serve.entity.User;
import com.unibestx.serve.exception.BusinessException;
import com.unibestx.serve.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UnifiedResponseIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SampleController())
                .setControllerAdvice(new GlobalResponseAdvice(JsonMapper.builder().build()),
                        new GlobalExceptionHandler())
                .build();
    }

    @Test
    void wrapsPlainObject() throws Exception {
        mockMvc.perform(get("/object"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("成功"))
                .andExpect(jsonPath("$.data.name").value("tom"));
    }

    @Test
    void wrapsVoidReturn() throws Exception {
        mockMvc.perform(get("/void"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void wrapsTextReturnAsJson() throws Exception {
        mockMvc.perform(get("/text"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("hello"));
    }

    @Test
    void wrapsListReturn() throws Exception {
        mockMvc.perform(get("/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0]").value("a"));
    }

    @Test
    void businessExceptionSetsMatchingHttpStatus() throws Exception {
        mockMvc.perform(get("/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("无权限访问"));
    }

    @Test
    void businessExceptionWithCustomCodeFallsBackToBadRequest() throws Exception {
        mockMvc.perform(get("/custom"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1001));
    }

    @Test
    void wrapsResponseEntityBodyAndKeepsStatusAndHeaders() throws Exception {
        mockMvc.perform(get("/response-entity"))
                .andExpect(status().isOk())
                .andExpect(header().string("token", "abc123456"))
                .andExpect(header().string("X-Custom", "hello"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("成功"))
                .andExpect(jsonPath("$.data.username").value("张三"));
    }

    @Test
    void responseEntityWithErrorStatusStillGetsWrappedAsSuccessBody() throws Exception {
        mockMvc.perform(get("/response-entity-error"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("李四"));
    }

    @Test
    void responseEntityUserVoKeepsStatusAndWrapsBody() throws Exception {
        mockMvc.perform(get("/user-vo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(123));
    }

    @RestController
    static class SampleController {

        @GetMapping("/object")
        public Map<String, String> object() {
            return Map.of("name", "tom");
        }

        @GetMapping("/void")
        public void voidMethod() {
        }

        @GetMapping("/text")
        public String text() {
            return "hello";
        }

        @GetMapping("/list")
        public List<String> list() {
            return List.of("a", "b");
        }

        @GetMapping("/forbidden")
        public void forbidden() {
            throw new BusinessException(403, "无权限访问");
        }

        @GetMapping("/custom")
        public void custom() {
            throw new BusinessException(1001, "自定义业务错误");
        }

        @GetMapping("/response-entity")
        public org.springframework.http.ResponseEntity<User> responseEntity() {
            User user = new User();
            user.setId(1L);
            user.setUsername("张三");
            return org.springframework.http.ResponseEntity
                    .status(org.springframework.http.HttpStatus.OK)
                    .header("token", "abc123456")
                    .header("X-Custom", "hello")
                    .body(user);
        }

        @GetMapping("/response-entity-error")
        public org.springframework.http.ResponseEntity<User> responseEntityError() {
            User user = new User();
            user.setId(2L);
            user.setUsername("李四");
            return org.springframework.http.ResponseEntity
                    .status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body(user);
        }

        @GetMapping("/user-vo")
        public org.springframework.http.ResponseEntity<com.unibestx.serve.vo.UserVO> userVo() {
            com.unibestx.serve.vo.UserVO userVO = new com.unibestx.serve.vo.UserVO();
            userVO.setId(123L);
            userVO.setUsername("username");
            return org.springframework.http.ResponseEntity
                    .status(org.springframework.http.HttpStatus.OK)
                    .body(userVO);
        }
    }
}
