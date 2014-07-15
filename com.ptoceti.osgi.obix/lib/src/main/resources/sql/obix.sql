PRAGMA page_size=4092;
DROP TABLE IF EXISTS "object";
CREATE TABLE "object" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,
	"name" VARCHAR,
	"uri" INTEGER,
	"uri_hash" VARCHAR,
	"contract_id" INTEGER,
	"isnullable" BOOLEAN,
	"icon_id" INTEGER,
	"displayname" VARCHAR,
	"display" VARCHAR,
	"writable" BOOLEAN,
	"status_id" INTEGER,
	"type_id" INTEGER,
	"parent_id" INTEGER,
	"created_ts" INTEGER,
	"modified_ts" INTEGER,
	"value_int" INTEGER,
	"value_text" VARCHAR,
	"value_bool" BOOLEAN,
	"value_real" REAL,
	"min" INTEGER,
	"max" INTEGER,
	"min_real" REAL,
	"max_real" REAL,
	"precision" INTEGER,
	"range_uri_id" INTEGER,
	"unit" VARCHAR,
	"in_contract_id" INTEGER,
	"out_contract_id" INTEGER,
	"of_contract_id" INTEGER
	);


DROP TABLE IF EXISTS "contract";
CREATE TABLE "contract" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "object_id" INTEGER);
DROP TABLE IF EXISTS "contracturi";
CREATE TABLE "contracturi" ("contract_id" INTEGER NOT NULL, "uri_id" INTEGER NOT NULL);

CREATE INDEX create_ts_idx ON "object"("parent_id" ASC, "created_ts" ASC);
CREATE INDEX parent_id_idx ON "object"("parent_id" ASC);
CREATE INDEX contract_id_idx ON "object"("contract_id" ASC);
CREATE INDEX modified_ts_idx ON "object"("modified_ts" ASC);
CREATE INDEX urihash_idx ON "object"("uri_hash" ASC);

CREATE INDEX contract_object_id_idx ON "contract"("object_id" ASC);
PRAGMA journal_mode=WAL;
PRAGMA wal_autocheckpoint=400;
