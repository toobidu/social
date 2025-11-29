package app.config;

import app.repository.UserRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import org.springframework.lang.NonNull;

/**
 * Cấu hình kết nối đến Elasticsearch (Reactive).
 * Sử dụng thư viện Spring Data Elasticsearch.
 */
@Configuration
@EnableReactiveElasticsearchRepositories(
    basePackages = "app.repository.search",
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = UserRepository.class)
)
public class ElasticsearchConfiguration extends ReactiveElasticsearchConfiguration {

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
            .connectedTo("localhost:9200")
            // .usingSsl() // Bật nếu dùng HTTPS
            .withBasicAuth("elastic", "changeme") // Bật nếu có mật khẩu
            .build();
    }
}
