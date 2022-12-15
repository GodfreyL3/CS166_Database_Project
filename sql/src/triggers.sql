
--  Request Trigger --
DROP SEQUENCE IF EXISTS prequest_sequence;
CREATE SEQUENCE prequest_sequence START WITH 30;

CREATE OR REPLACE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION product_requests()
RETURNS "trigger" AS
$BODY$
BEGIN
    
    -- Insert serial
    NEW.requestNumber = nextval('prequest_sequence');

    -- Update stock
    UPDATE Product AS p 
        SET numberOfUnits = numberOfUnits + NEW.unitsRequested
        WHERE NEW.storeID = p.storeID
	AND NEW.productName = p.productName;

    RETURN new;
END;
$BODY$
LANGUAGE PLPGSQL VOLATILE;

DROP TRIGGER IF EXISTS product_supply_table ON ProductSupplyRequests;
CREATE TRIGGER product_supply_table
	BEFORE INSERT ON ProductSupplyRequests
	FOR EACH ROW
	EXECUTE PROCEDURE product_requests();





-- Update Trigger --
DROP SEQUENCE IF EXISTS pupdate_sequence;
CREATE SEQUENCE pupdate_sequence START WITH 51;

CREATE OR REPLACE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION product_updates()
RETURNS "trigger" AS
$BODY$
BEGIN
   
   NEW.updateNumber = nextval('pupdate_sequence');
   NEW.updatedOn = CURRENT_TIMESTAMP(0);
   
   RETURN new;

END;
$BODY$
LANGUAGE PLPGSQL VOLATILE;

DROP TRIGGER IF EXISTS product_update_table ON ProductUpdates;
CREATE TRIGGER product_update_table
        BEFORE INSERT ON ProductUpdates
        FOR EACH ROW
        EXECUTE PROCEDURE product_updates();



--  Order Trigger --
DROP SEQUENCE IF EXISTS porder_sequence;
CREATE SEQUENCE porder_sequence START WITH 550;

CREATE OR REPLACE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION product_orders()
RETURNS "trigger" AS
$BODY$
BEGIN

   -- Fill in serial and time data automatically
   NEW.orderNumber = nextval('porder_sequence');
   NEW.orderTime = CURRENT_TIMESTAMP(0);

   -- Take value out of product unit value
   UPDATE Product AS p
        SET numberOfUnits = numberOfUnits - NEW.unitsOrdered
        WHERE NEW.storeID = p.storeID
        AND NEW.productName = p.productName;

   RETURN new;

END;
$BODY$
LANGUAGE PLPGSQL VOLATILE;

DROP TRIGGER IF EXISTS product_order_table ON Orders;
CREATE TRIGGER product_order_table
        BEFORE INSERT ON Orders
        FOR EACH ROW
        EXECUTE PROCEDURE product_orders();
