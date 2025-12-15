package net.sf.log4jdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jSpyLogDelegator implements SpyLogDelegator {
	private final Logger jdbcLogger = LoggerFactory.getLogger("jdbc.audit");
	private final Logger resultSetLogger = LoggerFactory.getLogger("jdbc.resultset");
	private final Logger sqlOnlyLogger = LoggerFactory.getLogger("jdbc.sqlonly");
	private final Logger sqlTimingLogger = LoggerFactory.getLogger("jdbc.sqltiming");
	private final Logger connectionLogger = LoggerFactory.getLogger("jdbc.connection");
	private final Logger debugLogger = LoggerFactory.getLogger("log4jdbc.debug");

	public boolean isJdbcLoggingEnabled() {
		return (this.jdbcLogger.isErrorEnabled()) || (this.resultSetLogger.isErrorEnabled())
				|| (this.sqlOnlyLogger.isErrorEnabled()) || (this.sqlTimingLogger.isErrorEnabled())
				|| (this.connectionLogger.isErrorEnabled());
	}

	public void exceptionOccured(Spy spy, String methodCall, Exception e, String sql, long execTime) {
		String classType = spy.getClassType();
		Integer spyNo = spy.getConnectionNumber();
		String header = spyNo + ". " + classType + "." + methodCall;
		if (sql == null) {
			this.jdbcLogger.error(header, e);
			this.sqlOnlyLogger.error(header, e);
			this.sqlTimingLogger.error(header, e);
		} else {
			this.jdbcLogger.error(header + " " + sql, e);
			if (this.sqlOnlyLogger.isDebugEnabled()) {
				this.sqlOnlyLogger.error(getDebugInfo() + nl + spyNo + ". " + sql, e);
			} else {
				this.sqlOnlyLogger.error(header + " " + sql, e);
			}
			if (this.sqlTimingLogger.isDebugEnabled()) {
				this.sqlTimingLogger.error(getDebugInfo() + nl + spyNo + ". " + sql + " {FAILED after " + execTime
						+ " msec}", e);
			} else {
				this.sqlTimingLogger.error(header + " FAILED! " + sql + " {FAILED after " + execTime + " msec}", e);
			}
		}
	}

	public void methodReturned(Spy spy, String methodCall, String returnMsg) {
		String classType = spy.getClassType();
		Integer spyNo = spy.getConnectionNumber();
		String header = spyNo + ". " + classType + "." + methodCall + " returned " + returnMsg;
		if ("ResultSet".equals(classType)) {
			if (this.resultSetLogger.isDebugEnabled()) {
				this.resultSetLogger.debug(header + " " + getDebugInfo());
			} else if (this.resultSetLogger.isInfoEnabled()) {
				this.resultSetLogger.info(header);
			}
		} else if (this.jdbcLogger.isDebugEnabled()) {
			this.jdbcLogger.debug(header + " " + getDebugInfo());
		} else if (this.jdbcLogger.isInfoEnabled()) {
			this.jdbcLogger.info(header);
		}
	}

	private static String nl = System.getProperty("line.separator");

	public void constructorReturned(Spy spy, String constructionInfo) {
	}

	private boolean shouldSqlBeLogged(String sql) {
		if (sql == null) {
			return false;
		}
		sql = sql.trim();
		if (sql.length() < 6) {
			return false;
		}
		sql = sql.substring(0, 6).toLowerCase();

		return ((DriverSpy.DumpSqlSelect) && ("select".equals(sql)))
				|| ((DriverSpy.DumpSqlInsert) && ("insert".equals(sql)))
				|| ((DriverSpy.DumpSqlUpdate) && ("update".equals(sql)))
				|| ((DriverSpy.DumpSqlDelete) && ("delete".equals(sql)))
				|| ((DriverSpy.DumpSqlCreate) && ("create".equals(sql)));
	}

	public void sqlOccured(Spy spy, String methodCall, String sql) {
		if ((!DriverSpy.DumpSqlFilteringOn) || (shouldSqlBeLogged(sql))) {
			sql = processSql(sql);
			if (this.sqlOnlyLogger.isDebugEnabled()) {
				this.sqlOnlyLogger.debug(getDebugInfo() + nl + spy.getConnectionNumber() + ". " + sql);
			} else if (this.sqlOnlyLogger.isInfoEnabled()) {
				this.sqlOnlyLogger.info(sql);
			}
		}
	}

	private String processSql(String sql) {
		if (sql == null) {
			return null;
		}
		sql = sql.trim();

		StringBuffer output = new StringBuffer();
		if (DriverSpy.DumpSqlMaxLineLength <= 0) {
			output.append(sql);
		} else {
			StringTokenizer st = new StringTokenizer(sql);

			int linelength = 0;
			while (st.hasMoreElements()) {
				String token = (String) st.nextElement();

				output.append(token);
				linelength += token.length();
				output.append(" ");
				linelength++;
				if (linelength > 90) {
					output.append("\n");
					linelength = 0;
				}
			}
		}
		if (DriverSpy.DumpSqlAddSemicolon) {
			output.append(";");
		}
		return output.toString();
	}

	public void sqlTimingOccured(Spy spy, long execTime, String methodCall, String sql) {
		if ((this.sqlTimingLogger.isErrorEnabled()) && ((!DriverSpy.DumpSqlFilteringOn) || (shouldSqlBeLogged(sql)))) {
			if ((DriverSpy.SqlTimingErrorThresholdEnabled) && (execTime >= DriverSpy.SqlTimingErrorThresholdMsec)) {
				this.sqlTimingLogger.error(buildSqlTimingDump(spy, execTime, methodCall, sql, true));
			} else if (this.sqlTimingLogger.isWarnEnabled()) {
				if ((DriverSpy.SqlTimingWarnThresholdEnabled) && (execTime >= DriverSpy.SqlTimingWarnThresholdMsec)) {
					this.sqlTimingLogger.warn(buildSqlTimingDump(spy, execTime, methodCall, sql, true));
				} else if (this.sqlTimingLogger.isDebugEnabled()) {
					this.sqlTimingLogger.debug(buildSqlTimingDump(spy, execTime, methodCall, sql, true));
				} else if (this.sqlTimingLogger.isInfoEnabled()) {
					this.sqlTimingLogger.info(buildSqlTimingDump(spy, execTime, methodCall, sql, false));
				}
			}
		}
	}

	private String buildSqlTimingDump(Spy spy, long execTime, String methodCall, String sql, boolean debugInfo) {
		StringBuffer out = new StringBuffer();
		if (debugInfo) {
			out.append(getDebugInfo());
			out.append(nl);
			out.append(spy.getConnectionNumber());
			out.append(". ");
		}
		
		Class<?> TenantContextHolderClazz;
		String tenantId = "";
		try {
			TenantContextHolderClazz = Class.forName("com.wellsoft.pt.security.util.TenantContextHolder");
			Method method = TenantContextHolderClazz.getMethod("getTenantId");  
			tenantId =  (String) method.invoke(null);
		} catch (ClassNotFoundException e) {
			out.append("net.sf.log4jdbc.Slf4jSpyLogDelegator in log4jdbc4-xx.xx.jar cann't find class: com.wellsoft.pt.security.util.TenantContextHolder");
			return out.toString();
		} catch (SecurityException e) {
			out.append("net.sf.log4jdbc.Slf4jSpyLogDelegator in log4jdbc4-xx.xx.jar cann't find class: com.wellsoft.pt.security.util.TenantContextHolder");
			return out.toString();
		} catch (NoSuchMethodException e) {
			out.append("net.sf.log4jdbc.Slf4jSpyLogDelegator in log4jdbc4-xx.xx.jar cann't find method: com.wellsoft.pt.security.util.TenantContextHolder.getTenantId()");
			return out.toString();
		} catch (IllegalArgumentException e) {
			out.append("net.sf.log4jdbc.Slf4jSpyLogDelegator in log4jdbc4-xx.xx.jar cann't find method: com.wellsoft.pt.security.util.TenantContextHolder.getTenantId()");
			return out.toString();
		} catch (IllegalAccessException e) {
			out.append("net.sf.log4jdbc.Slf4jSpyLogDelegator in log4jdbc4-xx.xx.jar cann't find method: com.wellsoft.pt.security.util.TenantContextHolder.getTenantId()");
			return out.toString();
		} catch (InvocationTargetException e) {
			out.append("net.sf.log4jdbc.Slf4jSpyLogDelegator in log4jdbc4-xx.xx.jar cann't find method: com.wellsoft.pt.security.util.TenantContextHolder.getTenantId()");
			return out.toString();
		}  
	
		 
		sql = processSql(sql);

		out.append(sql);
		out.append(" {executed in ");
		out.append(execTime);
		
		out.append(" msec within tenant " + tenantId + "}");

		return out.toString();
	}

	private static String getDebugInfo() {
		Throwable t = new Throwable();
		t.fillInStackTrace();

		StackTraceElement[] stackTrace = t.getStackTrace();
		if (stackTrace != null) {
			StringBuffer dump = new StringBuffer();
			if (DriverSpy.DumpFullDebugStackTrace) {
				boolean first = true;
				for (int i = 0; i < stackTrace.length; i++) {
					String className = stackTrace[i].getClassName();
					if (!className.startsWith("net.sf.log4jdbc")) {
						if (first) {
							first = false;
						} else {
							dump.append("  ");
						}
						dump.append("at ");
						dump.append(stackTrace[i]);
						dump.append(nl);
					}
				}
			} else {
				dump.append(" ");
				int firstLog4jdbcCall = 0;
				int lastApplicationCall = 0;
				for (int i = 0; i < stackTrace.length; i++) {
					String className = stackTrace[i].getClassName();
					if (className.startsWith("net.sf.log4jdbc")) {
						firstLog4jdbcCall = i;
					} else if ((DriverSpy.TraceFromApplication) && (className.startsWith(DriverSpy.DebugStackPrefix))) {
						lastApplicationCall = i;
						break;
					}
				}
				int j = lastApplicationCall;
				if (j == 0) {
					j = 1 + firstLog4jdbcCall;
				}
				dump.append(stackTrace[j].getClassName()).append(".").append(stackTrace[j].getMethodName()).append("(")
						.append(stackTrace[j].getFileName()).append(":").append(stackTrace[j].getLineNumber())
						.append(")");
			}
			return dump.toString();
		}
		return null;
	}

	public void debug(String msg) {
		this.debugLogger.debug(msg);
	}

	public void connectionOpened(Spy spy) {
		if (this.connectionLogger.isDebugEnabled()) {
			this.connectionLogger.info(spy.getConnectionNumber() + ". Connection opened " + getDebugInfo());
			this.connectionLogger.debug(ConnectionSpy.getOpenConnectionsDump());
		} else {
			this.connectionLogger.info(spy.getConnectionNumber() + ". Connection opened");
		}
	}

	public void connectionClosed(Spy spy) {
		if (this.connectionLogger.isDebugEnabled()) {
			this.connectionLogger.info(spy.getConnectionNumber() + ". Connection closed " + getDebugInfo());
			this.connectionLogger.debug(ConnectionSpy.getOpenConnectionsDump());
		} else {
			this.connectionLogger.info(spy.getConnectionNumber() + ". Connection closed");
		}
	}
}
