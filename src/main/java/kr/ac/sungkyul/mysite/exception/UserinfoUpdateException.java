package kr.ac.sungkyul.mysite.exception;

public class UserinfoUpdateException extends RuntimeException{

	public UserinfoUpdateException() {
		super("Exception form Updating User Info");
	}
	
	public UserinfoUpdateException(String message) {
		super(message);
	}

}
