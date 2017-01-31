import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.exceptions.*;
import java.io.UnsupportedEncodingException;


public class TestJwt {

	public static void main(String args[]) {
		try {
    			String token = JWT.create()
        		.withIssuer("auth0")
        		.sign(Algorithm.HMAC256("secret"));
			System.out.println("Token " + token);

		} catch (JWTCreationException|UnsupportedEncodingException exception){
    			//Invalid Signing configuration / Couldn't convert Claims.
		}
	}
}
