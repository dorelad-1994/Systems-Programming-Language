import atexit
import os
import sqlite3
import sys


# Data Transfer Objects:

class Vaccine:
    def __init__(self, id, date, supplier, quantity):
        self.id = id
        self.date = date
        self.supplier = supplier
        self.quantity = quantity


class Supplier:
    def __init__(self, id, name, logistic):
        self.id = id
        self.name = name
        self.logistic = logistic


class Clinic:
    def __init__(self, id, location, demand, logistic):
        self.id = id
        self.location = location
        self.demand = demand
        self.logistic = logistic


class Logistic:
    def __init__(self, id, name, count_sent, count_received):
        self.id = id
        self.name = name
        self.count_sent = count_sent
        self.count_received = count_received


# Data Access Objects:
# All of these are meant to be singletons
class _Vaccines:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, vaccine):
        self._conn.execute("""
               INSERT INTO vaccines (id, date, supplier, quantity) VALUES (?, ?, ?, ?)
           """, [vaccine.id, vaccine.date, vaccine.supplier, vaccine.quantity])

    def find_all(self):
        c = self._conn.cursor()
        all = c.execute("""
            SELECT * FROM vaccines ORDER BY date
        """).fetchall()

        return [Vaccine(*row) for row in all]

    def remove(self, vaccine):
        self._conn.execute("""
                      DELETE FROM vaccines WHERE id = ?
                  """, [vaccine.id])

    def update(self, vaccine_id, field, amount):
        update = "UPDATE vaccines SET {} = {} WHERE id = {}".format(field, amount, vaccine_id)
        self._conn.execute(update)

    def totalInventory(self):
        c = self._conn.cursor()
        c.execute('SELECT SUM (quantity) FROM vaccines')

        return c.fetchone()[0]

    def maxID(self):
        c = self._conn.cursor()
        c.execute('SELECT MAX (id) FROM vaccines')

        return c.fetchone()[0]


class _Suppliers:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, supplier):
        self._conn.execute("""
                INSERT INTO suppliers (id, name, logistic) VALUES (?, ?, ?)
        """, [supplier.id, supplier.name, supplier.logistic])

    def find(self, supplier_name):
        c = self._conn.cursor()
        c.execute("""
                SELECT * FROM suppliers WHERE name = ?
            """, [supplier_name])

        return Supplier(*c.fetchone())


class _Clinics:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, clinic):
        self._conn.execute("""
            INSERT INTO clinics (id, location, demand, logistic) VALUES (?, ?, ?, ?)
        """, [clinic.id, clinic.location, clinic.demand, clinic.logistic])

    def find(self, clinic_location):
        c = self._conn.cursor()
        c.execute("""
                        SELECT * FROM clinics WHERE location = ?
                    """, [clinic_location])

        return Clinic(*c.fetchone())

    def update(self, clinic_id, field, amount):
        update = "UPDATE clinics SET {} = {} WHERE id = {}".format(field, amount, clinic_id)
        self._conn.execute(update)

    def totalDemand(self):
        c = self._conn.cursor()
        c.execute('SELECT SUM (demand) FROM clinics')

        return c.fetchone()[0]


class _Logistics:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, logistic):
        self._conn.execute("""
            INSERT INTO logistics (id, name, count_sent, count_received) VALUES (?, ?, ?, ?)
        """, [logistic.id, logistic.name, logistic.count_sent, logistic.count_received])

    def find(self, logistic_id):
        c = self._conn.cursor()
        c.execute("""
                        SELECT * FROM logistics WHERE id = ?
                    """, [logistic_id])

        return Logistic(*c.fetchone())

    def update(self, logistic_id, field, amount):
        update = "UPDATE logistics SET {} = {} WHERE id = {}".format(field, amount, logistic_id)
        self._conn.execute(update)

    def totalCount(self, field):
        c = self._conn.cursor()
        c.execute('SELECT SUM ({}) FROM logistics'.format(field))

        return c.fetchone()[0]


# The Repository
class _Repository:
    def __init__(self):
        self._conn = sqlite3.connect('database.db')
        self.vaccines = _Vaccines(self._conn)
        self.suppliers = _Suppliers(self._conn)
        self.clinics = _Clinics(self._conn)
        self.logistics = _Logistics(self._conn)

    def _close(self):
        self._conn.commit()
        self._conn.close()

    def create_tables(self):
        self._conn.executescript("""
        CREATE TABLE IF NOT EXISTS `vaccines` (
	        `id`	     INTEGER,
	        `date`	      DATE       NOT NULL,
        	`supplier`	 INTEGER,
	        `quantity`	 INTEGER     NOT NULL,
        	FOREIGN KEY(`supplier`) REFERENCES `suppliers`(`id`),
        	PRIMARY KEY(`id`)
        );

        CREATE TABLE IF NOT EXISTS `suppliers` (
	        `id`	      INTEGER,
	        `name`	       STRING     NOT NULL,
	        `logistic`	  INTEGER,
	        FOREIGN KEY(`logistic`) REFERENCES `logistics`(`id`),
	        PRIMARY KEY(`id`)
        );

        CREATE TABLE IF NOT EXISTS `clinics` (
	        `id`	    INTEGER,
    	    `location`	STRING      NOT NULL,
    	    `demand`	INTEGER     NOT NULL,
    	    `logistic`	INTEGER,
    	    PRIMARY KEY(`id`),
    	    FOREIGN KEY(`logistic`) REFERENCES `logistics`(`id`)
        );
    
        CREATE TABLE IF NOT EXISTS `logistics` (
	        `id`	            INTEGER,
	        `name`	             TEXT       NOT NULL,
	        `count_sent`	    INTEGER     NOT NULL,
	        `count_received`	INTEGER     NOT NULL,
	        PRIMARY KEY(`id`)
        );
    """)


    def loadData(self, path):
        with open(path) as file:
            file = file.readlines()
            line = file[0].split(',')
            numOfVaccines = int(line[0])
            numOfSuppliers = int(line[1])
            numOfClinics = int(line[2])
            numOfLogistics = int(line[3])
            start = 1
            finish = numOfVaccines + 1
            for i in range(start, finish):
                line = file[i].split(',')
                self.vaccines.insert(Vaccine(*line))
            start = finish
            finish = finish + numOfSuppliers
            for i in range(start, finish):
                line = file[i].split(',')
                self.suppliers.insert(Supplier(*line))
            start = finish
            finish = finish + numOfClinics
            for i in range(start, finish):
                line = file[i].split(',')
                self.clinics.insert(Clinic(*line))
            start = finish
            finish = finish + numOfLogistics
            for i in range(start, finish):
                line = file[i].split(',')
                self.logistics.insert(Logistic(*line))

    def orders(self, path):
        with open(path) as file:
            file = file.readlines()
            for line in file:
                line = line.split(',')
                if len(line) == 2:
                    self.sendShipment(line)
                else:
                    self.receiveShipment(line)

    def receiveShipment(self, line):
        supplierName = line[0]
        supplierID = self.suppliers.find(supplierName).id
        quantity = int(line[1])
        date = line[2]

        self.vaccines.insert(Vaccine(self.vaccines.maxID() + 1, date, supplierID, quantity))

        logisticID = self.suppliers.find(supplierName).logistic
        amount = self.logistics.find(logisticID).count_received + quantity

        self.logistics.update(logisticID, 'count_received', amount)
        self.updateOutput()

    def sendShipment(self, line):
        location = line[0]
        demand = int(line[1])
        clinic = self.clinics.find(location)

        # update demand in clinic
        amount = clinic.demand - demand
        self.clinics.update(clinic.id, 'demand', amount)

        # remove vaccines from vaccines(if quantity = 0 remove entry)
        vaccines = self.vaccines.find_all()
        counter = demand
        while counter > 0:
            vaccine = vaccines[0]
            vaccines = vaccines[1:]
            if vaccine.quantity <= counter:
                counter = counter - vaccine.quantity
                self.vaccines.remove(vaccine)
            else:
                self.vaccines.update(vaccine.id, 'quantity', vaccine.quantity - counter)
                counter = 0

        # update count_sent in logistic of the clinic
        logID = clinic.logistic
        countSent = self.logistics.find(logID).count_sent
        self.logistics.update(logID, 'count_sent', countSent + demand)
        self.updateOutput()

    def updateOutput(self):
        output = open(sys.argv[3], 'a+')
        line = str(self.vaccines.totalInventory()) + ',' + str(self.clinics.totalDemand()) + ',' + str(
            self.logistics.totalCount('count_received')) + ',' + str(self.logistics.totalCount('count_sent')) + '\n'
        output.write(line)
        output.close()


if __name__ == "__main__":
    # the repository singleton
    if os.path.exists(sys.argv[3]):
        os.remove(sys.argv[3])
    repo = _Repository()
    atexit.register(repo._close)
    repo.create_tables()
    repo.loadData(sys.argv[1])
    repo.orders(sys.argv[2])
