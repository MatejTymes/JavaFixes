package playground.chv;

import javafixes.object.changing.ChangingValue;
import javafixes.object.changing.MutableValue;

import static javafixes.object.changing.builder.MutableValueBuilder.mutableValue;
import static javafixes.object.changing.function.valueHandler.EachPotentialValueHandler.handleUsedValue;

public class ChVDemo {

    private class ConnectionDetails {
    }

    private class DbConnection {
        public DbTable getDbTable(Object itemId) {
            return null;
        }
    }

    private class DbTable {
        public int getRecordsCount() {
            return 0;
        }

        public void deleteRecord(String recordId) {
        }
    }

    private ConnectionDetails dbADetails() {
        return null;
    }

    private ConnectionDetails dbBDetails() {
        return null;
    }

    private DbConnection connectTo(ConnectionDetails connectionDetails) {
        return null;
    }

    private void releaseConnection(DbConnection connection) {
    }


    public void doSomething() {
        ConnectionDetails originalConnectionDetails = dbADetails();

        // mutable wrapper of connection details
        MutableValue<ConnectionDetails> connectionDetails = mutableValue(
                originalConnectionDetails
        );


        // database connection derived from connection details
        // each time connection details change this wrapper will create/refer to a new connection
        ChangingValue<DbConnection> dbConnection = connectionDetails.mapValueBuilder(
                        details -> connectTo(details)
                )
                // and use this to dispose old connection if new one will be created
                .withDisposeFunction(
                        connection -> releaseConnection(connection)
                )
                // and call this when value changes
                .withEachPotentialValueHandler(
                        handleUsedValue(value -> System.out.println("we have a new connection"))
                )
                .build();


        // simple mapping - fetching database table using current connection
        ChangingValue<DbTable> usersTable = dbConnection.mapValue(
                connection -> connection.getDbTable("users")
        );


        // this will be executed on initial database connection
        int recordCount = usersTable.mapCurrentValue(
                table -> table.getRecordsCount()
        );
        usersTable.forCurrentValue(
                table -> table.deleteRecord("agent Smith")
        );


        ConnectionDetails newConnectionDetails = dbBDetails();

        // update of connection details
        connectionDetails.updateValue(
                newConnectionDetails
        );


        // usersTable now refers to the new database connection and gets its records count instead
        int recordCount2 = usersTable.mapCurrentValue(
                table -> table.getRecordsCount()
        );
    }

}
