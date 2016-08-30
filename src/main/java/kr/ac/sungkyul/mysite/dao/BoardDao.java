package kr.ac.sungkyul.mysite.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.ac.sungkyul.mysite.vo.BoardVo;

public class BoardDao {
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
	
	public void delete( Long userNo, Long no ) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = getConnection();
			
			String sql = "delete from board where no = ? and user_no = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, no );
			pstmt.setLong(2, userNo );
			
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
	
	public void update( BoardVo vo ) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			
			String sql =
				"update board set title = ?, content= ? where no = ? and user_no = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, vo.getTitle() );
			pstmt.setString(2, vo.getContent() );
			pstmt.setLong(3, vo.getNo() );
			pstmt.setLong(4, vo.getUserNo() );
			
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
	
	public void updateOrderNo( Long groupNo, Long orderNo ) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = getConnection();
			String sql = "update board set order_no = order_no + 1 where group_no = ? and order_no >= ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, groupNo);
			pstmt.setLong(2, orderNo);
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
	
	public void updateViewCount( Long no ) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			
			String sql = "update board set view_count = view_count + 1 where no = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, no);
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
	
	public BoardVo get( Long no ) {
		BoardVo vo = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			
			String sql = "select no, title, content, user_no, group_no, order_no, depth from board where no = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, no);
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				vo = new BoardVo();
				vo.setNo( rs.getLong(1) );
				vo.setTitle( rs.getString(2) );
				vo.setContent( rs.getString(3) );
				vo.setUserNo( rs.getLong(4) );
				vo.setGroupNo( rs.getLong(5) );
				vo.setOrderNo( rs.getLong(6) );
				vo.setDepth( rs.getInt(7) );
			}
			
		} catch( SQLException e ) {
			e.printStackTrace();
		} finally {
			try {
				if( rs != null ) {
					rs.close();
				}
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
	
	public int getTotalCount( String keyword ){
		
		int totalCount = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			
			String sql = ( null == keyword || "".equals(keyword) ) ? 
				"select count(*) from board" :
				"select count(*) from board where title like ? or content like ?";
			pstmt = conn.prepareStatement(sql);
			
			if( null != keyword && "".equals(keyword) == false ) {
				pstmt.setString( 1, "%"+keyword+ "%" );
				pstmt.setString( 2, "%"+keyword+ "%" );
			}
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				totalCount = rs.getInt( 1 );
			}
			
		} catch( SQLException e ) {
			e.printStackTrace();
		} finally {
			try {
				if( rs != null ) {
					rs.close();
				}
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
		
		return totalCount;		
	}
	
	public List<BoardVo> getList( int page, int pagesize, String keyword ) {
		List<BoardVo> list = new ArrayList<BoardVo>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			
			String sql = ( null == keyword || "".equals(keyword) ) ? 
				"SELECT * " +
				"  FROM (SELECT c.*, rownum rn" +
				"          FROM (  SELECT a.no, a.title, b.name, a.user_no, a.view_count, to_char(reg_date, 'yyyy-mm-dd pm hh:mi:ss'), depth" +
				"                    FROM board a," +
				"                         users b" +
				"                   WHERE a.user_no = b.no" +
				"                ORDER BY group_no DESC, order_no ASC) c )" +
				" WHERE ?  <= rn" +
				"   AND rn <= ?" :
					
				"SELECT * " +
					"  FROM (SELECT c.*, rownum rn" +
					"          FROM (  SELECT a.no, a.title, b.name, a.user_no, a.view_count, to_char(reg_date, 'yyyy-mm-dd pm hh:mi:ss'), depth" +
					"                    FROM board a," +
					"                         users b" +
					"                   WHERE a.user_no = b.no" +
					"                     AND (title like ? OR content like ?)" +
					"                ORDER BY group_no DESC, order_no ASC) c )" +
					" WHERE ?  <= rn" +
					"   AND rn <= ?" ;
			
			pstmt = conn.prepareStatement(sql);

			if( null == keyword || "".equals(keyword) ){
				pstmt.setInt(1, (page-1)*pagesize + 1);
				pstmt.setInt(2, page*pagesize);
			} else {
				pstmt.setString( 1, "%" + keyword + "%" );
				pstmt.setString( 2, "%" + keyword + "%" );
				pstmt.setInt(3, (page-1)*pagesize + 1);
				pstmt.setInt(4, page*pagesize);
			}
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				Long no = rs.getLong( 1 );
				String title = rs.getString( 2 );
				String userName = rs.getString( 3 );
				Long userNo = rs.getLong( 4 );
				Integer viewCount = rs.getInt( 5 );
				String regDate = rs.getString( 6 );
				Integer depth = rs.getInt( 7 );

				BoardVo vo = new BoardVo();
				vo.setNo( no );
				vo.setTitle( title );
				vo.setUserName( userName );
				vo.setUserNo( userNo );
				vo.setViewCount( viewCount );
				vo.setRegDate( regDate );
				vo.setDepth( depth );
				
				list.add( vo );
			}
			
			return list;
		} catch( SQLException ex ) {
			System.out.println( "error: " + ex);
			return list;
		} finally {
			try{
				if( rs != null ) {
					rs.close();
				}
				if( pstmt != null ) {
					pstmt.close();
				}
				if( conn != null ) {
					conn.close();
				}
			}catch( SQLException ex ) {
				ex.printStackTrace();
			}
		}
	}
	
	public void insert( BoardVo vo ){
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try{
			conn = getConnection();
			
			String sql = ( vo.getGroupNo() == null ) ? 
				"insert into board values(seq_board.nextval, ?, ?, sysdate, 0, nvl((select max(group_no) from board), 0) + 1, 1, 1, ?)":
				"insert into board values(seq_board.nextval, ?, ?, sysdate, 0, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			
			if( vo.getGroupNo() == null ) { 
				pstmt.setString( 1, vo.getTitle() );
				pstmt.setString( 2, vo.getContent() );
				pstmt.setLong( 3, vo.getUserNo() );
			} else {
				pstmt.setString( 1, vo.getTitle() );
				pstmt.setString( 2, vo.getContent() );
				pstmt.setLong( 3, vo.getGroupNo() );
				pstmt.setLong( 4, vo.getOrderNo() );
				pstmt.setInt( 5, vo.getDepth() );
				pstmt.setLong( 6, vo.getUserNo() );
			}
			
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
