package com.plenamente.sgt.infra.exception;

public class FileStorageException extends ServiceException {
  public FileStorageException(String message) {
    super("FILE_STORAGE_ERROR", message);
  }

  public FileStorageException(String message, Throwable cause) {
    super("FILE_STORAGE_ERROR", message);
  }
}