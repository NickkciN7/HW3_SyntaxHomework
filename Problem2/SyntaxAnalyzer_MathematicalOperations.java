
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class SyntaxAnalyzer_MathematicalOperations {
	static LexicalAnalyzer lexAnalyzer;
	
	public static void main(String[] args) {
		lexAnalyzer = new LexicalAnalyzer("TEST_Problem2.txt");
		Expression();
		lexAnalyzer.closeFile();
	}
	
	public static void Expression() {
		System.out.println("Enter <Expression>");
		
		Term();
		//while nextToken is + or -
		while(lexAnalyzer.nextToken == lexAnalyzer.ADD_OP || lexAnalyzer.nextToken == lexAnalyzer.SUB_OP ) {
			lexAnalyzer.lex();
			Term();
		}
		
		System.out.println("Exit <Expression>");
	}
	
	public static void Term() {
		System.out.println("Enter <Term>");
		
		Factor();
		//while nextToken is * or / or %
		while(lexAnalyzer.nextToken == lexAnalyzer.MULT_OP || 
			  lexAnalyzer.nextToken == lexAnalyzer.DIV_OP || 
			  lexAnalyzer.nextToken == lexAnalyzer.MOD_OP ) 
		{
			
			lexAnalyzer.lex();
			Factor();
		}
		
		System.out.println("Exit <Term>");
	}
	
	public static void Factor() {
		System.out.println("Enter <Factor>");
		
		if(lexAnalyzer.nextToken == lexAnalyzer.IDENT || 
		   lexAnalyzer.nextToken == lexAnalyzer.INT_LIT || 
		   lexAnalyzer.nextToken == lexAnalyzer.FLOAT_LIT) 
		{
			lexAnalyzer.lex();
		}
		else if(lexAnalyzer.nextToken == lexAnalyzer.LEFT_PAREN){
			lexAnalyzer.lex();
			Expression();
			if(lexAnalyzer.nextToken == lexAnalyzer.RIGHT_PAREN) {
				lexAnalyzer.lex();
			}
			else {
				Error();
			}
		}
		else {
			Error();
		}
		
		System.out.println("Exit <Factor>");
	}
	
	public static void Error() {
		System.out.println("\nERROR, NOT A VALID MATHEMATICAL EXPRESSION\n");
	}
}




class LexicalAnalyzer {
	/* Global declarations */
	/* Variables */
	static int charClass;
	static char[] lexeme = new char[100];//100 size
	static char nextChar;
	static int lexLen;
	static int token;
	static int nextToken;
	static FileInputStream fin;
	static int i;



	/* Character classes */
	final static int LETTER_UNDSCOR = 0;
	final static int LETTER = 1;
	final static int DIGIT_PER = 2;
	final static int UNKNOWN = 99;

	/* Token codes */
	final static int FLOAT_LIT = 9;
	final static int INT_LIT = 10;
	final static int IDENT = 11;
	final static int ASSIGN_OP = 20;
	final static int EQUALITY_OP = 8;
	final static int ADD_OP = 21;
	final static int SUB_OP = 22;
	final static int MULT_OP = 23;
	final static int DIV_OP = 24;
	final static int MOD_OP = 7;
	final static int LEFT_PAREN = 25;
	final static int RIGHT_PAREN = 26;
	final static int UNDERSCORE = 27;
	final static int CANT_DEFINE = 28;
		//reserved words
	final static int FOR_CODE = 30;
	final static int IF_CODE = 31;
	final static int ELSE_CODE = 32;
	final static int WHILE_CODE = 33;
	final static int DO_CODE = 34;
	final static int INT_CODE = 35;
	final static int FLOAT_CODE = 36;
	final static int SWITCH_CODE = 37;
	
	final static int EOF = 100;
	
	//regex for Integer
	final static String IntegerSuffix = "([uU][lL]?|[uU](ll|LL)|[uU][iI]64|[lL][uU]?|(ll|LL)[uU]?|[iI]64)";
	final static String DecimalConstant = "0|[1-9][0-9]*"+IntegerSuffix+"?";
	final static String OctalConstant = "0[0-7]+"+IntegerSuffix+"?";
	final static String HexadecimalConstant = "0[xX][0-9a-fA-f]+"+IntegerSuffix+"?";
	
	///was going to use to check if next character was special symbol and was going to make it so only special 
	//symbols follow a ident,float, or int, otherwise invalid token. but didn't end up doing that. 
	//so now it just generates new token for letters/numbers after without making error
	//for example 123.2abc will give float then identifier and not error
	//static ArrayList<Character> symbols = new ArrayList<Character>(); 
	
	//use when printing out what the token is rather than just printing that integer value for the token
	static HashMap<Integer, String> TokenMap = new HashMap<Integer, String>();
	//used to find token code of reserved word given a string equaling the reserved word
	static HashMap<String, Integer> ReservedWordMap = new HashMap<String, Integer>();

    public LexicalAnalyzer(String fileName) {
		TokenMap.put(FLOAT_LIT, "FLOAT_LIT");
		TokenMap.put(INT_LIT, "INT_LIT");
		TokenMap.put(IDENT, "IDENT");
		TokenMap.put(ASSIGN_OP, "ASSIGN_OP");
		TokenMap.put(EQUALITY_OP, "EQUALITY_OP");
		TokenMap.put(ADD_OP, "ADD_OP");
		TokenMap.put(SUB_OP, "SUB_OP");
		TokenMap.put(MULT_OP, "MULT_OP");
		TokenMap.put(DIV_OP, "DIV_OP");
		TokenMap.put(MOD_OP, "MOD_OP");
		TokenMap.put(LEFT_PAREN, "LEFT_PAREN");
		TokenMap.put(RIGHT_PAREN, "RIGHT_PAREN");
		TokenMap.put(UNDERSCORE, "UNDERSCORE");
		TokenMap.put(CANT_DEFINE, "CANT_DEFINE");
		TokenMap.put(EOF, "EOF");
		//reserved words
		TokenMap.put(FOR_CODE, "FOR_CODE");
		TokenMap.put(IF_CODE, "IF_CODE");
		TokenMap.put(ELSE_CODE, "ELSE_CODE");
		TokenMap.put(WHILE_CODE, "WHILE_CODE");
		TokenMap.put(DO_CODE, "DO_CODE");
		TokenMap.put(INT_CODE, "INT_CODE");
		TokenMap.put(FLOAT_CODE, "FLOAT_CODE");
		TokenMap.put(SWITCH_CODE, "SWITCH_CODE");
	
		
		ReservedWordMap.put("for", FOR_CODE);
		ReservedWordMap.put("if", IF_CODE);
		ReservedWordMap.put("else", ELSE_CODE);
		ReservedWordMap.put("while", WHILE_CODE);
		ReservedWordMap.put("do", DO_CODE);
		ReservedWordMap.put("int", INT_CODE);
		ReservedWordMap.put("float", FLOAT_CODE);
		ReservedWordMap.put("switch", SWITCH_CODE);
	
		
		try {
			//fin = new FileInputStream("TEST_Problem1.txt"); //txt file needs to be in outer workspace folder, not in src with Java file
			fin = new FileInputStream(fileName);
			//System.out.println("File found");
		} catch(FileNotFoundException exc){
			System.out.println("File not found");
			return;
		}
		
		
		getChar();
		lex();

		
	}
    static void closeFile() {
    	try {
			fin.close();
		} catch(IOException exc){
			System.out.println("Error closing file.");
		}
    }

	/* lookup - a function to look up operators and
	parentheses and return the token */
	static int lookup(char ch) {
		 switch (ch) {
			 case '(':
				 addChar();
				 nextToken = LEFT_PAREN;
				 break;
			 case ')':
				 addChar();
				 nextToken = RIGHT_PAREN;
				 break;
			 case '+':
				 addChar();
				 nextToken = ADD_OP;
				 break;
			 case '-':
				 addChar();
				 nextToken = SUB_OP;
				 break;
			 case '*':
				 addChar();
				 nextToken = MULT_OP;
				 break;
			 case '/':
				 addChar();
				 nextToken = DIV_OP;
				 break;
			 case '%':
				 addChar();
				 nextToken = MOD_OP;
				 break;
			 case '=':
				 addChar();
				 nextToken = ASSIGN_OP;
				 break;
//			 case '_':
//				 addChar();
//				 nextToken = UNDERSCORE;
//				 break;
			 default:
				 addChar();
				 nextToken = EOF;
				 break;
		 }
		 return nextToken;
	}
	

	
	
	/******************************************************/
	/* addChar - a function to add nextChar to lexeme */
	static void addChar() {
		if (lexLen <= 98) {
			//System.out.println((int)nextChar);
			//if((int)nextChar < 128) { 
				lexeme[lexLen++] = nextChar;
			//}
			lexeme[lexLen] = '\0';
		} else {
			System.out.println("Error - lexeme is too long \n");
		}

	}

	
	/******************************************************/
	/* getChar - a function to get the next character of
	input and determine its character class */
	static void getChar(){
//		 if ((nextChar = getc(in_fp)) != EOF) {
//			 if (isalpha(nextChar))
//			 	charClass = LETTER;
//			 else if (isdigit(nextChar))
//			 	charClass = DIGIT;
//			 else
//			 	charClass = UNKNOWN;
//		 } else
//		 	charClass = EOF;
		
		
		//read character
		try {
			i = fin.read();
		} catch(IOException exc) {
			System.out.println(exc);
			//return;
		}
		
		nextChar = (char)i;
		//if((int)nextChar > 127) { //high nextChar values were actually just character casting -1 (or EOF)
			//System.out.println("i: " + i);
		//}
		if (i != -1) {
			 if (Character.isLetter(nextChar) | nextChar == '_') {
			 	charClass = LETTER_UNDSCOR;
			 }
			 else if (Character.isDigit(nextChar) | nextChar == '.') { //or == '.'
			 	charClass = DIGIT_PER;
			 } 
			 else {
			 	charClass = UNKNOWN;
			 }
		 } else {
		 	charClass = EOF;
		 }
	}


	/******************************************************/
	/* getNonBlank - a function to call getChar until it
	returns a non-whitespace character */
	static void getNonBlank() {
		//while (isspace(nextChar))
		while (Character.isWhitespace(nextChar)) {
			getChar();
		}
	}

	/******************************************************/
	/* lex - a simple lexical analyzer for arithmetic
	expressions */
	static int lex() {
		 String lexemeStr; 
		 lexLen = 0;
		 getNonBlank();
		 switch (charClass) {
			/* Identifiers */
			 case LETTER_UNDSCOR:
				 //any line with comment "this line" means code relates to making identifier work with underscore
				 boolean firstIsUnderscore = false; //this line
				 if(nextChar == '_') firstIsUnderscore = true; //this line
				 
				 addChar();
				 getChar();
				 
				 boolean anyAdded = false; //this line
				 while (charClass == LETTER_UNDSCOR || (charClass == DIGIT_PER & nextChar != '.') ) {
					 anyAdded = true; //this line
					 addChar();
					 getChar();
				 }
				 if(firstIsUnderscore && !anyAdded) { //this line
					 nextToken = UNDERSCORE; //this line
				 } else {
					 nextToken = IDENT;
				 }
				 break;
			/* Integer literals */
			 case DIGIT_PER:
				 boolean firstIsPeriod = false; //this line
				 if(nextChar == '.') firstIsPeriod = true; //this line
				 boolean canBeFloating = true; //used after going through integer part
				 addChar();
				 getChar();
				 //System.out.println(firstIsPeriod);
				 if(firstIsPeriod) {
					 /*while(matches regex pattern for float){
					  * 	addChar()
					  * 	getChar()
					  * }
					  */
				 } else {
					 
					 
					 lexemeStr = new String(lexeme).split("\0")[0];
					 //System.out.println("lex str: "+lexemeStr);
					 boolean decConst = Pattern.matches(DecimalConstant, lexemeStr);
					 boolean octConst = Pattern.matches(OctalConstant, lexemeStr);
					 boolean hexConst = Pattern.matches(HexadecimalConstant, lexemeStr);
					 
					 //needed these because while loop terminates when it shouldnt without
					 //for example hexadecimal 0x2a. 0x2a matches the regex, but because while loop goes 
					 //character at a time, it stops at 0x since 0x doesn't match regex. So I need to check
					 //if next character appended will match regex, for example 0x2 would match and overcome this issue
					 
					 boolean decConstNext = false;
					 boolean octConstNext = false;
					 boolean hexConstNext = false;
					 
					//I noticed a really high unicode number was being added near end of file because '?' was being added to lexeme. 
					//turns out to be character casting -1(or EOF) and adding that to lexeme 
					//made sure below that charClass EOF cant enter while loop
					 
					 //added that Pattern.matches... in while loop conditions to prevent anything like a period from breaking the potential floating point number apart
					 while( ( (decConst | octConst | hexConst) | (decConstNext | octConstNext | hexConstNext) ) & (charClass != EOF) & Pattern.matches("[uUlLiIxX0-9a-fA-F]", String.valueOf(nextChar)) ) {
						 //System.out.println("boolean: "+ (charClass != EOF));
						 addChar();
						 getChar();
//			
						 lexemeStr = new String(lexeme).split("\0")[0];
						 //System.out.println("lexStr: " + lexemeStr);
						 decConst = Pattern.matches(DecimalConstant, lexemeStr);
						 decConstNext = Pattern.matches(DecimalConstant, lexemeStr+String.valueOf(nextChar));
						 octConst = Pattern.matches(OctalConstant, lexemeStr);
						 octConstNext = Pattern.matches(OctalConstant, lexemeStr+String.valueOf(nextChar));
						 hexConst = Pattern.matches(HexadecimalConstant, lexemeStr);
						 hexConstNext = Pattern.matches(HexadecimalConstant, lexemeStr+String.valueOf(nextChar));
						 //if octal, hexadecimal, or has u,U,l,L,i,I cant be a floating point
						 if(octConst | octConstNext | hexConst | hexConstNext | Pattern.matches("[uUlLiI]", String.valueOf(nextChar)) ) {
							 canBeFloating = false;
						 }
						 //because stops at i6 since i6 doesnt match i64
						 if(lexemeStr.charAt(lexemeStr.length()-1) == 'i') {
							 if(nextChar == '6') {
								 hexConstNext = true;
							 }
						 }
						 
//						 System.out.println("dec: " + decConst);
//						 System.out.println("oct: " + octConst);
//						 System.out.println("hex: " + hexConst);
//						 System.out.println("decN: " + decConstNext);
//						 System.out.println("octN: " + octConstNext);
//						 System.out.println("hexN: " + hexConstNext);
//						 System.out.println(charClass);
					 }
					 if( Pattern.matches(".*[iI]6", lexemeStr) ) {nextToken = CANT_DEFINE;}
					 else {nextToken = INT_LIT;}
				 }
				 //check if number after period, if not number skip everything else with boolean and at end set nextToken to PERIOD
				 //if first char entering this case wasn't a period, keep checking entire lexeme against Integer regex in while loop adding a new character each time like it is now
				 //when it hits period, e, or E stop while loop and do new while loop and other logic checking against regex for floating point
				 if(canBeFloating | firstIsPeriod) {
					 boolean eStarts = false;
					 boolean isNotFloating = false; //if next character is not ., e, E then not floating
					 if(firstIsPeriod | nextChar == '.' ) {
						 if(!firstIsPeriod) {
							 //if firstIsPeriod then addChar and getChar already done once above
							 addChar();
							 getChar();
						 }
						 while (charClass == DIGIT_PER & nextChar != '.') {
							 addChar();
							 getChar();
						 }
						 if(nextChar == 'e' | nextChar == 'E') {
							 eStarts = true;
						 } else if (Pattern.matches("[fFlL]", String.valueOf(nextChar))) {
							 addChar();
							 getChar();
						 }
					 } else if(nextChar == 'e' | nextChar == 'E') {
						 eStarts = true;
					 } else {
						 isNotFloating = true;
					 }
					 
					 if(eStarts) {
						 addChar();
						 getChar();
						 if(nextChar == '-') {
							 addChar();
							 getChar();
						 }
						 while (charClass == DIGIT_PER & nextChar != '.') {
							 addChar();
							 getChar();
						 }
						 if( Pattern.matches("[fFlL]", String.valueOf(nextChar)) ) {
							 addChar();
							 getChar();
						 }
						 //System.out.println("e starts");
					 }
					 
					 if(!isNotFloating) {
						 nextToken = FLOAT_LIT;
					 }
				 }
				 
				 
				 
				 //at end determine if nextToken is Integer, floating point or just a period based on boolean values or something

				
			 	break;
			/* Parentheses and operators */
			 case UNKNOWN:
				 lookup(nextChar);
				 getChar();
				 break;
				/* EOF */
			case EOF:
				 nextToken = EOF;
				 lexeme[0] = 'E';
				 lexeme[1] = 'O';
				 lexeme[2] = 'F';
				 lexeme[3] = '\0';
			 	 break;
		 } /* End of switch */
		 /*printf("Next token is: %d, Next lexeme is %s\n",
		 nextToken, lexeme);*/
//		 if(charClass != EOF & charClass != UNKNOWN) {
//			 while(!symbols.contains(nextChar) & nextChar != ' ' ) {
//				 addChar();
//				 getChar();
//				 nextToken = CANT_DEFINE;
//			 }
//		 }
		 
		 //checks if there were 2 = in a row meaning the lexeme should be == instead of = and token should be EQUALITY_OP instead of ASSIGN_OP
		 if(nextToken == ASSIGN_OP) {
			 if(nextChar == '=') {
				 nextToken = EQUALITY_OP;
				 addChar();
				 getChar();
			 }
		 }
		 
		 lexemeStr = new String(lexeme).split("\0")[0]; //was printing the null character and non null characters after before changing to this line. Only store the part until first null char
		 
		 if(nextToken == IDENT) { //all reserved words would initially be identified as an identifier 
			 if(ReservedWordMap.containsKey(lexemeStr)) { //if identifier is actually a reserved word
				 nextToken = ReservedWordMap.get(lexemeStr); //set nextToken to the token code for that reserved word
			 }
		 }
		 
		 System.out.println("Next token is: {token value: "+nextToken+", token name: "+ TokenMap.get(nextToken)+"}, Next lexeme is "+ lexemeStr);
		 return nextToken;
	} /* End of function lex */
}



