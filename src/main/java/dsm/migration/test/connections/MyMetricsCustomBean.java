package dsm.migration.test.connections;

import io.micrometer.core.instrument.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.net.*;
import java.util.function.*;

@Component
@EnableScheduling
public class MyMetricsCustomBean {


  public Supplier<Number> isBackendAccessable(String serverUrl,String proxyUrl) {
    Supplier<Number> isReachable = getStatusNOK();
    if (proxyUrl != null) {

      String hostProxy = proxyUrl.substring((proxyUrl.indexOf("://") + 3), proxyUrl.lastIndexOf(":"));
      int portProxy = Integer.parseInt(proxyUrl.substring(proxyUrl.lastIndexOf(":") + 1, proxyUrl.length()));
      Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostProxy, portProxy));
      URLConnection conn = null;
      try {
        conn = new URL(serverUrl).openConnection(proxy);
        conn.connect();
        isReachable = getStatusOK();
      } catch (IOException e) {
        System.out.println("Unable to check serverUrl With Proxy:" + serverUrl);
        System.out.println("Error:" + e.getMessage());
      } finally {
        HttpURLConnection conn1 = (HttpURLConnection) conn;
        conn1.disconnect();
      }
    } else {
      String host = serverUrl.substring(serverUrl.indexOf("://") + 3, serverUrl.lastIndexOf(":"));
      int port = Integer.parseInt(serverUrl.substring(serverUrl.lastIndexOf(":") + 1, serverUrl.length()));

      try (Socket sock = new Socket()) {
        SocketAddress sockAddress = new InetSocketAddress(host, port);

        int timeoutMs = 2000; // 2 seconds
        sock.connect(sockAddress, timeoutMs);
        isReachable = getStatusOK();
      } catch (Exception e) {
        System.out.println("Unable to check serverUrl:" + serverUrl);
        System.out.println("Error:" + e.getMessage());
      }

    }
    return isReachable;
  }

  @Scheduled(fixedRate = 3000)
  @Async
  public Supplier<Number> getValidBackend() {
    return isBackendAccessable("http://www.google.de:443",null);
  }
  @Scheduled(fixedRate = 3000)
  @Async
  public Supplier<Number> getInvalidBackend() {
    return isBackendAccessable("http://www.google.de:440",null);
  }

  public Supplier<Number> getStatusNOK() {
    return new Supplier<Number>() {
      @Override
      public Number get() {
        return 1;
      }
    };
  }

    public Supplier<Number> getStatusOK() {
      return new Supplier<Number>() {
        @Override
        public Number get() {
          return 0;
        }
      };
    }

  public MyMetricsCustomBean(MeterRegistry registry) {
    // simple, non-dimensional prometheus metric
    Gauge.builder("urlStatusGoogle", getValidBackend()).register(registry);
    Gauge.builder("urlStatusGoogleDown", getInvalidBackend()).register(registry);
    Gauge.builder("urlStatusGoogleOverProxy", getValidBackendOverProxy()).register(registry);
    Gauge.builder("urlStatusGoogleDownOverProxy", getInvalidBackendProxy()).register(registry);
  }

  @Scheduled(fixedRate = 3000)
  @Async
  public Supplier<Number> getValidBackendOverProxy() {
    return isBackendAccessable("https://www.google.de:443","http://192.168.178.49:3128");
  }
  @Scheduled(fixedRate = 3000)
  @Async
  public Supplier<Number> getInvalidBackendProxy() {
    return isBackendAccessable("https://www.google.de:440","http://192.168.178.49:3128");
  }
}