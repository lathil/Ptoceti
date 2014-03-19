package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : IntEntity.java
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
import java.util.ArrayList;
import java.util.List;

import com.ptoceti.osgi.data.ResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.data.ResultSetSingleHandler;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.impl.entity.ObjEntity.ObjResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.obix.impl.entity.ObjEntity.ObjResultSetHandler;
import com.ptoceti.osgi.obix.impl.entity.ObjEntity.ObjResultSetMultipleHandler;
import com.ptoceti.osgi.obix.impl.entity.RealEntity.ObjRealResultSetMultipleHandler;

public class IntEntity extends ObjEntity implements ValEntity {

	private static final String CREATE_INT = "insert into int (object_id, min, max, unit_uri_id, value ) values (?,?,?,?,?)";
	private static final String DELETE_INT = "delete from int where int.id=?";
	// private static final String SEARCH_INT_BY_HREF =
	// "select object.* from object, uri where object.uri_id = uri.id and uri.value=?";
	private static final String SEARCH_INT_BY_OBJECT_ID = "select int.* from int where int.object_id=?";

	private static final String SEARCH_OBJ_BY_PARENT_ID_AND_TIMESTAMP = "select object.*, int.id as int_id, int.min, int.max, int.value, int.unit_uri_id  from object, int  where object.id = int.object_id and object.parent_id=? and object.created_ts > ? and object.created_ts <= ?";

	private static final String UPDATE_INT = "update int set object_id = ?, min = ?, max = ?, unit_uri_id = ?, value = ? where id = ? ";

	private static final String COL_INT_ID = "id";
	private static final String COL_OBJ_INT_ID = "int_id";
	private static final String COL_INT_OBJECT_ID = "object_id";
	private static final String COL_INT_MIN = "min";
	private static final String COL_INT_MAX = "max";
	private static final String COL_INT_VALUE = "value";
	private static final String COL_INT_UNIT_URI_ID = "unit_uri_id";

	private Integer intid;
	private Integer unitUriId;

	public IntEntity(){
		super(EntityType.Int);
	}
	
	public IntEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchDetailsById();
	}

	public IntEntity(Int obixInt) {
		super(obixInt);
		setObjtype(EntityType.Int);
	}

	public void create() throws EntityException {

		super.create();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Int) getObixObject()).getMin());
		params.add(((Int) getObixObject()).getMax());

		Uri unitUri = ((Int) getObixObject()).getUnit();
		if (unitUri != null) {
			UriEntity uriEntity = new UriEntity(unitUri);
			uriEntity.create();
			setUnitUriId(uriEntity.getUriId());
		}

		params.add(getUnitUriId());
		params.add(((Int) getObixObject()).getVal());
		update(CREATE_INT, params.toArray(),
				new IntResultSetGeneratedKeysHandler(this));
	}

	public void delete() throws EntityException {

		super.delete();

		if (getIntid() != null) {
			UriEntity uriEntity = new UriEntity(new Uri());
			uriEntity.setId(getIntid());
			uriEntity.delete();
		}

		ArrayList params = new ArrayList();
		params.add(getIntid());
		update(DELETE_INT, params.toArray(), null);
	}

	public boolean fetchByHref() throws EntityException {

		boolean found = super.fetchByHref();
		if (found) {
			ArrayList params = new ArrayList();
			params.add(getId());

			query(SEARCH_INT_BY_OBJECT_ID, params.toArray(),
					new IntResultSetHandler(this));
		}
		return found;
	}

	public boolean fetchByObjectId() throws EntityException {

		boolean found = super.fetchByObjectId();
		if( found )fetchDetailsById();
		return found;

	}

	public void fetchDetailsById() throws EntityException {
		ArrayList params = new ArrayList();
		params.add(getId());

		query(SEARCH_INT_BY_OBJECT_ID, params.toArray(),
				new IntResultSetHandler(this));
	}

	public List fetchByParentIdFilterByTimestamsp(int parentId, long millisFrom, long millisTo) throws EntityException  {
		
		ArrayList params = new ArrayList();
		params.add(parentId);
		params.add(millisFrom);
		params.add(millisTo);

		ArrayList childsList = new ArrayList();

		queryMultiple(SEARCH_OBJ_BY_PARENT_ID_AND_TIMESTAMP, params.toArray(),
				new ObjIntResultSetMultipleHandler(childsList));

		return childsList;
	}

	public void update() throws EntityException {

		super.update();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Int) getObixObject()).getMin());
		params.add(((Int) getObixObject()).getMax());
		params.add(getUnitUriId());
		params.add(((Int) getObixObject()).getVal());
		params.add(getIntid());

		update(UPDATE_INT, params.toArray(), null);
	}

	public void setIntid(Integer intid) {
		this.intid = intid;
	}

	public Integer getIntid() {
		return intid;
	}

	public void setUnitUriId(Integer unitUriId) {
		this.unitUriId = unitUriId;
	}

	public Integer getUnitUriId() {
		return unitUriId;
	}
	
	public class ObjIntResultSetMultipleHandler extends ObjResultSetMultipleHandler {

		public ObjIntResultSetMultipleHandler(List entityList) {
			super(entityList);
	
		}
		
		@Override
		public void getRowAsBean(ResultSet rs) throws Exception {
			super.getRowAsBean(rs);
			
			Integer id = getInteger(rs, COL_OBJ_INT_ID);
			if (id != null)
				((IntEntity)entity).setIntid(id);

			((Int) entity.getObixObject()).setMax(getInteger(rs, COL_INT_MAX));
			((Int) entity.getObixObject()).setMin(getInteger(rs, COL_INT_MIN));
			((Int) entity.getObixObject()).setVal(getInteger(rs, COL_INT_VALUE));

			((IntEntity)entity).setUnitUriId(getInteger(rs, COL_INT_UNIT_URI_ID));
			
			entity.setDetailsfetched(true);
			
		}
		
		@Override
		public void getNextBean() {
			Int obixObj = new Int();
			entity = new IntEntity(obixObj);
		}
	}

	public class IntResultSetHandler extends ResultSetSingleHandler {

		private IntEntity entity;

		public IntResultSetHandler(IntEntity entity) {
			this.entity = entity;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer id = getInteger(rs, COL_INT_ID);
			if (id != null)
				entity.setIntid(id);

			((Int) entity.getObixObject()).setMax(getInteger(rs, COL_INT_MAX));
			((Int) entity.getObixObject()).setMin(getInteger(rs, COL_INT_MIN));
			((Int) entity.getObixObject())
					.setVal(getInteger(rs, COL_INT_VALUE));

			entity.setUnitUriId(getInteger(rs, COL_INT_UNIT_URI_ID));
			
			entity.setDetailsfetched(true);
			
			entity.setDetailsfetched(true);

		}
	}

	public class IntResultSetGeneratedKeysHandler extends
			ResultSetGeneratedKeysHandler {

		private IntEntity entity;

		public IntResultSetGeneratedKeysHandler(IntEntity entity) {
			this.entity = entity;
		}

		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setIntid(getRowID(rs));
		}
	}
}
