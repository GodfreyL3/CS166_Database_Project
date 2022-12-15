DROP INDEX IF EXISTS user_ID;
DROP INDEX IF EXISTS user_name;

CREATE INDEX user_ID
ON Users
USING BTREE (userID);

CREATE INDEX user_name
ON Users
USING BTREE (name);

CREATE INDEX store_ID
ON Store
USING BTREE (storeID);

CREATE INDEX product_name
ON Product
USING BTREE (productName);

