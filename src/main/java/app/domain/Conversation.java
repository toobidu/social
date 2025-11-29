package app.domain;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Metadata cho một cuộc trò chuyện (1-1 hoặc Nhóm).
 */
@Table("conversations")
public class Conversation extends AbstractAuditingEntity<Conversation> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKey("conversation_id")
    private UUID conversationId; // ID cuộc trò chuyện

    private String name; // Tên cuộc trò chuyện (nếu là nhóm)

    @Column("is_group")
    private Boolean isGroup; // Là nhóm hay không

    @Column("participant_ids")
    private Set<UUID> participantIds; // Danh sách ID người tham gia

    public UUID getConversationId() {
        return conversationId;
    }

    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Boolean group) {
        isGroup = group;
    }

    public Set<UUID> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(Set<UUID> participantIds) {
        this.participantIds = participantIds;
    }
}
