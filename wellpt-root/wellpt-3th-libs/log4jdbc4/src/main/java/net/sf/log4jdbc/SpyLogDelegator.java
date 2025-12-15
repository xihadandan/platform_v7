package net.sf.log4jdbc;

public abstract interface SpyLogDelegator {
	public abstract boolean isJdbcLoggingEnabled();

	public abstract void exceptionOccured(Spy paramSpy, String paramString1, Exception paramException,
			String paramString2, long paramLong);

	public abstract void methodReturned(Spy paramSpy, String paramString1, String paramString2);

	public abstract void constructorReturned(Spy paramSpy, String paramString);

	public abstract void sqlOccured(Spy paramSpy, String paramString1, String paramString2);

	public abstract void sqlTimingOccured(Spy paramSpy, long paramLong, String paramString1, String paramString2);

	public abstract void connectionOpened(Spy paramSpy);

	public abstract void connectionClosed(Spy paramSpy);

	public abstract void debug(String paramString);
}
