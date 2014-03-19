package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : RealEntity.java
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
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.impl.entity.IntEntity.IntResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.obix.impl.entity.IntEntity.IntResultSetHandler;
import com.ptoceti.osgi.obix.impl.entity.ObjEntity.ObjResultSetMultipleHandler;

public class RealEntity extends ObjEntity implements ValEntity {

	private Integer realid;
	private Integer unitUriId;

	private static final String CREATE_REAL = "insert into real (object_id, min, max, unit_uri_id, value, precision ) values (?,?,?,?,?,?)";
	private static final String DELETE_REAL = "delete from real where real.id=?";

	private static final String SEARCH_REAL_BY_OBJECT_ID = "select real.* from real where real.object_id=?";
	
	private static final String SEARCH_OBJ_BY_PARENT_ID_AND_TIMESTAMP = "select object.*, real.id as real_id, real.min, real.max, real.value, real.unit_uri_id, real.precision  from object, real  where object.id = real.object_id and object.parent_id=? and object.created_ts > ? and object.created_ts <= ?";


	private static final String UPDATE_REAL = "update real set object_id = ?, min = ?, max = ?, unit_uri_id = ?, value = ?, precision = ? where id = ? ";

	private static final String COL_REAL_ID = "id";
	private static final String COL_OBJ_REAL_ID = "real_id";
	private static final String COL_REAL_OBJECT_ID = "object_id";
	private static final String COL_REAL_MIN = "min";
	private static final String COL_REAL_MAX = "max";
	private static final String COL_REAL_VALUE = "value";
	private static final String COL_REAL_UNIT_URI_ID = "unit_uri_id";
	private static final String COL_REAL_PRECISION = "precision";

	public RealEntity() {
		super(EntityType.Real);
	}
	
	public RealEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchDetailsById();
	}

	public RealEntity(Real obixReal) {
		super(obixReal);
		setObjtype(EntityType.Real);
	}

	public void create() throws EntityException {

		super.create();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Real) getObixObject()).getMin());
		params.add(((Real) getObixObject()).getMax());

		Uri unitUri = ((Real) getObixObject()).getUnit();
		if (unitUri != null) {
			UriEntity uriEntity = new UriEntity(unitUri);
			uriEntity.create();
			setUnitUriId(uriEntity.getUriId());
		}

		params.add(getUnitUriId());
		params.add(((Real) getObixObject()).getVal());
		params.add(((Real) getObixObject()).getPrecision());

		update(CREATE_REAL, params.toArray(),
				new RealResultSetGeneratedKeysHandler(this));
	}

	public void delete() throws EntityException {

		super.delete();

		if (getRealid() != null) {
			UriEntity uriEntity = new UriEntity(new Uri());
			uriEntity.setId(getRealid());
			uriEntity.delete();
		}

		ArrayList params = new ArrayList();
		params.add(getRealid());
		update(DELETE_REAL, params.toArray(), null);
	}

	public boolean fetchByHref() throws EntityException {

		boolean found = super.fetchByHref();
		if (found) {
			ArrayList params = new ArrayList();
			params.add(getId());

			query(SEARCH_REAL_BY_OBJECT_ID, params.toArray(),
					new RealResultSetHandler(this));
		}
		return found;
	}

	public boolean fetchByObjectId() throws EntityException {

		boolean found = super.fetchByObjectId();
		if (found)
			fetchDetailsById();

		return found;

	}

	public void fetchDetailsById() throws EntityException {
		ArrayList params = new ArrayList();
		params.add(getId());

		query(SEARCH_REAL_BY_OBJECT_ID, params.toArray(),
				new RealResultSetHandler(this));
		
		UriEntity uriEntity = new UriEntity( new Uri());
		uriEntity.setUriId(getUnitUriId());
		if(uriEntity.fetchById()){
			((Real)getObixObject()).setUnit((Uri)uriEntity.getObixObject());
		}
	}

	public List fetchByParentIdFilterByTimestamsp(int parentId, long millisFrom, long millisTo) throws EntityException  {
		
		ArrayList params = new ArrayList();
		params.add(parentId);
		params.add(millisFrom);
		params.add(millisTo);

		ArrayList childsList = new ArrayList();

		queryMultiple(SEARCH_OBJ_BY_PARENT_ID_AND_TIMESTAMP, params.toArray(),
				new ObjRealResultSetMultipleHandler(childsList));

		return childsList;
	}
	
	
	public void update() throws EntityException {

		super.update();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Real) getObixObject()).getMin());
		params.add(((Real) getObixObject()).getMax());
		params.add(getUnitUriId());
		params.add(((Real) getObixObject()).getVal());
		params.add(((Real) getObixObject()).getPrecision());
		params.add(getRealid());

		update(UPDATE_REAL, params.toArray(), null);
	}

	public void setRealid(Integer realid) {
		this.realid = realid;
	}

	public Integer getRealid() {
		return realid;
	}

	public void setUnitUriId(Integer unitUriId) {
		this.unitUriId = unitUriId;
	}

	public Integer getUnitUriId() {
		return unitUriId;
	}
	
	public class ObjRealResultSetMultipleHandler extends ObjResultSetMultipleHandler {

		public ObjRealResultSetMultipleHandler(List entityList) {
			super(entityList);
	
		}
		
		@Override
		public void getRowAsBean(ResultSet rs) throws Exception {
			super.getRowAsBean(rs);
			
			Integer id = getInteger(rs, COL_OBJ_REAL_ID);
			if (id != null) ((RealEntity)entity).setRealid(id);

			((Real) entity.getObixObject()).setMax(getFloat(rs, COL_REAL_MAX));
			((Real) entity.getObixObject()).setMin(getFloat(rs, COL_REAL_MIN));
			((Real) entity.getObixObject()).setVal(getDouble(rs, COL_REAL_VALUE));
			((Real) entity.getObixObject()).setPrecision(getInteger(rs,
					COL_REAL_PRECISION));

			((RealEntity)entity).setUnitUriId(getInteger(rs, COL_REAL_UNIT_URI_ID));
			
			entity.setDetailsfetched(true);
			
		}
		
		@Override
		public void getNextBean() {
			Real obixObj = new Real();
			entity = new RealEntity(obixObj);
		}
	}

	public class RealResultSetHandler extends ResultSetSingleHandler {

		private RealEntity entity;

		public RealResultSetHandler(RealEntity entity) {
			this.entity = entity;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer id = getInteger(rs, COL_REAL_ID);
			if (id != null)
				entity.setRealid(id);

			((Real) entity.getObixObject()).setMax(getFloat(rs, COL_REAL_MAX));
			((Real) entity.getObixObject()).setMin(getFloat(rs, COL_REAL_MIN));
			((Real) entity.getObixObject()).setVal(getDouble(rs, COL_REAL_VALUE));
			((Real) entity.getObixObject()).setPrecision(getInteger(rs,
					COL_REAL_PRECISION));

			entity.setUnitUriId(getInteger(rs, COL_REAL_UNIT_URI_ID));
			
			entity.setDetailsfetched(true);

		}
	}

	public class RealResultSetGeneratedKeysHandler extends
			ResultSetGeneratedKeysHandler {

		private RealEntity entity;

		public RealResultSetGeneratedKeysHandler(RealEntity entity) {
			this.entity = entity;
		}

		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setRealid(getRowID(rs));
		}
	}
}
