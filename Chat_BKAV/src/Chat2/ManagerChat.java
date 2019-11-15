package Chat2;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerException;

public class ManagerChat {
	Statement statement;
    PreparedStatement preStatement;
    Connection conn;
    ResultSet rs;

    public ManagerChat(Connection conn) {
        this.conn = conn;
        
    }

    public void insert(HistoryChat c) throws SQLServerException{
        String sql = "INSERT DBO.HISTORYCHAT1 (userSend,userReceive,contents,time) VALUES(?,?,?,?)";
        try {
            this.preStatement = this.conn.prepareStatement(sql);
            //this.preStatement.setInt(1, c.getId());
            this.preStatement.setString(1, c.getUserSend());
            this.preStatement.setString(2, c.getUserReceive());
            this.preStatement.setString(3, c.getContent());
            this.preStatement.setString(4, c.getTimeChat());
            this.preStatement.executeUpdate();
        } catch (SQLException e1) {
            e1.printStackTrace();

        }
        
    }
    public List<HistoryChat> getListHC() {
        List<HistoryChat> listHC = new ArrayList<>();
        try {
            String sql = "SELECT * FROM dbo.HistoryChat;";
            rs = ((java.sql.Statement) statement).executeQuery(sql);
            while (rs.next()) {
                HistoryChat h = new HistoryChat(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)
                );

                listHC.add(h);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return listHC;
    }
    public void displayChat(List<HistoryChat> listHC,String userSend, String userReceive){
        for(HistoryChat h:listHC){
            if(userSend.equals(h.getUserSend())&&userReceive.equals(h.getUserReceive())){
                System.out.println(userSend+": "+h.getContent());
                
            }
            if(userReceive.equals(h.getUserSend())&&userSend.equals(h.getUserReceive())){
                System.out.println(userReceive+": "+h.getContent());
                
            }
        }
    }
}
