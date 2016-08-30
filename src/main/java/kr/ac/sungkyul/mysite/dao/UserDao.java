package kr.ac.sungkyul.mysite.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.ac.sungkyul.mysite.vo.UserVo;

@Repository
public class UserDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public void update( UserVo vo ) {
		sqlSession.update("user.update", vo);
	}
	
	public void insert( UserVo vo ) {
		int count = sqlSession.insert("user.insert", vo);
		System.out.println( count );
	}
	
	public UserVo get( String email ) {
		UserVo vo = sqlSession.selectOne("user.getByEmail", email);
		return vo;
	}
	
	public UserVo get( Long userNo ){
		UserVo vo = sqlSession.selectOne("user.getByNo", userNo);
		
		//List<UserVo> list = sqlSession.selectOne("user.getList", userNo);
		
		return vo;
	}
	
	public UserVo get( String email, String password ){
		UserVo userVo = new UserVo();
		userVo.setEmail(email);
		userVo.setPassword(password);
		
		//만약에 파리미터로 넘겨야 할 매핑 클래스가 없는 경우
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("email", email);
//		map.put("password", password);
//		UserVo vo = sqlSession.selectOne("user.getByEmailAndPassword", map);	
		
		UserVo vo = sqlSession.selectOne("user.getByEmailAndPassword", userVo);	
		return vo;
	}
}