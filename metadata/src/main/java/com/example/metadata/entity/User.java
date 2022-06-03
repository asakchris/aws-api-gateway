package com.example.metadata.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

@Entity(name = "m_user")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "user_name")
  private String userName;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "modified_at")
  private LocalDateTime modifiedAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    User user = (User) o;
    return userId != null && Objects.equals(userId, user.userId);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
