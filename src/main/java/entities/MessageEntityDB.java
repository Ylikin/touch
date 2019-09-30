package entities;

import lombok.Builder;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cities")
public class MessageEntityDB {
    @Column(name = "attractions")
    @Type(type = "text")
    String attractions;
    String clientQuestions;

    String agentAnswers;

    @Id
    String agentName;
}
