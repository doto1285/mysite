package kr.ac.sungkyul.mysite.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import kr.ac.sungkyul.mysite.vo.UserVo;

@Repository
public class UserDao {

	private Connection getConnection() throws SQLException {
		Connection conn = null ;
		try {
			Class.forName( "oracle.jdbc.driver.OracleDriver" );
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			conn = DriverManager.getConnection(url, "skudb", "skudb");
		} catch( ClassNotFoundException e ) {
			e.printStackTrace();
		} 
		
		return conn;
	}
	
	public void update( UserVo vo ) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = getConnection();
			
			Long no = vo.getNo();
			String name = vo.getName();
			String password = vo.getPassword();
			String gender = vo.getGender();
			boolean isPasswordEmpty = "".equals( password );
			
			String sql = null;
			if( isPasswordEmpty == true ) {
				sql = "update users set name = ?, gender = ? where no = ?";
			} else {
				sql = "update users set name = ?, password = ?, gender = ? where no = ?";
			}
			
			pstmt = conn.prepareStatement( sql );

			if( isPasswordEmpty == true ) {
				pstmt.setString( 1, name );
				pstmt.setString( 2, gender );
				pstmt.setLong( 3, no );
			} else {
				pstmt.setString( 1, name );
				pstmt.setString( 2, password );
				pstmt.setString( 3, gender );
				pstmt.setLong( 4, no );
			}
			
			pstmt.executeUpdate();
			
		} catch( SQLException e ) {
			e.printStackTrace();
		} finally {
			try {
				if( pstmt != null ) {
					pstmt.close();
				}
				if( conn != null ) {
					conn.close();
				}
			} catch( SQLException e ) {
				e.printStackTrace();
			}
		}
	}
	
	public UserVo get( Long userNo ){
		UserVo vo = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			conn = getConnection();
			
			String sql = "select no, name, gender from users where no = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong( 1, userNo );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				Long no = rs.getLong( 1 );
				String name = rs.getString( 2 );
				String gender = rs.getString( 3 );
				
				vo = new UserVo();
				vo.setNo(no);
				vo.setName(name);
				vo.setGender(gender);
			}
			
		} catch( SQLException e ) {
			e.printStackTrace();
		} finally {
			try{
				if( pstmt != null ) {
					pstmt.close();
				}
				if( conn != null ) {
					conn.close();
				}
			} catch( SQLException e ) {
				e.printStackTrace();
			}
		}
		
		return vo;
	}
	
	public UserVo get( String email, String password ){
		UserVo vo = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			conn = getConnection();
			
			String sql = "select no, name from users where email=? and password=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString( 1, email );
			pstmt.setString( 2, password );
			
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				Long no = rs.getLong( 1 );
				String name = rs.getString( 2 );
				
				vo = new UserVo();
				vo.setNo(no);
				vo.setName(name);
			}
			
		} catch( SQLException e ) {
			e.printStackTrace();
		} finally {
			try{
				if( pstmt != null ) {
					pstmt.close();
				}
				if( conn != null ) {
					conn.close();
				}
			} catch( SQLException e ) {
				e.printStackTrace();
			}
		}
		
		return vo;
	}
	
	public void insert( UserVo vo ) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try{
			conn = getConnection();
			
			String sql = "insert into users values(seq_users.nextval, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString( 1, vo.getName() );
			pstmt.setString( 2, vo.getEmail() );
			pstmt.setString( 3, vo.getPassword() );
			pstmt.setString( 4, vo.getGender() );
			
			pstmt.executeUpdate();
			
		} catch( SQLException e ) {
			e.printStackTrace();
		} finally {
			try{
				if( pstmt != null ) {
					pstmt.close();
				}
				if( conn != null ) {
					conn.close();
				}
			} catch( SQLException e ) {
				e.printStackTrace();
			}
		}
	}
}
