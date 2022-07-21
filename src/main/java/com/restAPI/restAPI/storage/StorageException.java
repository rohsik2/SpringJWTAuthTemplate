package com.restAPI.restAPI.storage;

public class StorageException extends RuntimeException {

	public StorageException(String message) {
		super(message);
	}

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}

    public static class StorageFileNotFoundException extends Exception {
        public StorageFileNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }
}
