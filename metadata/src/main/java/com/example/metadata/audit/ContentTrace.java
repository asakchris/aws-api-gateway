package com.example.metadata.audit;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ContentTrace implements Serializable {

  @Serial private static final long serialVersionUID = 5136450385891989256L;

  private String requestId;
  private LocalDateTime startTime;
  private String method;
  private String uri;
  private String username;
  private String requestBody;
  private int status;
  private String responseBody;
  private long timeTakenInMillis;
}
