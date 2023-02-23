package hyundai.hyundai.Custom.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomReq {

    @ApiModelProperty(example = "[1,null,3,null,5,6]")
    private List<Integer> basicNumberList = new ArrayList<>();
    // private Set<Integer> basicNumberList;
}
