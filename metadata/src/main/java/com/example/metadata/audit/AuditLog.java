package com.example.metadata.audit;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "t_audit_log")
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AuditLog implements Serializable {

  @Serial
  private static final long serialVersionUID = -1150547241544334838L;

  @Id
  @Column(name = "request_id")
  private String requestId;

  @Column(name = "start_time")
  private LocalDateTime startTime;

  @Column(name = "method")
  private String method;

  @Column(name = "uri")
  private String uri;

  @Column(name = "username")
  private String username;

  @Column(name = "request_body")
  private String requestBody;

  @Column(name = "status")
  private Integer status;

  @Column(name = "response_body")
  private String responseBody;

  @Column(name = "time_taken_in_millis")
  private Long timeTakenInMillis;

  @Column(name = "created_date")
  private LocalDateTime createdDate;
}
