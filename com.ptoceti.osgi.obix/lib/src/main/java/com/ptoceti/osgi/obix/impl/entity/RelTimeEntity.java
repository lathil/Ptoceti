package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : RelTimeEntity.java
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
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.impl.entity.RelTimeEntity.RelTimeResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.obix.impl.entity.RelTimeEntity.RelTimeResultSetHandler;

public class RelTimeEntity extends ObjEntity implements ValEntity {

	private static final String CREATE_RELTIME = "insert into reltime (object_id, min_reltime_id, max_reltime_id, value ) values (?,?,?,?)";
	// private static final String SEARCH_INT_BY_HREF =
	// "select object.* from object, uri where object.uri_id = uri.id and uri.value=?"
	// ;
	private static final String DELETE_RELTIME = "delete from reltime where reltime.id=?";
	private static final String SEARCH_RELTIME_BY_OBJECT_ID = "select reltime.* from reltime where reltime.object_id=?";

	private static final String UPDATE_RELTIME = "update reltime set object_id = ?, min_reltime_id = ?, max_reltime_id = ?, value = ? where id = ? ";

	private static final String COL_RELTIME_ID = "id";
	private static final String COL_RELTIME_OBJECT_ID = "object_id";
	private static final String COL_RELTIME_MIN_ID = "min_reltime_id";
	private static final String COL_RELTIME_MAX_ID = "max_reltime_id";
	private static final String COL_RELTIME_VALUE = "value";

	private Integer RelTimeId;

	public RelTimeEntity() {
		super(EntityType.RelTime);
	}
	
	public RelTimeEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchDetailsById();
	}

	public RelTimeEntity(Reltime obixObj) {
		super(obixObj);
		setObjtype(EntityType.RelTime);
	}

	public void create() throws EntityException {

		super.create();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Reltime) getObixObject()).getMin());
		params.add(((Reltime) getObixObject()).getMax());
		params.add(((Reltime) getObixObject()).getVal());
		update(CREATE_RELTIME, params.toArray(),
				new RelTimeResultSetGeneratedKeysHandler(this));
	}

	public void delete() throws EntityException {

		super.delete();

		if (getRelTimeId() != null) {
			UriEntity uriEntity = new UriEntity(new Uri());
			uriEntity.setId(getRelTimeId());
			uriEntity.delete();
		}

		ArrayList params = new ArrayList();
		params.add(getRelTimeId());
		update(DELETE_RELTIME, params.toArray(), null);
	}

	public boolean fetchByHref() throws EntityException {

		boolean found = super.fetchByHref();
		if (found) {
			ArrayList params = new ArrayList();
			params.add(getId());

			query(SEARCH_RELTIME_BY_OBJECT_ID,
					params.toArray(), new RelTimeResultSetHandler(this));
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

		query(SEARCH_RELTIME_BY_OBJECT_ID, params.toArray(),
				new RelTimeResultSetHandler(this));
	}

	public void update() throws EntityException {

		super.update();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Reltime) getObixObject()).getMin());
		params.add(((Reltime) getObixObject()).getMax());
		params.add(((Reltime) getObixObject()).getVal());
		params.add(getRelTimeId());

		update(UPDATE_RELTIME, params.toArray(), null);
	}

	public void setRelTimeId(Integer RelTimeId) {
		this.RelTimeId = RelTimeId;
	}

	public Integer getRelTimeId() {
		return RelTimeId;
	}

	public class RelTimeResultSetHandler extends ResultSetSingleHandler {

		private RelTimeEntity entity;

		public RelTimeResultSetHandler(RelTimeEntity entity) {
			this.entity = entity;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer id = getInteger(rs, COL_RELTIME_ID);
			if (id != null)
				entity.setRelTimeId(id);

			// ((Reltime)entity.getObixObject()).setMax( getInteger(rs,
			// COL_INT_MAX));
			// ((Reltime)entity.getObixObject()).setMin( getInteger(rs,
			// COL_INT_MIN));
			((Reltime) entity.getObixObject()).setVal(getLong(rs,
					COL_RELTIME_VALUE));
			
			entity.setDetailsfetched(true);

		}
	}

	public class RelTimeResultSetGeneratedKeysHandler extends
			ResultSetGeneratedKeysHandler {

		private RelTimeEntity entity;

		public RelTimeResultSetGeneratedKeysHandler(RelTimeEntity entity) {
			this.entity = entity;
		}

		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setRelTimeId(getRowID(rs));
		}
	}
}
