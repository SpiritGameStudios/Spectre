package dev.spiritstudios.spectre.api.core.exception;

public class ImpossibleException extends RuntimeException {
	public ImpossibleException() {
		super("This should be impossible. If you are seeing this, something has gone horribly wrong. Please submit a bug report to the creator of the mod this crash comes from.");
	}
	public ImpossibleException(String message) {
		super(message);
	}
}
