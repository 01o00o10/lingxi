// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.sample.service;

import io.github.o1o00o10.core.error.LingxiStandardErrorCode;
import io.github.o1o00o10.core.exception.LingxiException;
import io.github.o1o00o10.sample.api.UserApi;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class UserService implements UserApi {
  private final AtomicLong ids = new AtomicLong(1);

  public UserView get(Long id, String traceId) {
    return new UserView(id, "User-" + id, traceId);
  }

  public UserView create(CreateUser request) {
    return new UserView(ids.getAndIncrement(), request.getName(), null);
  }

  @Async
  public CompletableFuture<UserView> async(Long id) {
    return CompletableFuture.completedFuture(new UserView(id, "Async-" + id, null));
  }

  public OperationView search(String keyword, String channel, String locale) {
    return new OperationView("search", keyword + ":" + channel + ":" + locale);
  }

  public OperationView update(Long id, CreateUser request) {
    return new OperationView("update", id + ":" + request.getName());
  }

  public OperationView delete(Long id) {
    return new OperationView("delete", String.valueOf(id));
  }

  public OperationView patch(Long id, String name) {
    return new OperationView("patch", id + ":" + name);
  }

  public Callable<OperationView> callable() {
    return () -> new OperationView("async", "callable");
  }

  public DeferredResult<OperationView> deferred() {
    DeferredResult<OperationView> result = new DeferredResult<OperationView>();
    result.setResult(new OperationView("async", "deferred"));
    return result;
  }

  public WebAsyncTask<OperationView> webAsyncTask() {
    return new WebAsyncTask<OperationView>(() -> new OperationView("async", "web-task"));
  }

  public OperationView raw() {
    return new OperationView("raw", "unwrapped");
  }

  public FileView upload(MultipartFile file) {
    return new FileView(file.getOriginalFilename(), file.getSize());
  }

  public OperationView context(String tenant, String optional) {
    return new OperationView("context", tenant + ":" + optional);
  }

  public String text() {
    return "lingxi";
  }

  public SseEmitter events() {
    SseEmitter emitter = new SseEmitter();
    try {
      emitter.send(SseEmitter.event().name("ready").data("lingxi"));
      emitter.complete();
    } catch (IOException ex) {
      emitter.completeWithError(ex);
    }
    return emitter;
  }

  public OperationView duplicate() {
    throw new LingxiException(LingxiStandardErrorCode.DUPLICATE_REQUEST);
  }

  public OperationView unexpected() {
    throw new IllegalStateException("internal implementation detail");
  }
}
