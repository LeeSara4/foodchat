package foodchat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbutil.DBUtil;

public class ChatDao {
//	public int plusResolve() {
//		try (Connection conn = DBUtil.getConnection();
//				PreparedStatement stmt = conn.prepareStatement("UPDATE resolved SET count = count + 1 WHERE current_date();")) {
//			return stmt.executeUpdate();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
//	
//	public String getResolve() {
//		try (Connection conn = DBUtil.getConnection();
//			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM resolved WHERE date = current_date()")) {
//			try (ResultSet rs = stmt.executeQuery()) {
//				rs.next();
//				return rs.getString("count");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
//
//	public String getById(int i) {
//		try (Connection conn = DBUtil.getConnection();
//			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM food WHERE id = ?")) {
//			stmt.setInt(1, i);
//			try (ResultSet rs = stmt.executeQuery()) {
//				rs.next();
//				return rs.getString("name");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
}
