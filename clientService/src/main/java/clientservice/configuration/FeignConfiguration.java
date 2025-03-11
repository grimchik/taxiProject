package clientservice.configuration;

import clientservice.errordecoder.ClientErrorDecoder;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.codec.ErrorDecoder;

@Configuration
public class FeignConfiguration {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new ClientErrorDecoder();
    }

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }
}
