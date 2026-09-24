// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.autoconfigure;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "lingxi")
public class LingxiProperties {
  private boolean enabled = true;
  private boolean enforceApiOnly;
  private String[] basePackage = new String[0];
  private String prefix = "/api";
  private final Response response = new Response();
  private final Interceptor interceptor = new Interceptor();
  private final Auth auth = new Auth();
  private final Async async = new Async();
  private final File file = new File();
  private final Idempotent idempotent = new Idempotent();
  private final Diagnostics diagnostics = new Diagnostics();

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean v) {
    enabled = v;
  }

  public boolean isEnforceApiOnly() {
    return enforceApiOnly;
  }

  public void setEnforceApiOnly(boolean v) {
    enforceApiOnly = v;
  }

  public String[] getBasePackage() {
    return basePackage;
  }

  public void setBasePackage(String[] v) {
    basePackage = v;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String v) {
    prefix = v;
  }

  public Response getResponse() {
    return response;
  }

  public Interceptor getInterceptor() {
    return interceptor;
  }

  public Auth getAuth() {
    return auth;
  }

  public Async getAsync() {
    return async;
  }

  public File getFile() {
    return file;
  }

  public Idempotent getIdempotent() {
    return idempotent;
  }

  public Diagnostics getDiagnostics() {
    return diagnostics;
  }

  public static class Response {
    private boolean wrap = true;

    public boolean isWrap() {
      return wrap;
    }

    public void setWrap(boolean v) {
      wrap = v;
    }
  }

  public static class Interceptor {
    private boolean enabled = true;
    private List<String> global = new ArrayList<String>();

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean v) {
      enabled = v;
    }

    public List<String> getGlobal() {
      return global;
    }

    public void setGlobal(List<String> v) {
      global = v;
    }
  }

  public static class Auth {
    private boolean enabled = true;
    private final Jwt jwt = new Jwt();

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean v) {
      enabled = v;
    }

    public Jwt getJwt() {
      return jwt;
    }

    public static class Jwt {
      private String secret = "change-me-change-me-change-me-change-me";
      private Duration expire = Duration.ofHours(2);
      private String header = "Authorization";
      private String prefix = "Bearer ";

      public String getSecret() {
        return secret;
      }

      public void setSecret(String v) {
        secret = v;
      }

      public Duration getExpire() {
        return expire;
      }

      public void setExpire(Duration v) {
        expire = v;
      }

      public String getHeader() {
        return header;
      }

      public void setHeader(String v) {
        header = v;
      }

      public String getPrefix() {
        return prefix;
      }

      public void setPrefix(String v) {
        prefix = v;
      }
    }
  }

  public static class Async {
    private boolean enabled = true;
    private Duration defaultTimeout = Duration.ofSeconds(60);

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean v) {
      enabled = v;
    }

    public Duration getDefaultTimeout() {
      return defaultTimeout;
    }

    public void setDefaultTimeout(Duration v) {
      defaultTimeout = v;
    }
  }

  public static class File {
    private boolean enabled = true;
    private DataSize maxSize = DataSize.ofMegabytes(10);
    private boolean chunkUpload;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean v) {
      enabled = v;
    }

    public DataSize getMaxSize() {
      return maxSize;
    }

    public void setMaxSize(DataSize v) {
      maxSize = v;
    }

    public boolean isChunkUpload() {
      return chunkUpload;
    }

    public void setChunkUpload(boolean v) {
      chunkUpload = v;
    }
  }

  public static class Idempotent {
    private boolean enabled = false;
    private Duration ttl = Duration.ofMinutes(10);
    private String keyPrefix = "lingxi:idempotent:";

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean v) {
      enabled = v;
    }

    public Duration getTtl() {
      return ttl;
    }

    public void setTtl(Duration v) {
      ttl = v;
    }

    public String getKeyPrefix() {
      return keyPrefix;
    }

    public void setKeyPrefix(String v) {
      keyPrefix = v;
    }
  }

  public static class Diagnostics {
    private boolean logOnStartup = true;
    private boolean endpointEnabled;
    private String path = "/lingxi/diagnostics";

    public boolean isLogOnStartup() {
      return logOnStartup;
    }

    public void setLogOnStartup(boolean logOnStartup) {
      this.logOnStartup = logOnStartup;
    }

    public boolean isEndpointEnabled() {
      return endpointEnabled;
    }

    public void setEndpointEnabled(boolean endpointEnabled) {
      this.endpointEnabled = endpointEnabled;
    }

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }
  }
}
