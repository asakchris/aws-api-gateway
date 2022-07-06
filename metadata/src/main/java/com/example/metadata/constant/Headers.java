package com.example.metadata.constant;

public enum Headers {

  REQUEST_ID(Constants.REQUEST_ID),
  USER_NAME(Constants.USER_NAME_VALUE);

  private final String header;

  Headers(String header) {
    this.header = header;
  }

  public String getHeader() {
    return header;
  }

  public static class Constants {
    public static final String USER_NAME_VALUE = "App-User-Name";
    public static final String REQUEST_ID = "Request-Id";
  }
}
