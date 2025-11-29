package app.domain;

import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.cassandra.core.mapping.Column;

/**
 * Lớp trừu tượng cơ sở cho các thực thể, chứa các định nghĩa cho thuộc tính tạo, sửa đổi lần cuối,
 * người tạo, người sửa đổi lần cuối.
 */
public abstract class AbstractAuditingEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column("created_by")
    private String createdBy; // Người tạo

    @CreatedDate
    @Column("created_date")
    private Instant createdDate = Instant.now(); // Ngày tạo

    @LastModifiedBy
    @Column("last_modified_by")
    private String lastModifiedBy; // Người sửa đổi lần cuối

    @LastModifiedDate
    @Column("last_modified_date")
    private Instant lastModifiedDate = Instant.now(); // Ngày sửa đổi lần cuối

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
