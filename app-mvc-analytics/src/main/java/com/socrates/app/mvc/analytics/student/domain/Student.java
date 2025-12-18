package com.socrates.app.mvc.analytics.student.domain;

import com.socrates.app.mvc.analytics.instructor.domain.Instructor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Student {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public void updateLastActivityAt() {
        this.lastActivityAt = LocalDateTime.now();
    }
}
