package driverservice.errordecoder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ClientErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        String message = "Feign Client Error";

        try {
            if (response.body() != null) {
                String responseBody = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);

                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode.has("detail"))
                {
                    message = jsonNode.get("detail").asText();
                }
            }
        }
        catch (IOException e)
        {
            message = "Failed to read error response body";
        }

        return new RuntimeException("Feign error (" + status + "): " + message);
    }
}
