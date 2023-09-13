package com.example.cardscollection;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.util.Iterator;

public class DbManager
{
    private class dbIterator implements Iterator<dbItem>
    {
        ResultSet rs = null;
        private dbIterator(ResultSet rs)
        {
            this.rs = rs;
        }

        @Override
        public boolean hasNext()
        {
            try
            {
                return rs.next();
            } catch(SQLException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public dbItem next()
        {
            try
            {
                return new dbItem(rs.getString("filename"), rs.getString("cardname"), rs.getString("description"));
            } catch(SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    public static class dbItem
    {
        //ignoring incapsulation
        public String filename = "";
        public String cardname = "";
        public String description = "";

        public dbItem(String filename, String cardname, String description)
        {
            this.filename = filename;
            this.cardname = cardname;
            this.description = description;
        }

        @Override
        public String toString()
        {
            String[] cols = {"File name", "Card name", "Description"};
            return ("\n%-"+Math.max(filename.length(), cols[0].length())+"s|%-"+
                    Math.max(cardname.length(), cols[1].length())+"s|%s\n").formatted(cols[0], cols[1], cols[2])+
                    ("%-"+Math.max(filename.length(), cols[0].length())+"s|%-"
                    +Math.max(cardname.length(), cols[1].length())+"s|%s\n").formatted(filename, cardname, description);
        }
    }
    private static DbManager dbManager = null;
    private final String dbTable = "collection";
    private final String cardDbPath = "jdbc:sqlite:./src/database/cards.db";
    private Connection conn = null;
    public static DbManager getManager()
    {
        if(dbManager == null)
            dbManager = new DbManager();
        return dbManager;
    }
    private DbManager()
    {
        //create db table if it does not exist
        String query = "create table if not exists %s (filename text PRIMARY key not NULL, cardname text, description text)".formatted(dbTable);
        try
        {
            Statement s = createConnectionStatement();
            s.executeUpdate(query);
            s.close();
            closeConnection();
        }catch(SQLException e){ e.printStackTrace();}

    }

    private Statement createConnectionStatement()
    {
        Statement st = null;
        try
        {
            conn = DriverManager.getConnection(cardDbPath);
            st = conn.createStatement();
        }
        catch(SQLException e){throw new RuntimeException(e);}

        return st;
    }

    private void closeConnection()
    {
        try
        {
            conn.close();
        }
        catch(SQLException e){throw new RuntimeException(e);}
    }

    public void update(dbItem item)
    {
        String query = ("insert into %s (filename, cardname, description) values ('%s', '%s', '%s') " +
                "on CONFLICT(filename) do UPDATE set cardname=excluded.cardname, description=excluded.description;")
        .formatted(dbTable, item.filename, item.cardname, item.description);

        try
        {
            Statement s = createConnectionStatement();
            s.executeUpdate(query);
            s.close();
            closeConnection();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void delete(String filename)
    {
        String query = "delete from %s where filename='%s'"
                        .formatted(dbTable, filename);
        try
        {
            Statement s = createConnectionStatement();
            s.executeUpdate(query);
            s.close();
            closeConnection();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<dbItem> dbExpose()
    {
        String query = "SELECT * FROM %S"
                .formatted(dbTable);
        List<dbItem> res = new ArrayList<>();
        try
        {
            Statement s = createConnectionStatement();
            dbIterator dbiter = new dbIterator(s.executeQuery(query));
            while(dbiter.hasNext())
            {
                res.add(dbiter.next());
            }
            s.close();
            closeConnection();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
        return res;
    }


}
