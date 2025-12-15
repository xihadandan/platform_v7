package net.sf.log4jdbc;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PostLogProfilerProcessor {
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("usage: java PostLogProfilerProcessor <log-file>");
			System.exit(1);
		}
		new PostLogProfilerProcessor(args[0], System.out);
	}

	private long totalSql = 0L;
	private long lineNo = 0L;
	private long totalMsec = 0L;
	private long maxMsec = 0L;
	private long flaggedSqlTotalMsec = 0L;
	private long threshold = 100L;
	private long topOffenderCount = 1000L;
	private List flaggedSql = new LinkedList();

	public PostLogProfilerProcessor(String filename, PrintStream out) throws Exception {
		FileReader f = new FileReader(filename);
		LineNumberReader l = new LineNumberReader(f);

		StringBuffer sql = new StringBuffer();
		String line;
		do {
			line = l.readLine();
			if (line != null) {
				boolean blankLine = line.length() == 0;
				this.lineNo += 1L;
				if (blankLine) {
					processSql(sql);
					sql = new StringBuffer();
				} else {
					sql.append(line);
				}
			}
		} while (line != null);
		out.println("processed " + this.lineNo + " lines.");

		f.close();

		out.println("Number of sql statements:  " + this.totalSql);
		out.println("Total number of msec    :  " + this.totalMsec);
		if (this.totalMsec > 0L) {
			out.println("Average msec/statement  :  " + this.totalSql / this.totalMsec);
		}
		int flaggedSqlStmts = this.flaggedSql.size();
		if (flaggedSqlStmts > 0) {
			out.println("Sql statements that took more than " + this.threshold + " msec were flagged.");
			out.println("Flagged sql statements              :  " + flaggedSqlStmts);
			out.println("Flagged sql Total number of msec    :  " + this.flaggedSqlTotalMsec);
			out.println("Flagged sql Average msec/statement  :  " + this.flaggedSqlTotalMsec / flaggedSqlStmts);

			out.println("sorting...");

			Object[] flaggedSqlArray = this.flaggedSql.toArray();
			Arrays.sort(flaggedSqlArray);

			int execTimeSize = ("" + this.maxMsec).length();
			if (this.topOffenderCount > flaggedSqlArray.length) {
				this.topOffenderCount = flaggedSqlArray.length;
			}
			out.println("top " + this.topOffenderCount + " offender" + (this.topOffenderCount == 1L ? "" : "s") + ":");
			for (int i = 0; i < this.topOffenderCount; i++) {
				ProfiledSql p = (ProfiledSql) flaggedSqlArray[i];
				out.println(Utilities.rightJustify(execTimeSize, new StringBuilder().append("").append(p.getExecTime())
						.toString())
						+ " " + p.getSql());
			}
		}
	}

	private void processSql(StringBuffer sql) {
		if (sql.length() > 0) {
			this.totalSql += 1L;
			String sqlStr = sql.toString();
			if (sqlStr.endsWith("msec}")) {
				int executedIn = sqlStr.indexOf("{executed in ");
				if (executedIn == -1) {
					System.err.println("WARNING:  sql w/o timing info found at line " + this.lineNo);
					return;
				}
				String msecStr = sqlStr.substring(executedIn + 13, sqlStr.length() - 6);
				long msec = Long.parseLong(msecStr);
				this.totalMsec += msec;
				if (msec > this.maxMsec) {
					this.maxMsec = msec;
				}
				if (msec > this.threshold) {
					flagSql(msec, sqlStr);
					this.flaggedSqlTotalMsec += msec;
				}
			} else {
				System.err.println("WARNING:  sql w/o timing info found at line " + this.lineNo);
			}
		}
	}

	private void flagSql(long msec, String sql) {
		this.flaggedSql.add(new ProfiledSql(msec, sql));
	}

	private class ProfiledSql implements Comparable {
		private Long execTime;
		private String sql;

		public ProfiledSql(long msec, String sql) {
			this.execTime = new Long(msec);
			this.sql = sql;
		}

		public int compareTo(Object o) {
			return ((ProfiledSql) o).execTime.compareTo(this.execTime);
		}

		public Long getExecTime() {
			return this.execTime;
		}

		public String getSql() {
			return this.sql;
		}

		public String toString() {
			return this.execTime + " msec:  " + this.sql;
		}
	}
}
