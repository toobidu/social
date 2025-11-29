package app.domain;

import java.io.Serializable;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

/**
 * Kiểu dữ liệu định nghĩa bởi người dùng (UDT) cho Địa chỉ.
 */
@Data
@UserDefinedType("address")
public class Address implements Serializable {

    private String country; // Quốc gia

    private String city; // Tỉnh / Thành phố

    private String district; // Quận / Huyện

    private String ward; // Phường / Xã

    private String detail; // Địa chỉ chi tiết (Số nhà, tên đường...)
}
