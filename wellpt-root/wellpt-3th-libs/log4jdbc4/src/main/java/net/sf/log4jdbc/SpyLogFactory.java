package net.sf.log4jdbc;

public class SpyLogFactory {
	private static final SpyLogDelegator logger = new Slf4jSpyLogDelegator();

	public static SpyLogDelegator getSpyLogDelegator() {
		return logger;
	}
}
