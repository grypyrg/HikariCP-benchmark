/*
 * Copyright (C) 2014 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaxxer.hikari.benchmark;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;
import java.sql.ResultSet;


import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class TrxBench extends BenchBase
{
    @Benchmark
    public Statement cycleStatement(ConnectionState state) throws SQLException
    {
            Connection con = DS.getConnection();
	    if ( con.getTransactionIsolation() != Connection.TRANSACTION_READ_COMMITTED ) {
		    con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
	    }
	    con.setAutoCommit(false);
	    PreparedStatement stmt = con.prepareStatement("select * from employees where emp_no = ?");
	    stmt.setInt(1, 20000);
	    ResultSet rs = stmt.executeQuery();
	    stmt.close();
	    rs.close();
	    con.commit();
            con.close();
	    return stmt;
    }

    @State(Scope.Thread)
    public static class ConnectionState extends Blackhole
    {
        Connection connection;

        @Setup(Level.Iteration)
        public void setup() throws SQLException
        {
            //connection = DS.getConnection();
        }

        @TearDown(Level.Iteration)
        public void teardown() throws SQLException
        {
            //connection.close();
        }
    }
}
