package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : EntityType.java
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


import java.lang.Enum;

public enum EntityType {

	Obj( 1, "Obj"),
	AbsTime(2,"AbsTime"),
	Bool(3,"Bool"),
	Contract(4,"Contract"),
	Enum(5,"Enum"),
	Feed(6,"Feed"),
	Int(7,"Int"),
	List(8,"List"),
	Real(9,"Real"),
	Ref(10,"Ref"),
	RelTime(11,"RelTime"),
	Str(12,"Str"),
	Uri(13,"Uri");
	
	private Integer ident;
	private String libelle;
	
	EntityType(Integer ident, String name){
		this.ident = ident;
		this.libelle = name;
	}


	public Integer getIdent() {
		return ident;
	}

	public String getLibelle() {
		return libelle;
	}
	
	public static EntityType getEnum(Integer ident) {
		
		if( ident.intValue() == EntityType.AbsTime.ident.intValue()) return EntityType.AbsTime;
		else if( ident.intValue() == EntityType.Bool.ident.intValue()) return EntityType.Bool;
		else if( ident.intValue() == EntityType.Contract.ident.intValue()) return EntityType.Contract;
		else if( ident.intValue() == EntityType.Enum.ident.intValue()) return EntityType.Enum;
		else if( ident.intValue() == EntityType.Feed.ident.intValue()) return EntityType.Feed;
		else if( ident.intValue() == EntityType.Int.ident.intValue()) return EntityType.Int;
		else if( ident.intValue() == EntityType.List.ident.intValue()) return EntityType.List;
		else if( ident.intValue() == EntityType.Obj.ident.intValue()) return EntityType.Obj;
		else if( ident.intValue() == EntityType.Real.ident.intValue()) return EntityType.Real;
		else if( ident.intValue() == EntityType.Ref.ident.intValue()) return EntityType.Ref;
		else if( ident.intValue() == EntityType.RelTime.ident.intValue()) return EntityType.RelTime;
		else if( ident.intValue() == EntityType.Str.ident.intValue()) return EntityType.Str;
		else if( ident.intValue() == EntityType.Uri.ident.intValue()) return EntityType.Uri;
		
		return null;
		
	}
}
