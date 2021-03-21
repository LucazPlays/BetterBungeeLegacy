package net.md_5.bungee.util;

public class QuietException extends RuntimeException
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QuietException(final String message) {
        super(message);
    }
    
    @Override
    public Throwable initCause(final Throwable cause) {
        return this;
    }
    
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
