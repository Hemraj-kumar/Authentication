package dev.hemraj.kafka_001.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ApiResponse {
    private int code;
    private String message="";
    private List<ErrorBO> errorBOList=new ArrayList<>();
    private boolean success;
    private Object data = new JSONObject();
}
