package com.ptoceti.osgi.data;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Data
 * FILENAME : AbstractResultSetSingleHandler.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2014 ptoceti
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public abstract class AbstractResultSetSingleHandler implements
		IResultSetSingleHandler {

	protected Boolean getBoolean(ResultSet rs, String colName) {
		Boolean result = null;
		try {
			boolean value = rs.getBoolean(colName);
			if (!rs.wasNull())
				result = new Boolean(value);

		} catch (SQLException ex) {

		}

		return result;
	}

	protected Boolean getBoolean(ResultSet rs, int col) {
		Boolean result = null;
		try {
			boolean value = rs.getBoolean(col);
			if (!rs.wasNull())
				result = new Boolean(value);

		} catch (SQLException ex) {

		}

		return result;
	}

	protected String getString(ResultSet rs, String colName) {
		String result = null;
		try {
			result = rs.getString(colName);

		} catch (SQLException ex) {

		}

		return result;
	}

	protected String getString(ResultSet rs, int col) {
		String result = null;
		try {
			result = rs.getString(col);

		} catch (SQLException ex) {

		}

		return result;
	}

	protected Long getLong(ResultSet rs, String colName) {
		Long result = null;
		try {
			long value = rs.getLong(colName);
			if (!rs.wasNull())
				result = new Long(value);

		} catch (SQLException ex) {

		}

		return result;
	}

	protected Long getLong(ResultSet rs, int col) {
		Long result = null;
		try {
			long value = rs.getLong(col);
			if (!rs.wasNull())
				result = new Long(value);

		} catch (SQLException ex) {

		}

		return result;
	}

	protected Integer getInteger(ResultSet rs, String colName) {
		Integer result = null;
		try {
			int value = rs.getInt(colName);
			if (!rs.wasNull())
				result = new Integer(value);

		} catch (SQLException ex) {

		}

		return result;
	}

	protected Integer getInteger(ResultSet rs, int col) {
		Integer result = null;
		try {
			int value = rs.getInt(col);
			if (!rs.wasNull())
				result = new Integer(value);

		} catch (SQLException ex) {

		}

		return result;
	}

	protected Float getFloat(ResultSet rs, String colName) {
		Float result = null;
		try {
			float value = rs.getFloat(colName);
			if (!rs.wasNull())
				result = new Float(value);

		} catch (SQLException ex) {

		}

		return result;
	}
	
	protected Float getFloat(ResultSet rs, int col) {
		Float result = null;
		try {
			float value = rs.getFloat(col);
			if (!rs.wasNull())
				result = new Float(value);

		} catch (SQLException ex) {

		}

		return result;
	}
	protected Double getDouble(ResultSet rs, String colName) {
		Double result = null;
		try {
			double value = rs.getDouble(colName);
			if (!rs.wasNull())
				result = new Double(value);

		} catch (SQLException ex) {

		}

		return result;
	}

	protected Double getDouble(ResultSet rs, int col) {
		Double result = null;
		try {
			double value = rs.getDouble(col);
			if (!rs.wasNull())
				result = new Double(value);

		} catch (SQLException ex) {

		}

		return result;
	}

	protected Date getDate(ResultSet rs, String colName) {
		Date result = null;
		try {
			Timestamp value = rs.getTimestamp(colName);
			if (!rs.wasNull())
				result = new Date(value.getTime());

		} catch (SQLException ex) {

		}

		return result;
	}

	protected Date getDate(ResultSet rs, int col) {
		Date result = null;
		try {
			Timestamp value = rs.getTimestamp(col);
			if (!rs.wasNull())
				result = new Date(value.getTime());

		} catch (SQLException ex) {

		}

		return result;
	}

	protected Calendar getCalendar(ResultSet rs, String colName) {
		Calendar result = null;
		try {
			Timestamp value = rs.getTimestamp(colName);
			if (!rs.wasNull()) {
				result = Calendar.getInstance();
				result.setTimeInMillis(value.getTime());
			}
		} catch (SQLException ex) {

		}

		return result;
	}

	protected Calendar getCalendar(ResultSet rs, int col) {
		Calendar result = null;
		try {
			Timestamp value = rs.getTimestamp(col);
			if (!rs.wasNull()) {
				result = Calendar.getInstance();
				result.setTimeInMillis(value.getTime());
			}
		} catch (SQLException ex) {

		}

		return result;
	}

	public abstract void getRowAsBean(ResultSet rs) throws Exception;
}
