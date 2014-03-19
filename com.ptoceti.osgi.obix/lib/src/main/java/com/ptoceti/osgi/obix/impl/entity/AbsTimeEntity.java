package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : AbsTimeEntity.java
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
import java.util.Date;

import com.ptoceti.osgi.data.ResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.data.ResultSetSingleHandler;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.impl.entity.IntEntity.IntResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.obix.impl.entity.IntEntity.IntResultSetHandler;

public class AbsTimeEntity extends ObjEntity implements ValEntity {

	private static final String CREATE_ABSTIME = "insert into abstime (object_id, min_abstime_id, max_abstime_id, value ) values (?,?,?,?)";
	// private static final String SEARCH_INT_BY_HREF =
	// "select object.* from object, uri where object.uri_id = uri.id and uri.value=?"
	// ;
	private static final String DELETE_ABSTIME = "delete from abstime where abstime.id=?";
	private static final String SEARCH_ABSTIME_BY_OBJECT_ID = "select abstime.* from abstime where abstime.object_id=?";
	private static final String SEARCH_ABSTIME_BY_PARENT_ID = "select abstime.* from abstime, object where abstime.object_id = object.id qnd object.parent_id=?";

	private static final String UPDATE_ABSTIME = "update abstime set object_id = ?, min_abstime_id = ?, max_abstime_id = ?, value = ? where id = ? ";

	private static final String COL_ABSTIME_ID = "id";
	private static final String COL_ABSTIME_OBJECT_ID = "object_id";
	private static final String COL_ABSTIME_MIN_ID = "min_abstime_id";
	private static final String COL_ABSTIME_MAX_ID = "max_abstime_id";
	private static final String COL_ABSTIME_VALUE = "value";

	private Integer absTimeId;
	
	public AbsTimeEntity(){
		super(EntityType.AbsTime);
	}

	public AbsTimeEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchDetailsById();
	}

	public AbsTimeEntity(Abstime obixObj) {
		super(obixObj);

		setObjtype(EntityType.AbsTime);
	}

	public void create() throws EntityException {

		super.create();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Abstime) getObixObject()).getMin());
		params.add(((Abstime) getObixObject()).getMax());
		params.add(((Abstime) getObixObject()).getVal());
		update(CREATE_ABSTIME, params.toArray(),
				new AbsTimeResultSetGeneratedKeysHandler(this));
	}

	public void delete() throws EntityException {

		super.delete();

		if (getAbsTimeId() != null) {
			UriEntity uriEntity = new UriEntity(new Uri());
			uriEntity.setId(getAbsTimeId());
			uriEntity.delete();
		}

		ArrayList params = new ArrayList();
		params.add(getAbsTimeId());
		update(DELETE_ABSTIME, params.toArray(), null);
	}

	public boolean fetchByHref() throws EntityException {

		if (super.fetchByHref() == true) {

			ArrayList params = new ArrayList();
			params.add(getId());

			query(SEARCH_ABSTIME_BY_OBJECT_ID,
					params.toArray(), new AbsTimeResultSetHandler(this));
			return true;
		}

		return false;
	}

	public boolean fetchByObjectId() throws EntityException {

		if (super.fetchByObjectId() == true) {
			fetchDetailsById();
			return true;
		}

		return false;
	}

	public void fetchDetailsById() throws EntityException {

		ArrayList params = new ArrayList();
		params.add(getId());

		query(SEARCH_ABSTIME_BY_OBJECT_ID, params.toArray(),
				new AbsTimeResultSetHandler(this));
	}

	public void update() throws EntityException {

		super.update();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Abstime) getObixObject()).getMin());
		params.add(((Abstime) getObixObject()).getMax());
		params.add(((Abstime) getObixObject()).getVal());
		params.add(getAbsTimeId());

		update(UPDATE_ABSTIME, params.toArray(), null);
	}

	public void setAbsTimeId(Integer absTimeId) {
		this.absTimeId = absTimeId;
	}

	public Integer getAbsTimeId() {
		return absTimeId;
	}

	public class AbsTimeResultSetHandler extends ResultSetSingleHandler {

		private AbsTimeEntity entity;

		public AbsTimeResultSetHandler(AbsTimeEntity entity) {
			this.entity = entity;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer id = getInteger(rs, COL_ABSTIME_ID);
			if (id != null)
				entity.setAbsTimeId(id);

			// ((Abstime)entity.getObixObject()).setMax( getInteger(rs,
			// COL_INT_MAX));
			// ((Abstime)entity.getObixObject()).setMin( getInteger(rs,
			// COL_INT_MIN));
			((Abstime) entity.getObixObject()).setVal(getDate(rs,
					COL_ABSTIME_VALUE));
			
			entity.setDetailsfetched(true);

		}
	}

	public class AbsTimeResultSetGeneratedKeysHandler extends
			ResultSetGeneratedKeysHandler {

		private AbsTimeEntity entity;

		public AbsTimeResultSetGeneratedKeysHandler(AbsTimeEntity entity) {
			this.entity = entity;
		}

		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setAbsTimeId(getRowID(rs));
		}
	}
}
