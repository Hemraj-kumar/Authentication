package dev.hemraj.kafka_001.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private int code;
    private String message;
    private List<ErrorBO> errorBOList;
    private boolean success;
    private Object data = new JSONObject();
}
