package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : StrEntity.java
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

import com.ptoceti.osgi.data.ResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.data.ResultSetSingleHandler;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.impl.entity.RealEntity.RealResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.obix.impl.entity.RealEntity.RealResultSetHandler;

public class StrEntity extends ObjEntity implements ValEntity {

	private static final String CREATE_STR = "insert into str (object_id, min, max, value ) values (?,?,?,?)";
	// private static final String SEARCH_INT_BY_HREF =
	// "select object.* from object, uri where object.uri_id = uri.id and uri.value=?"
	// ;
	private static final String SEARCH_STR_BY_OBJECT_ID = "select str.* from str where str.object_id=?";

	private static final String UPDATE_STR = "update str set object_id = ?, min = ?, max = ?, value = ? where id = ? ";

	private static final String COL_STR_ID = "id";
	private static final String COL_STR_OBJECT_ID = "object_id";
	private static final String COL_STR_MIN = "min";
	private static final String COL_STR_MAX = "max";
	private static final String COL_STR_VALUE = "value";

	private Integer strid;

	public StrEntity() {
		super(EntityType.Str);
	}
	
	public StrEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchDetailsById();
	}

	public StrEntity(Str obixObj) {
		super(obixObj);
		setObjtype(EntityType.Str);
	}

	public void create() throws EntityException {

		super.create();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Real) getObixObject()).getMin());
		params.add(((Real) getObixObject()).getMax());
		params.add(((Real) getObixObject()).getVal());

		update(CREATE_STR, params.toArray(),
				new StrResultSetGeneratedKeysHandler(this));
	}

	public boolean fetchByHref() throws EntityException {

		boolean found = super.fetchByHref();
		if (found) {
			ArrayList params = new ArrayList();
			params.add(getId());

			query(SEARCH_STR_BY_OBJECT_ID, params.toArray(),
					new StrResultSetHandler(this));
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

		query(SEARCH_STR_BY_OBJECT_ID, params.toArray(),
				new StrResultSetHandler(this));
	}

	public void update() throws EntityException {

		super.update();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Real) getObixObject()).getMin());
		params.add(((Real) getObixObject()).getMax());
		params.add(((Real) getObixObject()).getVal());
		params.add(getStrid());

		update(UPDATE_STR, params.toArray(), null);
	}

	public void setStrid(Integer strid) {
		this.strid = strid;
	}

	public Integer getStrid() {
		return strid;
	}

	public class StrResultSetHandler extends ResultSetSingleHandler {

		private StrEntity entity;

		public StrResultSetHandler(StrEntity entity) {
			this.entity = entity;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer id = getInteger(rs, COL_STR_ID);
			if (id != null)
				entity.setStrid(id);

			((Str) entity.getObixObject()).setMax(getInteger(rs, COL_STR_MAX));
			((Str) entity.getObixObject()).setMin(getInteger(rs, COL_STR_MIN));
			((Str) entity.getObixObject()).setVal(getString(rs, COL_STR_VALUE));
			
			entity.setDetailsfetched(true);
		}
	}

	public class StrResultSetGeneratedKeysHandler extends
			ResultSetGeneratedKeysHandler {

		private StrEntity entity;

		public StrResultSetGeneratedKeysHandler(StrEntity entity) {
			this.entity = entity;
		}

		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setStrid(getRowID(rs));
		}
	}

}
