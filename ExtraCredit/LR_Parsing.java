import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class LR_Parsing {
	
	//for Action
	final static int ID_INDEX = 0;
	final static int PLUS_INDEX = 1;
	final static int MULT_INDEX = 2;
	final static int LEFT_PAREN_INDEX = 3;
	final static int RIGHT_PAREN_INDEX = 4;
	final static int DOLLAR_SYM_INDEX = 4;
	//for Goto
	final static int E_INDEX = 0;
	final static int T_INDEX = 1;
	final static int F_INDEX = 2;
	
	//for return values of parse
	final static int DONE = 0;
	final static int NOT_DONE = 1;
	
	//used to get integer index value for token
	static HashMap<String, Integer> symbolToIndex = new HashMap<String, Integer>();
	
	//store Action and Goto values from the LR parsing table
	static String[][] ActionTable = new String[12][6];
	static String[][] GotoTable = new String[12][3];
	//Rules[i][0] = LHS of rule
	//Rules[i][1] = number of symbols on RHS. will pop Rules[i][1]*2 from stack when Reducing since there is a symbol and state pair
	static String[][] Rules = new String[6][2];
	
	//for input and the current stack
	static Stack<String> input = new Stack<String>();
	static Stack<String> stack = new Stack<String>();
	
	public static void main(String[] args) {
		//initialize HashMap
		initializeHashMap();
		
		//initialize the 2d arrays that store the table values based on the indices
		initializeArrays();
		
		//initialize input and stack
		input.push("$");
		String[] inputString = {"id","+","id","*", "id"};
		//String[] inputString = {"id","+","+","*", "id"};
		for(int i = inputString.length-1; i  >= 0; i--) {
			input.push(inputString[i]);
		}
		
		
		stack.push("0");

//		stack.push(input.pop());
//		stack.push(input.pop());
//		System.out.println(Arrays.toString(stack.toArray()));

//		for(int i = 0; i < ActionTable.length; i++) {
//			for(int j = 0; j < ActionTable[i].length; j++) {
//				System.out.print(ActionTable[i][j] + " ");
//			}
//			System.out.println();
//		}
//		if(ActionTable[0][1] == null) {
//			System.out.println("was null");
//		}
		
		boolean isDone = false;
		while(!isDone & count < 100) { //just in case program doesn't terminate, stop while loop when parse called 100 times
			//System.out.println("in while");
			int doneOrNot = NOT_DONE;
			try {
				doneOrNot = parse(); //parse returns DONE or NOT_DONE which are type int
			} catch(Exception exc) { //might have some exception popping from stacks
				System.out.println(exc);
			}
			
			if(doneOrNot == DONE) {
				isDone = true; //no longer need to check input anymore
			}
		}
	}
	
	static void initializeHashMap() {
		//symbolToIndex
		
		//Action
		symbolToIndex.put("id", ID_INDEX);
		symbolToIndex.put("+", PLUS_INDEX);
		symbolToIndex.put("*", MULT_INDEX);
		symbolToIndex.put("(", LEFT_PAREN_INDEX);
		symbolToIndex.put(")", RIGHT_PAREN_INDEX);
		symbolToIndex.put("$", DOLLAR_SYM_INDEX);
		
		//Goto
		symbolToIndex.put("E", E_INDEX);
		symbolToIndex.put("T", T_INDEX);
		symbolToIndex.put("F", F_INDEX);
		
		
		
		
		//ruleNumber_To_numberSymbolsRHS
		//ruleNumber_To_numberSymbolsRHS.put();
	}
	
	static void initializeArrays() {
		//ActionTable
		
		//state 0
		ActionTable[0][ID_INDEX] = "S,5";
		ActionTable[0][LEFT_PAREN_INDEX] = "S,4";
		//state 1
		ActionTable[1][PLUS_INDEX] = "S,6";
		ActionTable[1][DOLLAR_SYM_INDEX] = "accept";
		//state 2
		ActionTable[2][PLUS_INDEX] = "R,2";
		ActionTable[2][MULT_INDEX] = "S,7";
		ActionTable[2][RIGHT_PAREN_INDEX] = "R,2";
		ActionTable[2][DOLLAR_SYM_INDEX] = "R,2";
		//state 3
		ActionTable[3][PLUS_INDEX] = "R,4";
		ActionTable[3][MULT_INDEX] = "R,4";
		ActionTable[3][RIGHT_PAREN_INDEX] = "R,4";
		ActionTable[3][DOLLAR_SYM_INDEX] = "R,4";
		//state 4
		ActionTable[4][ID_INDEX] = "S,5";
		ActionTable[4][LEFT_PAREN_INDEX] = "S,4";
		//state 5
		ActionTable[5][PLUS_INDEX] = "R,6";
		ActionTable[5][MULT_INDEX] = "R,6";
		ActionTable[5][RIGHT_PAREN_INDEX] = "R,6";
		ActionTable[5][DOLLAR_SYM_INDEX] = "R,6";
		//state 6
		ActionTable[6][ID_INDEX] = "S,5";
		ActionTable[6][LEFT_PAREN_INDEX] = "S,4";
		//state 7
		ActionTable[7][ID_INDEX] = "S,5";
		ActionTable[7][LEFT_PAREN_INDEX] = "S,4";
		//state 8
		ActionTable[8][PLUS_INDEX] = "S,6";
		ActionTable[8][RIGHT_PAREN_INDEX] = "S,11";
		//state 9
		ActionTable[9][PLUS_INDEX] = "R,1";
		ActionTable[9][MULT_INDEX] = "S,7";
		ActionTable[9][RIGHT_PAREN_INDEX] = "R,1";
		ActionTable[9][DOLLAR_SYM_INDEX] = "R,1";
		//state 10
		ActionTable[10][PLUS_INDEX] = "R,3";
		ActionTable[10][MULT_INDEX] = "R,3";
		ActionTable[10][RIGHT_PAREN_INDEX] = "R,3";
		ActionTable[10][DOLLAR_SYM_INDEX] = "R,3";
		//state 11
		ActionTable[11][PLUS_INDEX] = "R,5";
		ActionTable[11][MULT_INDEX] = "R,5";
		ActionTable[11][RIGHT_PAREN_INDEX] = "R,5";
		ActionTable[11][DOLLAR_SYM_INDEX] = "R,5";
		
		
		
		//Goto Table
		
		//state 0
		GotoTable[0][E_INDEX] = "1";
		GotoTable[0][T_INDEX] = "2";
		GotoTable[0][F_INDEX] = "3";
		//state 4
		GotoTable[4][E_INDEX] = "8";
		GotoTable[4][T_INDEX] = "2";
		GotoTable[4][F_INDEX] = "3";
		//state 6
		GotoTable[6][T_INDEX] = "9";
		GotoTable[6][F_INDEX] = "3";
		//state 7
		GotoTable[7][F_INDEX] = "10";
		
		
		
		
		
		//Rules
		
		//rule 1
		Rules[0][0] = "E";
		Rules[0][1] = "3";
		//rule 2
		Rules[1][0] = "E";
		Rules[1][1] = "1";
		//rule 3
		Rules[2][0] = "T";
		Rules[2][1] = "3";
		//rule 4
		Rules[3][0] = "T";
		Rules[3][1] = "1";
		//rule 5
		Rules[4][0] = "F";
		Rules[4][1] = "3";
		//rule 6
		Rules[5][0] = "F";
		Rules[5][1] = "1";		
	}
	static int count = 0;
	static int parse() throws Exception{
		count++;
		int state = Integer.parseInt(stack.peek());
		int symbol = symbolToIndex.get(input.peek()); //which column based on the token
		//System.out.println("state: " + state + "   column: " + column);
		String action = ActionTable[state][symbol];
		String[] actionSplit = {};
		
		System.out.println("Stack: " + Arrays.toString(stack.toArray()));
		System.out.println("Input: " + Arrays.toString(input.toArray()));
		System.out.print("Action: " + action);
		//System.out.println();
		
		if(  ( action != null ) && ( !action.equals("accept") )  )  { //short circuit and (&&) because if null then checking .equals(null) will throw exception
			actionSplit = action.split(","); //actions have comma separators in the ActionTable
			//System.out.println("Action Split: " + actionSplit[0] + " " + actionSplit[1] );
		}
		else  {
			if(action == null) { //goes first and returns so that .equals(null) on next if will never occur
				System.out.println("\n\nInput WAS NOT accepted.");
				return DONE;
			}
			if(action.equals("accept")) {
				System.out.println("\n\nInput WAS accepted.");
				return DONE;
			}
			
		}
		//System.out.println("before shift");
		
		//shift
		if(actionSplit[0].equals("S")) { //can't compare strings with == Need to use .equals()
			//System.out.println("in shift");
			stack.push(input.pop()); //top of input stack pushed onto stack
			stack.push(actionSplit[1]); //next state from ActionTable pushed onto stack
		}
		
		//reduce
		if(actionSplit[0].equals("R")) {
			int rule = Integer.parseInt(actionSplit[1]); //get rule
			int numToPop = Integer.parseInt(Rules[rule-1][1]); //get number of symbols on RHS of rule
			for(int i = 0; i < numToPop*2; i++) {
				stack.pop(); //pop number of symbols*2 because each symbol is paired with state in stack
			}
			state = Integer.parseInt(stack.peek()); //state in stack after popping above is done
			String LHS = Rules[rule-1][0]; //get symbol in LHS of the rule
			symbol = symbolToIndex.get(LHS); //get int value of column index for that LHS symbol
			String nextState = GotoTable[state][symbol]; //use state and symbol to find next state in GotoTable
			System.out.print(" (use GOTO["+state+", "+ LHS+"])");
			stack.push(LHS); //push LHS of rule onto stack
			stack.push(nextState); //push the state found from the GotoTable onto stack
		}
		
		System.out.println("\n");
		//didn't find an empty(null) action or the accept action, so the input can should still be parsed.
		return NOT_DONE;
	}
}
