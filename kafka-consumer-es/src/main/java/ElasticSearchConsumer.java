import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ElasticSearchConsumer {


    public static RestHighLevelClient createClient() {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ElasticConfig.username, ElasticConfig.password));

        RestClientBuilder builder = RestClient.builder(new HttpHost(ElasticConfig.hostname, 443, "https"))
                                                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                                                    @Override
                                                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                                                        return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                                                    }
                                                });

        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;

    }

    public static void main(String[] args) throws IOException {
        Logger logger = LoggerFactory.getLogger(ElasticSearchConsumer.class.getName());


        RestHighLevelClient client = createClient();

        String jsonString = "{ \"foo\": \"bar\" }";

        IndexRequest request = new IndexRequest("twitter", "tweets").source(jsonString, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        String id = response.getId();

        logger.info(id);

        client.close();


    }

}
