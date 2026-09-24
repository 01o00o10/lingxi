// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.o1o00o10.web.mapping.LingxiApiRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(properties = {"logging.level.root=ERROR", "debug=false"})
@AutoConfigureMockMvc
class LingxiIntegrationTest {
  @Autowired MockMvc mockMvc;
  @Autowired LingxiApiRegistry registry;

  @Test
  void registersInterfaceMethodsAndWrapsResponse() throws Exception {
    assertThat(registry.definitions()).hasSize(17);
    mockMvc
        .perform(get("/api/v1/users/7").header("X-Trace-Id", "trace-test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0000000"))
        .andExpect(jsonPath("$.data.id").value(7))
        .andExpect(jsonPath("$.data.traceId").value("trace-test"));
  }

  @Test
  void validatesRequestBody() throws Exception {
    mockMvc
        .perform(post("/api/v1/users").contentType("application/json").content("{\"name\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("LX01001"));
  }

  @Test
  void supportsCompletableFuture() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/users/9/async"))
            .andExpect(request().asyncStarted())
            .andReturn();
    mockMvc
        .perform(asyncDispatch(result))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.name").value("Async-9"));
  }

  @Test
  void supportsMultipart() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "demo.txt", "text/plain", "hello".getBytes("UTF-8"));
    mockMvc
        .perform(multipart("/api/v1/files").file(file))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.name").value("demo.txt"))
        .andExpect(jsonPath("$.data.size").value(5));
  }

  @Test
  void supportsAllHttpMethodsAndRequestBody() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/users/7").contentType("application/json").content("{\"name\":\"Neo\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.operation").value("update"))
        .andExpect(jsonPath("$.data.value").value("7:Neo"));
    mockMvc
        .perform(patch("/api/v1/users/7").param("name", "Trinity"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.value").value("7:Trinity"));
    mockMvc
        .perform(delete("/api/v1/users/7"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.operation").value("delete"));
  }

  @Test
  void bindsQueryHeaderAndCookie() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/users/search")
                .param("keyword", "lingxi")
                .header("X-Channel", "MOBILE")
                .cookie(new javax.servlet.http.Cookie("locale", "zh-CN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.value").value("lingxi:MOBILE:zh-CN"));
  }

  @Test
  void reportsMissingRequiredParametersWithoutLeakingDetails() throws Exception {
    mockMvc
        .perform(get("/api/v1/users/search"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("LX01001"))
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  void supportsCallableDeferredResultAndWebAsyncTask() throws Exception {
    assertAsyncValue("/api/v1/async/callable", "callable");
    assertAsyncValue("/api/v1/async/deferred", "deferred");
    assertAsyncValue("/api/v1/async/web-task", "web-task");
  }

  @Test
  void canDisableResponseWrappingPerContractMethod() throws Exception {
    mockMvc
        .perform(get("/api/v1/raw"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.operation").value("raw"))
        .andExpect(jsonPath("$.code").doesNotExist());
  }

  @Test
  void exposesSanitizedDiagnosticsWhenEnabled() throws Exception {
    mockMvc
        .perform(get("/lingxi/diagnostics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"))
        .andExpect(jsonPath("$.routeCount").value(17))
        .andExpect(jsonPath("$.interceptorCount").value(1))
        .andExpect(jsonPath("$.routes[0].tradeCode").isNotEmpty())
        .andExpect(
            jsonPath("$.warnings[0]")
                .value(
                    "The diagnostics endpoint is exposed while Lingxi authentication is disabled"))
        .andExpect(jsonPath("$.secret").doesNotExist());
  }

  @Test
  void resolvesInterceptorExtensionsDefaultsAndOptionalParameters() throws Exception {
    mockMvc
        .perform(get("/api/v1/context").header("X-Tenant", "tenant-a"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.value").value("tenant-a:null"));
    mockMvc
        .perform(get("/api/v1/context"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.value").value("public:null"));
  }

  @Test
  void wrapsStringResponsesAsJson() throws Exception {
    mockMvc
        .perform(get("/api/v1/text"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"))
        .andExpect(jsonPath("$.code").value("0000000"))
        .andExpect(jsonPath("$.data").value("lingxi"));
  }

  @Test
  void streamsServerSentEventsWithoutResponseWrapping() throws Exception {
    MvcResult result =
        mockMvc.perform(get("/api/v1/events")).andExpect(request().asyncStarted()).andReturn();
    mockMvc
        .perform(asyncDispatch(result))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("event:ready")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("data:lingxi")));
  }

  @Test
  void mapsBusinessErrorsToTheirHttpStatus() throws Exception {
    mockMvc
        .perform(get("/api/v1/errors/duplicate"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("LX03001"))
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  void sanitizesUnexpectedErrors() throws Exception {
    mockMvc
        .perform(get("/api/v1/errors/unexpected"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value("LX05001"))
        .andExpect(jsonPath("$.message").value("Internal server error"))
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("implementation detail"))));
  }

  private void assertAsyncValue(String path, String value) throws Exception {
    MvcResult result = mockMvc.perform(get(path)).andExpect(request().asyncStarted()).andReturn();
    mockMvc
        .perform(asyncDispatch(result))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0000000"))
        .andExpect(jsonPath("$.data.value").value(value));
  }
}
