CREATE TABLE supplier(
                         supplier_id VARCHAR(10)PRIMARY KEY ,
                         contact_number VARCHAR(25),
                         name VARCHAR(25),
                         description VARCHAR(25)
);

CREATE TABLE raw_material(
                             rm_code VARCHAR(10) PRIMARY KEY ,
                             description VARCHAR(25),
                             qty_on_hand VARCHAR(10)
);

CREATE TABLE supplier_detail(
                                qty VARCHAR(10),
                                date Date,
                                rm_code VARCHAR(10),
                                supplier_id VARCHAR(10),
                                FOREIGN KEY(rm_code) REFERENCES raw_material(rm_code),
                                FOREIGN KEY (supplier_id)REFERENCES supplier(supplier_id)
);

CREATE TABLE item(
                     item_code VARCHAR(10) PRIMARY KEY ,
                     qty_on_hand VARCHAR(10),
                     unit_price DOUBLE(10,2),
    description VARCHAR(30)
);

CREATE TABLE material_detail(
                                qty VARCHAR(10),
                                current_unit_price DOUBLE (10,2),
    item_code VARCHAR(10),
    rm_code VARCHAR(10),
    FOREIGN KEY (item_code) REFERENCES item (item_code),
    FOREIGN KEY (rm_code) REFERENCES raw_material(rm_code)
);

CREATE TABLE user(
                     user_id VARCHAR(10) PRIMARY KEY ,
                     user_name VARCHAR(25),
                     password VARCHAR(25)
);

CREATE TABLE employee(
                         employee_id VARCHAR(10)PRIMARY KEY ,
                         name VARCHAR(25),
                         address VARCHAR(25),
                         amount_per_hour DECIMAL(10,2),
                         contact_number VARCHAR(15),
                         work_time INT(25),
                         user_id  VARCHAR(10),
                         FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE attendance(
                           attendance_id VARCHAR(10) PRIMARY KEY ,
                           employee_id VARCHAR(10),
                           date DATE ,
                           entrance_time TIME,
                           exits_time TIME,
                           FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

CREATE TABLE extra_expenses(
                               expenses_id VARCHAR(10)PRIMARY KEY ,
                               date DATE,
                               description VARCHAR(30),
                               amount DOUBLE(10,2),
    user_id VARCHAR(10),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE customer(
                         customer_id VARCHAR(10) PRIMARY KEY ,
                         name VARCHAR(25),
                         address VARCHAR(25),
                         contact_Number VARCHAR(15),
                         user_id  VARCHAR(10),
                         FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE orders(
                       order_id VARCHAR(10) PRIMARY KEY ,
                       date DATE,
                       customer_id VARCHAR(10) ,
                       FOREIGN KEY (customer_id)  REFERENCES customer(customer_id)
);

CREATE TABLE order_detail(
                             qty INT(10),
                             current_unit_price DOUBLE(10,2),
    order_id VARCHAR(10),
    item_code VARCHAR(10) ,
    FOREIGN KEY (order_id)  REFERENCES orders (order_id),
    FOREIGN KEY (item_code) REFERENCES item (item_code)
);