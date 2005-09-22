package tracer;

import java.io.FileReader;
import java.io.StreamTokenizer;
import java.io.IOException;

/**
 * This class knows how to read scene files
 */
public class Parser {
	
	private String filename;
	
	private StreamTokenizer tokenizer;
	public String errorString() {
		return filename + "(" + tokenizer.lineno() + "): ";	
	}
	public String tokenWasUnexpected() throws IOException {
		String message = errorString();
		message = message + "`";
		if( tokenizer.ttype == StreamTokenizer.TT_WORD ) {
			message = message + tokenizer.sval;
		} else if( tokenizer.ttype > 0 ) {
			message = message + (char)tokenizer.ttype;
		} else if( tokenizer.ttype == StreamTokenizer.TT_NUMBER ) {
			message = message + tokenizer.nval;
		}
		message = message + "' unexpected.";
		tokenizer.nextToken();
		return message;
	}
	
	public Parser( String filename ) {
		try {
		
			FileReader reader = new FileReader( filename );
			tokenizer = new StreamTokenizer( reader );
			tokenizer.wordChars( '_', '_' );
			tokenizer.slashSlashComments( true );
			tokenizer.slashStarComments( true );
			tokenizer.lowerCaseMode( true );
			tokenizer.eolIsSignificant( false );
			this.filename = filename;
			
		} catch( IOException e ) {
			System.out.println( "Failed to open file " + filename );
		}			
	}
	
	public boolean endOfFile() throws IOException {
		if( tokenizer.nextToken() == StreamTokenizer.TT_EOF ) {
			return true;
		} else {
			tokenizer.pushBack();
			return false;
		}
	}
	
	public boolean tryKeyword( String key ) throws IOException {
		int token = tokenizer.nextToken();
		if( token == StreamTokenizer.TT_WORD ) {
			// a multi-character token
			if( key.equals( tokenizer.sval ) ) {
				return true;
			} else {
				tokenizer.pushBack();
				return false;
			}
		} else if( token > 0 ) {
			// a one-character token
			if( key.length() == 1 && key.charAt(0) ==  (char)token ) {
				return true;
			} else {
				tokenizer.pushBack();
				return false;	
			}
		} else {
			String message = errorString() + "Expected keyword, found ";
			if( token == StreamTokenizer.TT_NUMBER ) {
				message = message + "a number.";
			} else if( token == StreamTokenizer.TT_EOF ) {
				message = message + " end of file.";	
			} else if( token == StreamTokenizer.TT_EOL ) {
				message = message + " end of line.";	
			}
			System.out.println( message );
			return false;
		}
	}
	public boolean parseKeyword( String key ) throws IOException {
		int token = tokenizer.nextToken();
		if( token == StreamTokenizer.TT_WORD && key.equals(tokenizer.sval) ) {
			// multi-character token
			return true;
		} else if( token > 0 && key.length()==1 && key.charAt(0)==(char)token ) {
			// single-character token
			return true;
		} else {
			String message = errorString() + "Expected keyword `" + key + "', but found `" + tokenizer.sval + "' while state is " + tokenizer.toString();
			System.out.println( message );
			return false;
		}
	}
	
	public float parseFloat() throws IOException {
		int token = tokenizer.nextToken();
		if( token == StreamTokenizer.TT_NUMBER ) {
			return (float)tokenizer.nval;
		} else {
			System.out.println( errorString() + "Expected floating point constant." );
			return 0;
		}
	}
	
	
	
}