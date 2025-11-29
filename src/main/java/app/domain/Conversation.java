package app.domain;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Metadata for a conversation (1-on-1 or Group).
 */
@Table("conversations")
public class Conversation extends AbstractAuditingEntity<Conversation> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKey("conversation_id")
    private UUID conversationId;

    private String name;

    @Column("is_group")
    private Boolean isGroup;

    @Column("participant_ids")
    private Set<UUID> participantIds;

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
