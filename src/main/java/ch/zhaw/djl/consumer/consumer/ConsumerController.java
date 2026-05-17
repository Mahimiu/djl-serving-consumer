package ch.zhaw.djl.consumer.consumer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@Tag(name = "Image Classification Consumer API", description = "Endpoints for image classification via DJL Serving sidecar")
public class ConsumerController {

    private static final String MODEL_NAME = "traced_resnet18";
    private static final String MODEL_SERVICE_URI = "http://localhost:8080/predictions/" + MODEL_NAME;

    @GetMapping("/ping")
    @Operation(summary = "Health ping", description = "Simple ping to check if the consumer app is running")
    public String ping() {
        return "DJL Consumer app is up and running!";
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns the application health status with timestamp")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("modelService", MODEL_SERVICE_URI);
        return response;
    }

    @GetMapping("/info")
    @Operation(summary = "Service info", description = "Returns information about the consumer and the connected model service")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("modelName", MODEL_NAME);
        response.put("framework", "DJL Serving with TorchScript");
        response.put("architecture", "ResNet18 (ImageNet-1k)");
        response.put("modelService", MODEL_SERVICE_URI);
        response.put("numClasses", 1000);
        response.put("pattern", "Sidecar Architecture");
        return response;
    }

    @PostMapping(path = "/analyze", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Classify image", description = "Upload an image and get Top-5 classification probabilities from the model sidecar")
    public ResponseEntity<String> predict(@RequestParam("image") MultipartFile image) throws Exception {
        InputStream is = new ByteArrayInputStream(image.getBytes());

        var webClient = WebClient.create();
        Resource resource = new InputStreamResource(is);
        var result = webClient.post()
                .uri(MODEL_SERVICE_URI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromResource(resource))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }
}
