// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.sample.api;

import io.github.o1o00o10.core.annotation.LingxiApi;
import io.github.o1o00o10.core.annotation.LingxiParam;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface UserApi {
  @LingxiApi(
      path = "/users/{id}",
      method = RequestMethod.GET,
      tradeCode = "USER_QUERY",
      auth = false)
  UserView get(@PathVariable("id") Long id, @LingxiParam("traceId") String traceId);

  @LingxiApi(path = "/users", method = RequestMethod.POST, tradeCode = "USER_CREATE", auth = false)
  UserView create(@RequestBody @Valid CreateUser request);

  @LingxiApi(
      path = "/users/{id}/async",
      method = RequestMethod.GET,
      tradeCode = "USER_ASYNC",
      auth = false)
  CompletableFuture<UserView> async(@PathVariable("id") Long id);

  @LingxiApi(
      path = "/users/search",
      method = RequestMethod.GET,
      tradeCode = "USER_SEARCH",
      auth = false)
  OperationView search(
      @RequestParam("keyword") String keyword,
      @RequestHeader("X-Channel") String channel,
      @CookieValue("locale") String locale);

  @LingxiApi(
      path = "/users/{id}",
      method = RequestMethod.PUT,
      tradeCode = "USER_UPDATE",
      auth = false)
  OperationView update(@PathVariable("id") Long id, @RequestBody @Valid CreateUser request);

  @LingxiApi(
      path = "/users/{id}",
      method = RequestMethod.DELETE,
      tradeCode = "USER_DELETE",
      auth = false)
  OperationView delete(@PathVariable("id") Long id);

  @LingxiApi(
      path = "/users/{id}",
      method = RequestMethod.PATCH,
      tradeCode = "USER_PATCH",
      auth = false)
  OperationView patch(@PathVariable("id") Long id, @RequestParam("name") String name);

  @LingxiApi(
      path = "/async/callable",
      method = RequestMethod.GET,
      tradeCode = "ASYNC_CALLABLE",
      auth = false)
  Callable<OperationView> callable();

  @LingxiApi(
      path = "/async/deferred",
      method = RequestMethod.GET,
      tradeCode = "ASYNC_DEFERRED",
      auth = false)
  DeferredResult<OperationView> deferred();

  @LingxiApi(
      path = "/async/web-task",
      method = RequestMethod.GET,
      tradeCode = "ASYNC_WEB_TASK",
      auth = false)
  WebAsyncTask<OperationView> webAsyncTask();

  @LingxiApi(
      path = "/raw",
      method = RequestMethod.GET,
      tradeCode = "RAW_RESPONSE",
      auth = false,
      wrapResponse = false)
  OperationView raw();

  @LingxiApi(
      path = "/files",
      method = RequestMethod.POST,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      tradeCode = "FILE_UPLOAD",
      auth = false)
  FileView upload(@RequestPart("file") MultipartFile file);

  @LingxiApi(
      path = "/context",
      method = RequestMethod.GET,
      tradeCode = "CONTEXT_QUERY",
      auth = false)
  OperationView context(
      @LingxiParam(value = "tenant", defaultValue = "public") String tenant,
      @LingxiParam(value = "optional", required = false) String optional);

  @LingxiApi(path = "/text", method = RequestMethod.GET, tradeCode = "TEXT_QUERY", auth = false)
  String text();

  @LingxiApi(
      path = "/events",
      method = RequestMethod.GET,
      produces = MediaType.TEXT_EVENT_STREAM_VALUE,
      tradeCode = "EVENT_STREAM",
      auth = false)
  SseEmitter events();

  @LingxiApi(
      path = "/errors/duplicate",
      method = RequestMethod.GET,
      tradeCode = "ERROR_DUPLICATE",
      auth = false)
  OperationView duplicate();

  @LingxiApi(
      path = "/errors/unexpected",
      method = RequestMethod.GET,
      tradeCode = "ERROR_UNEXPECTED",
      auth = false)
  OperationView unexpected();

  class CreateUser {
    @NotBlank private String name;

    public String getName() {
      return name;
    }

    public void setName(String value) {
      name = value;
    }
  }

  class UserView {
    private final Long id;
    private final String name;
    private final String traceId;

    public UserView(Long id, String name, String traceId) {
      this.id = id;
      this.name = name;
      this.traceId = traceId;
    }

    public Long getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getTraceId() {
      return traceId;
    }
  }

  class FileView {
    private final String name;
    private final long size;

    public FileView(String name, long size) {
      this.name = name;
      this.size = size;
    }

    public String getName() {
      return name;
    }

    public long getSize() {
      return size;
    }
  }

  class OperationView {
    private final String operation;
    private final String value;

    public OperationView(String operation, String value) {
      this.operation = operation;
      this.value = value;
    }

    public String getOperation() {
      return operation;
    }

    public String getValue() {
      return value;
    }
  }
}
