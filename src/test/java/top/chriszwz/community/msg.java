package top.chriszwz.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class msg {
    private String ContentType;
    private String Url;
    private String Content;
}
