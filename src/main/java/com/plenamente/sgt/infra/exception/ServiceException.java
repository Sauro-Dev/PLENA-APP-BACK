package com.plenamente.sgt.infra.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
  private final String code;

  public ServiceException(String message) {
    super(message);
    this.code = "INTERNAL_ERROR";
  }

  public ServiceException(String message, Throwable cause) {
    super(message, cause);
    this.code = "INTERNAL_ERROR";
  }

  public ServiceException(String code, String message) {
    super(message);
    this.code = code;
  }
}