package io.emeraldpay.polkaj.apihttp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.emeraldpay.polkaj.api.AbstractPolkadotApi;
import io.emeraldpay.polkaj.api.RpcCall;
import io.emeraldpay.polkaj.api.RpcException;
import io.emeraldpay.polkaj.json.jackson.PolkadotModule;
import okhttp3.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

/**
 * Default JSON RPC HTTP client for Polkadot API. It uses Java 11 HttpClient implementation for requests.
 * Each request made from that client has a uniq id, from a monotone sequence starting on 0. A new instance is
 * supposed to be create through {@link PolkadotHttpApi#newBuilder()}:
 * <br>
 * The class is AutoCloseable, with {@link PolkadotHttpApi#close()} methods, which shutdown a thread (or threads) used for http requests.
 *
 * <br>
 * Example:
 * <pre><code>
 * PolkadotHttpApi client = PolkadotHttpApi.newBuilder().build();
 * Future&lt;Hash256&gt; hash = client.execute(Hash256.class, "chain_getFinalisedHead");
 * System.out.println("Current head: " + hash.get());
 * </code></pre>
 */
public class PolkadotHttpApi extends AbstractPolkadotApi implements AutoCloseable {

    private static final String APPLICATION_JSON = "application/json";

    private final OkHttpClient httpClient;
    private final Request.Builder request;
    private boolean closed = false;

    private PolkadotHttpApi(String target, OkHttpClient httpClient, String basicAuth,ObjectMapper objectMapper) {
        super(objectMapper);
        this.httpClient = httpClient;

        Request.Builder request = new Request.Builder()
                .url(target)
//                .timeout(Duration.ofMinutes(1))
                .header("User-Agent", "Polkaj/0.3") //TODO generate version during compilation
                .header("Content-Type", APPLICATION_JSON);

        if (basicAuth != null) {
            request = request.header("Authorization", basicAuth);
        }

        this.request = request;
    }

    /**
     * Execute JSON RPC request
     *
     * @param call RPC call to execute
     * @param <T> type of the result
     * @return CompletableFuture for the result. Note that the Future may throw RpcException when it get
     * @see RpcException
     */
    @Override
    public <T> CompletableFuture<T> execute(RpcCall<T> call) {
        if (closed) {
            throw new IllegalStateException("Client is already closed");
        }
        int id = nextId();
        JavaType type = call.getResultType(objectMapper.getTypeFactory());
        try {
            Request.Builder request = this.request
                    .post(RequestBody.create(MediaType.parse(APPLICATION_JSON),encode(id, call.getMethod(), call.getParams())));

            return CompletableFuture.supplyAsync(()->{
                try {
                    Response response = httpClient.newCall(request.build()).execute();
                    return decode(id,verify(response).body().string(),type);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
//
        } catch (JsonProcessingException e) {
            throw new RpcException(-32600, "Unable to encode request as JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Verify the HTTP response meta, i.e. statuc code, headers, etc.
     *
     * @param response HTTP response from server
     * @return The response itself if all is ok
     * @throws CompletionException with RpcException details to let executor know that the response is invalid
     * @see CompletionException
     * @see RpcException
     */
    public Response verify(Response response) {
        if (response.code() != 200) {
            try {
                String body=response.body().string();
                throw new CompletionException(
                        new RpcException(-32000, "Server returned error status: " + response.code()+" with message "+body)
                );
            } catch (IOException e) {
                throw new CompletionException(
                        new RpcException(-32000, "Server returned error status: " + response.code())
                );
            }


        }
        //response shouldn't contain non-ascii so charset can be ignored
        String contentType=response.headers().get("content-type");
        if (Objects.nonNull(contentType) && !contentType.startsWith(APPLICATION_JSON)) {
            throw new CompletionException(
                    new RpcException(-32000, "Server returned invalid content-type: " + contentType)
            );
        }
        return response;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
    }

    /**
     * PolkadotRpcClient builder. All of the configurations are optional, and the default build would use
     * a standard Java HttpClient without any authorization connecting to localhost:9933 and using
     * a new instance of a Jackson ObjectMapper with PolkadotModule enabled.
     *
     * @see PolkadotHttpApi
     * @see OkHttpClient
     * @see PolkadotModule
     */
    public static class Builder {
        private String target;
        private String basicAuth;
        private OkHttpClient httpClient;
        private ObjectMapper objectMapper;
        private Duration timeout;


        /**
         * Setup Basic Auth for RPC calls
         *
         * @param username username
         * @param password password
         * @return builder
         */
        public Builder basicAuth(String username, String password) {
            this.basicAuth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
            return this;
        }

        /**
         * Server address URL
         *
         * @param target URL
         * @return builder
         */
        public Builder connectTo(String target) {
            this.httpClient = null;
            this.target = target;
            return this;
        }

        /**
         * Provide a custom HttpClient configured
         *
         * @param httpClient client
         * @return builder
         */
        public Builder httpClient(OkHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /**
         * Provide a custom ObjectMapper that will be used to encode/decode request and responses.
         *
         * @param objectMapper ObjectMapper
         * @return builder
         */
        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        /**
         * Override the default timeout with a custom duration.
         *
         * @param timeout Duration
         * @return builder
         */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        protected void initDefaults() {
            if (httpClient == null && target == null) {
                connectTo("http://127.0.0.1:9933");
            }
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
                objectMapper.registerModule(new PolkadotModule());
            }
            if (timeout == null) {
                timeout = Duration.ofMinutes(1);
            }
        }

        /**
         * Apply configuration and build client
         *
         * @return new instance of PolkadotRpcClient
         */
        public PolkadotHttpApi build() {
            initDefaults();

            if (this.httpClient == null) {
                httpClient = new OkHttpClient.Builder()
                        .callTimeout(Duration.ofSeconds(2))
                        .readTimeout(Duration.ofSeconds(2))
                        .connectTimeout(Duration.ofSeconds(2))
                        .connectionPool(new ConnectionPool(10,5, TimeUnit.MINUTES))
                        .build();
            }

            return new PolkadotHttpApi(target, httpClient, basicAuth, objectMapper);
        }

    }
}
