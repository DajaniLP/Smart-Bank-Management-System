package interfaces;

import java.time.LocalDateTime;

public interface Auditable {
    LocalDateTime getCreatedTimestamp();
    LocalDateTime getLastUpdatedTimestamp();
}