package net.sf.log4jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public class ResultSetSpy implements ResultSet, Spy {
	private final SpyLogDelegator log;
	private ResultSet realResultSet;
	private StatementSpy parent;
	public static final String classTypeDescription = "ResultSet";

	protected void reportException(String methodCall, SQLException exception) {
		this.log.exceptionOccured(this, methodCall, exception, null, -1L);
	}

	protected void reportAllReturns(String methodCall, String msg) {
		this.log.methodReturned(this, methodCall, msg);
	}

	public ResultSet getRealResultSet() {
		return this.realResultSet;
	}

	public ResultSetSpy(StatementSpy parent, ResultSet realResultSet) {
		if (realResultSet == null) {
			throw new IllegalArgumentException("Must provide a non null real ResultSet");
		}
		this.realResultSet = realResultSet;
		this.parent = parent;
		this.log = SpyLogFactory.getSpyLogDelegator();
		reportReturn("new ResultSet");
	}

	public String getClassType() {
		return "ResultSet";
	}

	public Integer getConnectionNumber() {
		return this.parent.getConnectionNumber();
	}

	protected boolean reportReturn(String methodCall, boolean value) {
		reportAllReturns(methodCall, "" + value);
		return value;
	}

	protected byte reportReturn(String methodCall, byte value) {
		reportAllReturns(methodCall, "" + value);
		return value;
	}

	protected int reportReturn(String methodCall, int value) {
		reportAllReturns(methodCall, "" + value);
		return value;
	}

	protected double reportReturn(String methodCall, double value) {
		reportAllReturns(methodCall, "" + value);
		return value;
	}

	protected short reportReturn(String methodCall, short value) {
		reportAllReturns(methodCall, "" + value);
		return value;
	}

	protected long reportReturn(String methodCall, long value) {
		reportAllReturns(methodCall, "" + value);
		return value;
	}

	protected float reportReturn(String methodCall, float value) {
		reportAllReturns(methodCall, "" + value);
		return value;
	}

	protected Object reportReturn(String methodCall, Object value) {
		reportAllReturns(methodCall, "" + value);
		return value;
	}

	protected void reportReturn(String methodCall) {
		reportAllReturns(methodCall, "");
	}

	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		String methodCall = "updateAsciiStream(" + columnIndex + ", " + x + ", " + length + ")";
		try {
			this.realResultSet.updateAsciiStream(columnIndex, x, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
		String methodCall = "updateAsciiStream(" + columnName + ", " + x + ", " + length + ")";
		try {
			this.realResultSet.updateAsciiStream(columnName, x, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public int getRow() throws SQLException {
		String methodCall = "getRow()";
		try {
			return reportReturn(methodCall, this.realResultSet.getRow());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void cancelRowUpdates() throws SQLException {
		String methodCall = "cancelRowUpdates()";
		try {
			this.realResultSet.cancelRowUpdates();
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public Time getTime(int columnIndex) throws SQLException {
		String methodCall = "getTime(" + columnIndex + ")";
		try {
			return (Time) reportReturn(methodCall, this.realResultSet.getTime(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Time getTime(String columnName) throws SQLException {
		String methodCall = "getTime(" + columnName + ")";
		try {
			return (Time) reportReturn(methodCall, this.realResultSet.getTime(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		String methodCall = "getTime(" + columnIndex + ", " + cal + ")";
		try {
			return (Time) reportReturn(methodCall, this.realResultSet.getTime(columnIndex, cal));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Time getTime(String columnName, Calendar cal) throws SQLException {
		String methodCall = "getTime(" + columnName + ", " + cal + ")";
		try {
			return (Time) reportReturn(methodCall, this.realResultSet.getTime(columnName, cal));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean absolute(int row) throws SQLException {
		String methodCall = "absolute(" + row + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.absolute(row));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		String methodCall = "getTimestamp(" + columnIndex + ")";
		try {
			return (Timestamp) reportReturn(methodCall, this.realResultSet.getTimestamp(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Timestamp getTimestamp(String columnName) throws SQLException {
		String methodCall = "getTimestamp(" + columnName + ")";
		try {
			return (Timestamp) reportReturn(methodCall, this.realResultSet.getTimestamp(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		String methodCall = "getTimestamp(" + columnIndex + ", " + cal + ")";
		try {
			return (Timestamp) reportReturn(methodCall, this.realResultSet.getTimestamp(columnIndex, cal));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
		String methodCall = "getTimestamp(" + columnName + ", " + cal + ")";
		try {
			return (Timestamp) reportReturn(methodCall, this.realResultSet.getTimestamp(columnName, cal));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void moveToInsertRow() throws SQLException {
		String methodCall = "moveToInsertRow()";
		try {
			this.realResultSet.moveToInsertRow();
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public boolean relative(int rows) throws SQLException {
		String methodCall = "relative(" + rows + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.relative(rows));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean previous() throws SQLException {
		String methodCall = "previous()";
		try {
			return reportReturn(methodCall, this.realResultSet.previous());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void moveToCurrentRow() throws SQLException {
		String methodCall = "moveToCurrentRow()";
		try {
			this.realResultSet.moveToCurrentRow();
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public Ref getRef(int i) throws SQLException {
		String methodCall = "getRef(" + i + ")";
		try {
			return (Ref) reportReturn(methodCall, this.realResultSet.getRef(i));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateRef(int columnIndex, Ref x) throws SQLException {
		String methodCall = "updateRef(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateRef(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public Ref getRef(String colName) throws SQLException {
		String methodCall = "getRef(" + colName + ")";
		try {
			return (Ref) reportReturn(methodCall, this.realResultSet.getRef(colName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateRef(String columnName, Ref x) throws SQLException {
		String methodCall = "updateRef(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateRef(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public Blob getBlob(int i) throws SQLException {
		String methodCall = "getBlob(" + i + ")";
		try {
			return (Blob) reportReturn(methodCall, this.realResultSet.getBlob(i));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		String methodCall = "updateBlob(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateBlob(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public Blob getBlob(String colName) throws SQLException {
		String methodCall = "getBlob(" + colName + ")";
		try {
			return (Blob) reportReturn(methodCall, this.realResultSet.getBlob(colName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateBlob(String columnName, Blob x) throws SQLException {
		String methodCall = "updateBlob(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateBlob(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public Clob getClob(int i) throws SQLException {
		String methodCall = "getClob(" + i + ")";
		try {
			return (Clob) reportReturn(methodCall, this.realResultSet.getClob(i));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateClob(int columnIndex, Clob x) throws SQLException {
		String methodCall = "updateClob(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateClob(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public Clob getClob(String colName) throws SQLException {
		String methodCall = "getClob(" + colName + ")";
		try {
			return (Clob) reportReturn(methodCall, this.realResultSet.getClob(colName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateClob(String columnName, Clob x) throws SQLException {
		String methodCall = "updateClob(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateClob(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public boolean getBoolean(int columnIndex) throws SQLException {
		String methodCall = "getBoolean(" + columnIndex + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getBoolean(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean getBoolean(String columnName) throws SQLException {
		String methodCall = "getBoolean(" + columnName + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getBoolean(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Array getArray(int i) throws SQLException {
		String methodCall = "getArray(" + i + ")";
		try {
			return (Array) reportReturn(methodCall, this.realResultSet.getArray(i));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateArray(int columnIndex, Array x) throws SQLException {
		String methodCall = "updateArray(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateArray(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public Array getArray(String colName) throws SQLException {
		String methodCall = "getArray(" + colName + ")";
		try {
			return (Array) reportReturn(methodCall, this.realResultSet.getArray(colName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateArray(String columnName, Array x) throws SQLException {
		String methodCall = "updateArray(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateArray(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public RowId getRowId(int columnIndex) throws SQLException {
		String methodCall = "getRowId(" + columnIndex + ")";
		try {
			return (RowId) reportReturn(methodCall, this.realResultSet.getRowId(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public RowId getRowId(String columnLabel) throws SQLException {
		String methodCall = "getRowId(" + columnLabel + ")";
		try {
			return (RowId) reportReturn(methodCall, this.realResultSet.getRowId(columnLabel));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		String methodCall = "updateRowId(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateRowId(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		String methodCall = "updateRowId(" + columnLabel + ", " + x + ")";
		try {
			this.realResultSet.updateRowId(columnLabel, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public int getHoldability() throws SQLException {
		String methodCall = "getHoldability()";
		try {
			return reportReturn(methodCall, this.realResultSet.getHoldability());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean isClosed() throws SQLException {
		String methodCall = "isClosed()";
		try {
			return reportReturn(methodCall, this.realResultSet.isClosed());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateNString(int columnIndex, String nString) throws SQLException {
		String methodCall = "updateNString(" + columnIndex + ", " + nString + ")";
		try {
			this.realResultSet.updateNString(columnIndex, nString);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateNString(String columnLabel, String nString) throws SQLException {
		String methodCall = "updateNString(" + columnLabel + ", " + nString + ")";
		try {
			this.realResultSet.updateNString(columnLabel, nString);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		String methodCall = "updateNClob(" + columnIndex + ", " + nClob + ")";
		try {
			this.realResultSet.updateNClob(columnIndex, nClob);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		String methodCall = "updateNClob(" + columnLabel + ", " + nClob + ")";
		try {
			this.realResultSet.updateNClob(columnLabel, nClob);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public NClob getNClob(int columnIndex) throws SQLException {
		String methodCall = "getNClob(" + columnIndex + ")";
		try {
			return (NClob) reportReturn(methodCall, this.realResultSet.getNClob(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public NClob getNClob(String columnLabel) throws SQLException {
		String methodCall = "getNClob(" + columnLabel + ")";
		try {
			return (NClob) reportReturn(methodCall, this.realResultSet.getNClob(columnLabel));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		String methodCall = "getSQLXML(" + columnIndex + ")";
		try {
			return (SQLXML) reportReturn(methodCall, this.realResultSet.getSQLXML(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		String methodCall = "getSQLXML(" + columnLabel + ")";
		try {
			return (SQLXML) reportReturn(methodCall, this.realResultSet.getSQLXML(columnLabel));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		String methodCall = "updateSQLXML(" + columnIndex + ", " + xmlObject + ")";
		try {
			this.realResultSet.updateSQLXML(columnIndex, xmlObject);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		String methodCall = "updateSQLXML(" + columnLabel + ", " + xmlObject + ")";
		try {
			this.realResultSet.updateSQLXML(columnLabel, xmlObject);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public String getNString(int columnIndex) throws SQLException {
		String methodCall = "getNString(" + columnIndex + ")";
		try {
			return (String) reportReturn(methodCall, this.realResultSet.getNString(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public String getNString(String columnLabel) throws SQLException {
		String methodCall = "getNString(" + columnLabel + ")";
		try {
			return (String) reportReturn(methodCall, this.realResultSet.getNString(columnLabel));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		String methodCall = "getNCharacterStream(" + columnIndex + ")";
		try {
			return (Reader) reportReturn(methodCall, this.realResultSet.getNCharacterStream(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		String methodCall = "getNCharacterStream(" + columnLabel + ")";
		try {
			return (Reader) reportReturn(methodCall, this.realResultSet.getNCharacterStream(columnLabel));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		String methodCall = "updateNCharacterStream(" + columnIndex + ", " + x + ", " + length + ")";
		try {
			this.realResultSet.updateNCharacterStream(columnIndex, x, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		String methodCall = "updateNCharacterStream(" + columnLabel + ", " + reader + ", " + length + ")";
		try {
			this.realResultSet.updateNCharacterStream(columnLabel, reader, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		String methodCall = "updateAsciiStream(" + columnIndex + ", " + x + ", " + length + ")";
		try {
			this.realResultSet.updateAsciiStream(columnIndex, x, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		String methodCall = "updateBinaryStream(" + columnIndex + ", " + x + ", " + length + ")";
		try {
			this.realResultSet.updateBinaryStream(columnIndex, x, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		String methodCall = "updateCharacterStream(" + columnIndex + ", " + x + ", " + length + ")";
		try {
			this.realResultSet.updateCharacterStream(columnIndex, x, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		String methodCall = "updateAsciiStream(" + columnLabel + ", " + x + ", " + length + ")";
		try {
			this.realResultSet.updateAsciiStream(columnLabel, x, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		String methodCall = "updateBinaryStream(" + columnLabel + ", " + x + ", " + length + ")";
		try {
			this.realResultSet.updateBinaryStream(columnLabel, x, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		String methodCall = "updateCharacterStream(" + columnLabel + ", " + reader + ", " + length + ")";
		try {
			this.realResultSet.updateCharacterStream(columnLabel, reader, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		String methodCall = "updateBlob(" + columnIndex + ", " + inputStream + ", " + length + ")";
		try {
			this.realResultSet.updateBlob(columnIndex, inputStream, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		String methodCall = "updateBlob(" + columnLabel + ", " + inputStream + ", " + length + ")";
		try {
			this.realResultSet.updateBlob(columnLabel, inputStream, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		String methodCall = "updateClob(" + columnIndex + ", " + reader + ", " + length + ")";
		try {
			this.realResultSet.updateClob(columnIndex, reader, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		String methodCall = "updateClob(" + columnLabel + ", " + reader + ", " + length + ")";
		try {
			this.realResultSet.updateClob(columnLabel, reader, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		String methodCall = "updateNClob(" + columnIndex + ", " + reader + ", " + length + ")";
		try {
			this.realResultSet.updateNClob(columnIndex, reader, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		String methodCall = "updateNClob(" + columnLabel + ", " + reader + ", " + length + ")";
		try {
			this.realResultSet.updateNClob(columnLabel, reader, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateNCharacterStream(int columnIndex, Reader reader) throws SQLException {
		String methodCall = "updateNCharacterStream(" + columnIndex + ", " + reader + ")";
		try {
			this.realResultSet.updateNCharacterStream(columnIndex, reader);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		String methodCall = "updateNCharacterStream(" + columnLabel + ", " + reader + ")";
		try {
			this.realResultSet.updateNCharacterStream(columnLabel, reader);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		String methodCall = "updateAsciiStream(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateAsciiStream(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		String methodCall = "updateBinaryStream(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateBinaryStream(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		String methodCall = "updateCharacterStream(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateCharacterStream(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		String methodCall = "updateAsciiStream(" + columnLabel + ", " + x + ")";
		try {
			this.realResultSet.updateAsciiStream(columnLabel, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		String methodCall = "updateBinaryStream(" + columnLabel + ", " + x + ")";
		try {
			this.realResultSet.updateBinaryStream(columnLabel, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		String methodCall = "updateCharacterStream(" + columnLabel + ", " + reader + ")";
		try {
			this.realResultSet.updateCharacterStream(columnLabel, reader);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		String methodCall = "updateBlob(" + columnIndex + ", " + inputStream + ")";
		try {
			this.realResultSet.updateBlob(columnIndex, inputStream);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		String methodCall = "updateBlob(" + columnLabel + ", " + inputStream + ")";
		try {
			this.realResultSet.updateBlob(columnLabel, inputStream);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		String methodCall = "updateClob(" + columnIndex + ", " + reader + ")";
		try {
			this.realResultSet.updateClob(columnIndex, reader);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		String methodCall = "updateClob(" + columnLabel + ", " + reader + ")";
		try {
			this.realResultSet.updateClob(columnLabel, reader);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		String methodCall = "updateNClob(" + columnIndex + ", " + reader + ")";
		try {
			this.realResultSet.updateNClob(columnIndex, reader);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		String methodCall = "updateNClob(" + columnLabel + ", " + reader + ")";
		try {
			this.realResultSet.updateNClob(columnLabel, reader);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public boolean isBeforeFirst() throws SQLException {
		String methodCall = "isBeforeFirst()";
		try {
			return reportReturn(methodCall, this.realResultSet.isBeforeFirst());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public short getShort(int columnIndex) throws SQLException {
		String methodCall = "getShort(" + columnIndex + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getShort(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public short getShort(String columnName) throws SQLException {
		String methodCall = "getShort(" + columnName + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getShort(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public int getInt(int columnIndex) throws SQLException {
		String methodCall = "getInt(" + columnIndex + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getInt(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public int getInt(String columnName) throws SQLException {
		String methodCall = "getInt(" + columnName + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getInt(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void close() throws SQLException {
		String methodCall = "close()";
		try {
			this.realResultSet.close();
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		String methodCall = "getMetaData()";
		try {
			return (ResultSetMetaData) reportReturn(methodCall, this.realResultSet.getMetaData());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public int getType() throws SQLException {
		String methodCall = "getType()";
		try {
			return reportReturn(methodCall, this.realResultSet.getType());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public double getDouble(int columnIndex) throws SQLException {
		String methodCall = "getDouble(" + columnIndex + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getDouble(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public double getDouble(String columnName) throws SQLException {
		String methodCall = "getDouble(" + columnName + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getDouble(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void deleteRow() throws SQLException {
		String methodCall = "deleteRow()";
		try {
			this.realResultSet.deleteRow();
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public int getConcurrency() throws SQLException {
		String methodCall = "getConcurrency()";
		try {
			return reportReturn(methodCall, this.realResultSet.getConcurrency());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean rowUpdated() throws SQLException {
		String methodCall = "rowUpdated()";
		try {
			return reportReturn(methodCall, this.realResultSet.rowUpdated());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Date getDate(int columnIndex) throws SQLException {
		String methodCall = "getDate(" + columnIndex + ")";
		try {
			return (Date) reportReturn(methodCall, this.realResultSet.getDate(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Date getDate(String columnName) throws SQLException {
		String methodCall = "getDate(" + columnName + ")";
		try {
			return (Date) reportReturn(methodCall, this.realResultSet.getDate(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		String methodCall = "getDate(" + columnIndex + ", " + cal + ")";
		try {
			return (Date) reportReturn(methodCall, this.realResultSet.getDate(columnIndex, cal));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Date getDate(String columnName, Calendar cal) throws SQLException {
		String methodCall = "getDate(" + columnName + ", " + cal + ")";
		try {
			return (Date) reportReturn(methodCall, this.realResultSet.getDate(columnName, cal));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean last() throws SQLException {
		String methodCall = "last()";
		try {
			return reportReturn(methodCall, this.realResultSet.last());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean rowInserted() throws SQLException {
		String methodCall = "rowInserted()";
		try {
			return reportReturn(methodCall, this.realResultSet.rowInserted());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean rowDeleted() throws SQLException {
		String methodCall = "rowDeleted()";
		try {
			return reportReturn(methodCall, this.realResultSet.rowDeleted());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateNull(int columnIndex) throws SQLException {
		String methodCall = "updateNull(" + columnIndex + ")";
		try {
			this.realResultSet.updateNull(columnIndex);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateNull(String columnName) throws SQLException {
		String methodCall = "updateNull(" + columnName + ")";
		try {
			this.realResultSet.updateNull(columnName);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateShort(int columnIndex, short x) throws SQLException {
		String methodCall = "updateShort(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateShort(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateShort(String columnName, short x) throws SQLException {
		String methodCall = "updateShort(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateShort(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		String methodCall = "updateBoolean(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateBoolean(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateBoolean(String columnName, boolean x) throws SQLException {
		String methodCall = "updateBoolean(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateBoolean(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateByte(int columnIndex, byte x) throws SQLException {
		String methodCall = "updateByte(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateByte(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateByte(String columnName, byte x) throws SQLException {
		String methodCall = "updateByte(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateByte(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateInt(int columnIndex, int x) throws SQLException {
		String methodCall = "updateInt(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateInt(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateInt(String columnName, int x) throws SQLException {
		String methodCall = "updateInt(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateInt(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public Object getObject(int columnIndex) throws SQLException {
		String methodCall = "getObject(" + columnIndex + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getObject(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Object getObject(String columnName) throws SQLException {
		String methodCall = "getObject(" + columnName + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getObject(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Object getObject(String colName, Map map) throws SQLException {
		String methodCall = "getObject(" + colName + ", " + map + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getObject(colName, map));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean next() throws SQLException {
		String methodCall = "next()";
		try {
			return reportReturn(methodCall, this.realResultSet.next());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateLong(int columnIndex, long x) throws SQLException {
		String methodCall = "updateLong(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateLong(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateLong(String columnName, long x) throws SQLException {
		String methodCall = "updateLong(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateLong(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateFloat(int columnIndex, float x) throws SQLException {
		String methodCall = "updateFloat(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateFloat(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateFloat(String columnName, float x) throws SQLException {
		String methodCall = "updateFloat(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateFloat(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateDouble(int columnIndex, double x) throws SQLException {
		String methodCall = "updateDouble(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateDouble(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateDouble(String columnName, double x) throws SQLException {
		String methodCall = "updateDouble(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateDouble(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public Statement getStatement() throws SQLException {
		String methodCall = "getStatement()";
		try {
			return (Statement) reportReturn(methodCall, this.realResultSet.getStatement());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		String methodCall = "getObject(" + columnIndex + ", " + map + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getObject(columnIndex, map));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateString(int columnIndex, String x) throws SQLException {
		String methodCall = "updateString(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateString(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateString(String columnName, String x) throws SQLException {
		String methodCall = "updateString(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateString(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		String methodCall = "getAsciiStream(" + columnIndex + ")";
		try {
			return (InputStream) reportReturn(methodCall, this.realResultSet.getAsciiStream(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public InputStream getAsciiStream(String columnName) throws SQLException {
		String methodCall = "getAsciiStream(" + columnName + ")";
		try {
			return (InputStream) reportReturn(methodCall, this.realResultSet.getAsciiStream(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		String methodCall = "updateBigDecimal(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateBigDecimal(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public URL getURL(int columnIndex) throws SQLException {
		String methodCall = "getURL(" + columnIndex + ")";
		try {
			return (URL) reportReturn(methodCall, this.realResultSet.getURL(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
		String methodCall = "updateBigDecimal(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateBigDecimal(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public URL getURL(String columnName) throws SQLException {
		String methodCall = "getURL(" + columnName + ")";
		try {
			return (URL) reportReturn(methodCall, this.realResultSet.getURL(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		String methodCall = "updateBytes(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateBytes(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateBytes(String columnName, byte[] x) throws SQLException {
		String methodCall = "updateBytes(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateBytes(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	/**
	 * @deprecated
	 */
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		String methodCall = "getUnicodeStream(" + columnIndex + ")";
		try {
			return (InputStream) reportReturn(methodCall, this.realResultSet.getUnicodeStream(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	/**
	 * @deprecated
	 */
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		String methodCall = "getUnicodeStream(" + columnName + ")";
		try {
			return (InputStream) reportReturn(methodCall, this.realResultSet.getUnicodeStream(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateDate(int columnIndex, Date x) throws SQLException {
		String methodCall = "updateDate(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateDate(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateDate(String columnName, Date x) throws SQLException {
		String methodCall = "updateDate(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateDate(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public int getFetchSize() throws SQLException {
		String methodCall = "getFetchSize()";
		try {
			return reportReturn(methodCall, this.realResultSet.getFetchSize());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public SQLWarning getWarnings() throws SQLException {
		String methodCall = "getWarnings()";
		try {
			return (SQLWarning) reportReturn(methodCall, this.realResultSet.getWarnings());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		String methodCall = "getBinaryStream(" + columnIndex + ")";
		try {
			return (InputStream) reportReturn(methodCall, this.realResultSet.getBinaryStream(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public InputStream getBinaryStream(String columnName) throws SQLException {
		String methodCall = "getBinaryStream(" + columnName + ")";
		try {
			return (InputStream) reportReturn(methodCall, this.realResultSet.getBinaryStream(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void clearWarnings() throws SQLException {
		String methodCall = "clearWarnings()";
		try {
			this.realResultSet.clearWarnings();
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		String methodCall = "updateTimestamp(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateTimestamp(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
		String methodCall = "updateTimestamp(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateTimestamp(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public boolean first() throws SQLException {
		String methodCall = "first()";
		try {
			return reportReturn(methodCall, this.realResultSet.first());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public String getCursorName() throws SQLException {
		String methodCall = "getCursorName()";
		try {
			return (String) reportReturn(methodCall, this.realResultSet.getCursorName());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public int findColumn(String columnName) throws SQLException {
		String methodCall = "findColumn(" + columnName + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.findColumn(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean wasNull() throws SQLException {
		String methodCall = "wasNull()";
		try {
			return reportReturn(methodCall, this.realResultSet.wasNull());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		String methodCall = "updateBinaryStream(" + columnIndex + ", " + x + ", " + length + ")";
		try {
			this.realResultSet.updateBinaryStream(columnIndex, x, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
		String methodCall = "updateBinaryStream(" + columnName + ", " + x + ", " + length + ")";
		try {
			this.realResultSet.updateBinaryStream(columnName, x, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public String getString(int columnIndex) throws SQLException {
		String methodCall = "getString(" + columnIndex + ")";
		try {
			return (String) reportReturn(methodCall, this.realResultSet.getString(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public String getString(String columnName) throws SQLException {
		String methodCall = "getString(" + columnName + ")";
		try {
			return (String) reportReturn(methodCall, this.realResultSet.getString(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Reader getCharacterStream(int columnIndex) throws SQLException {
		String methodCall = "getCharacterStream(" + columnIndex + ")";
		try {
			return (Reader) reportReturn(methodCall, this.realResultSet.getCharacterStream(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public Reader getCharacterStream(String columnName) throws SQLException {
		String methodCall = "getCharacterStream(" + columnName + ")";
		try {
			return (Reader) reportReturn(methodCall, this.realResultSet.getCharacterStream(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void setFetchDirection(int direction) throws SQLException {
		String methodCall = "setFetchDirection(" + direction + ")";
		try {
			this.realResultSet.setFetchDirection(direction);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		String methodCall = "updateCharacterStream(" + columnIndex + ", " + x + ", " + length + ")";
		try {
			this.realResultSet.updateCharacterStream(columnIndex, x, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
		String methodCall = "updateCharacterStream(" + columnName + ", " + reader + ", " + length + ")";
		try {
			this.realResultSet.updateCharacterStream(columnName, reader, length);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public byte getByte(int columnIndex) throws SQLException {
		String methodCall = "getByte(" + columnIndex + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getByte(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public byte getByte(String columnName) throws SQLException {
		String methodCall = "getByte(" + columnName + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getByte(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateTime(int columnIndex, Time x) throws SQLException {
		String methodCall = "updateTime(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateTime(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateTime(String columnName, Time x) throws SQLException {
		String methodCall = "updateTime(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateTime(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public byte[] getBytes(int columnIndex) throws SQLException {
		String methodCall = "getBytes(" + columnIndex + ")";
		try {
			return (byte[]) reportReturn(methodCall, this.realResultSet.getBytes(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public byte[] getBytes(String columnName) throws SQLException {
		String methodCall = "getBytes(" + columnName + ")";
		try {
			return (byte[]) reportReturn(methodCall, this.realResultSet.getBytes(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean isAfterLast() throws SQLException {
		String methodCall = "isAfterLast()";
		try {
			return reportReturn(methodCall, this.realResultSet.isAfterLast());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
		String methodCall = "updateObject(" + columnIndex + ", " + x + ", " + scale + ")";
		try {
			this.realResultSet.updateObject(columnIndex, x, scale);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateObject(int columnIndex, Object x) throws SQLException {
		String methodCall = "updateObject(" + columnIndex + ", " + x + ")";
		try {
			this.realResultSet.updateObject(columnIndex, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateObject(String columnName, Object x, int scale) throws SQLException {
		String methodCall = "updateObject(" + columnName + ", " + x + ", " + scale + ")";
		try {
			this.realResultSet.updateObject(columnName, x, scale);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateObject(String columnName, Object x) throws SQLException {
		String methodCall = "updateObject(" + columnName + ", " + x + ")";
		try {
			this.realResultSet.updateObject(columnName, x);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public int getFetchDirection() throws SQLException {
		String methodCall = "getFetchDirection()";
		try {
			return reportReturn(methodCall, this.realResultSet.getFetchDirection());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public long getLong(int columnIndex) throws SQLException {
		String methodCall = "getLong(" + columnIndex + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getLong(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public long getLong(String columnName) throws SQLException {
		String methodCall = "getLong(" + columnName + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getLong(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean isFirst() throws SQLException {
		String methodCall = "isFirst()";
		try {
			return reportReturn(methodCall, this.realResultSet.isFirst());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void insertRow() throws SQLException {
		String methodCall = "insertRow()";
		try {
			this.realResultSet.insertRow();
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public float getFloat(int columnIndex) throws SQLException {
		String methodCall = "getFloat(" + columnIndex + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getFloat(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public float getFloat(String columnName) throws SQLException {
		String methodCall = "getFloat(" + columnName + ")";
		try {
			return reportReturn(methodCall, this.realResultSet.getFloat(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean isLast() throws SQLException {
		String methodCall = "isLast()";
		try {
			return reportReturn(methodCall, this.realResultSet.isLast());
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void setFetchSize(int rows) throws SQLException {
		String methodCall = "setFetchSize(" + rows + ")";
		try {
			this.realResultSet.setFetchSize(rows);
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void updateRow() throws SQLException {
		String methodCall = "updateRow()";
		try {
			this.realResultSet.updateRow();
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void beforeFirst() throws SQLException {
		String methodCall = "beforeFirst()";
		try {
			this.realResultSet.beforeFirst();
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	/**
	 * @deprecated
	 */
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		String methodCall = "getBigDecimal(" + columnIndex + ", " + scale + ")";
		try {
			return (BigDecimal) reportReturn(methodCall, this.realResultSet.getBigDecimal(columnIndex, scale));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	/**
	 * @deprecated
	 */
	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
		String methodCall = "getBigDecimal(" + columnName + ", " + scale + ")";
		try {
			return (BigDecimal) reportReturn(methodCall, this.realResultSet.getBigDecimal(columnName, scale));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		String methodCall = "getBigDecimal(" + columnIndex + ")";
		try {
			return (BigDecimal) reportReturn(methodCall, this.realResultSet.getBigDecimal(columnIndex));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		String methodCall = "getBigDecimal(" + columnName + ")";
		try {
			return (BigDecimal) reportReturn(methodCall, this.realResultSet.getBigDecimal(columnName));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public void afterLast() throws SQLException {
		String methodCall = "afterLast()";
		try {
			this.realResultSet.afterLast();
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

	public void refreshRow() throws SQLException {
		String methodCall = "refreshRow()";
		try {
			this.realResultSet.refreshRow();
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		String methodCall = "unwrap(" + (iface == null ? "null" : iface.getName()) + ")";
		try {
			return (T) reportReturn(
					methodCall,
					(iface != null) && ((iface == ResultSet.class) || (iface == Spy.class)) ? this : this.realResultSet
							.unwrap(iface));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		String methodCall = "isWrapperFor(" + (iface == null ? "null" : iface.getName()) + ")";
		try {
			return reportReturn(methodCall, ((iface != null) && ((iface == ResultSet.class) || (iface == Spy.class)))
					|| (this.realResultSet.isWrapperFor(iface)));
		} catch (SQLException s) {
			reportException(methodCall, s);
			throw s;
		}
	}
}
