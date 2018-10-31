//John Skinner
//Cullen Mollette
//James Harper
//CSC415 Assignment 4: Create the Lexer
//If you would like to see the github for this project I can add you as a collaborator
import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.Scanner;

public class POSLexer {
	Scanner sc;
	int length=0;
	char[] lexArray = new char[] {'+', '=','?', '!', '<','>','-',':','(',')'};
	char[] identifierArray = new char[] {'a','b','c','d','e','f','g','h','i','j','k','l','m','n',
			'o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J',
			'K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','_'};
	char[] numericArray = new char[] {'0','1','2','3','4','5','6','7','8','9'};
	String[] keywordArray = new String[] { "text", "if", "loop" };
	public static void main(String[] args) throws FileNotFoundException {
		POSLexer lt = new POSLexer();
		String filename;
		StringBuilder in=new StringBuilder();
		for(String s : args){
			in.append(s);
			in.append(" ");
		}
		filename = in.toString();
		System.out.println(filename);
		lt.sc = new Scanner(new File(filename));
		while(lt.sc.hasNext()){
			lt.lex(lt.sc.next());
		}		
	}	
	//Lex function Takes a string of any size and returns the lexical analysis
	public void lex(String str) {
		String currentString;
		char currentChar;
		//Formatting
		str = str.replaceAll("\"", "\\\"");
		str = str.replace("\n", " ");
		//Starting the loop
		currentString = str;			
		for(int i = 0; i<currentString.length();i++){
			currentChar = currentString.charAt(i);					
			if(checkLexeme(currentChar)) {
				defineLexeme(currentChar);
			}else if(checkComment(currentChar)) {
				defineComment(currentString); 
				break;
			}else if(checkString(currentChar)) {
				defineString(currentString);
				break;
			}else if(checkNumeric(currentChar)) {
				defineNumeric(currentString);
				break;
			}else {
				defineIdentifier(currentString);
				break;
			}	
		}	
	}
	
	//Define methods
	//This is where we return the definition string to be printed
	//Will take a char or String and print the definition
	private void defineKeyword(String s)
	{
		printString(s,"Keyword");
	}
	//Return the description of Lexeme
	//Switch statement to decide
	private void defineLexeme(char c){	
		switch(c){
			case '+': printChar(c,"Add/Concatenate");
			break;
			case '=': printChar(c,"Assignment");
			break;
			case '?': printChar(c,"Is Equal To");
			break;
			case '!': printChar(c,"Is Not Equal To");
			break;
			case '<': printChar(c,"Less Than");
			break;
			case '>': printChar(c,"Greater Than");
			break;
			case '-': printChar(c,"Negation");
			break;
			case ':': printChar(c,"Colon");
			break;
			case '(': printChar(c,"Open Parenthesis");
			break;
			case ')': printChar(c,"Close Parenthesis");
			break;
			default: printChar(c,"**Error: Invalid Character**");
		}	
	}
	//It takes the first " and then loops until the closing "
	//Check each token for " stop and print when found
	private void defineString(String s)	{
		StringBuilder strBuild = new StringBuilder("\"");
		boolean endStringFound = false;
		String nextString;
		for(int i=1; i<s.length(); i++){
			if(s.charAt(i) == '\"'){
				strBuild.append("\"");
				printString(strBuild.toString(), "String Literal");
				endStringFound = true;
			}
			else{
				strBuild.append(s.charAt(i));
			}
		}
		while(!endStringFound){
			if(sc.hasNext()){
				//Checks the rest of the file for the end "
				strBuild.append(" ");
				nextString = sc.next();
				for(int i=0; i<nextString.length(); i++){
					if(nextString.charAt(i) == '\"'){
						strBuild.append("\"");
						printString(strBuild.toString(), "String Literal");
						endStringFound = true;
					}else{
						strBuild.append(nextString.charAt(i));
					}	
				}
			}else{
				throw new InvalidParameterException("String Literal Never Closed");
			}
			
			
		}
	}
	//It takes the { and loops until }
	//Check each token for { stop and print when found
	private void defineComment(String s) {
		StringBuilder commentBuild = new StringBuilder("");
		boolean endCommentFound = false;
		String nextString;
		//Starts at 1 because we already have the first \"
		for(int i=0; i<s.length(); i++){
			//checks for final comment
			if(s.charAt(i) == '}'){
				commentBuild.append("}");
				printString(commentBuild.toString(), "Comment");
				endCommentFound = true;
			}else{
				commentBuild.append(s.charAt(i));
			}
		}
		while(!endCommentFound){
			commentBuild.append(" ");
			nextString = this.sc.next();
			for(int i=0; i<nextString.length(); i++){
				 if(nextString.charAt(i) == '}') {
					 commentBuild.append("}");
					 printString(commentBuild.toString(), "Comment");
					 endCommentFound = true;
				 }else{
					 commentBuild.append(nextString.charAt(i));							
				 }
			}					
		}
	}
	//Takes the first number, if 0 it prints
	//Otherwise it loops until it finds a non number
	//Then prints the number as a string NO NEED TO MAKE IT AN INT
	private void defineNumeric(String s) {
		StringBuilder strBuild = new StringBuilder();
		boolean identifierfound = false;
		for(int i=0; i<s.length();i++)
		{
			if(containsChar(numericArray,s.charAt(i)))
			{
				//Catches the 0 case
				if(s.charAt(i) == '0' && strBuild.toString().equals("")) {
					strBuild.append(s.charAt(i));
					printString(strBuild.toString(),"Numeric literal");
					//Clearing in case we need to make multiple numeric literals in one go
					strBuild = new StringBuilder();
				}else{
					strBuild.append(s.charAt(i));
				}
			}else{ //Valid for all non leading 0 cases
				if(!strBuild.toString().equals(""))	{
					printString(strBuild.toString(), "Numeric Literal");
				}
				strBuild = new StringBuilder();
				defineIdentifier(s.substring(i));
				identifierfound = true;
				break;
			}
		}
		if(!strBuild.toString().equals("")&&!identifierfound){
			printString(strBuild.toString(), "Numeric Literal");
		}
	}	
	//Refactored
	//defines Identifiers and checks against other lexeme types
	private void defineIdentifier(String s){
		StringBuilder identifierBuild = new StringBuilder("");
		char currentChar;
		//System.out.println("Define Identifier 2: " + s);
		for(int i=0; i<s.length(); i++){
			if(containsString(keywordArray, s));
		}
		for(int i = 0; i<s.length();i++){
			currentChar = s.charAt(i);			
			if(checkLexeme(currentChar)) {
				defineIdentifier(identifierBuild.toString());
				defineLexeme(currentChar);
				identifierBuild = new StringBuilder("");
			}else if(checkComment(currentChar)) {
				defineIdentifier(identifierBuild.toString());	
				defineComment(s.substring(i)); 
				identifierBuild = new StringBuilder("");
				break;
			}else if(checkString(currentChar)) {
				defineIdentifier(identifierBuild.toString());

				defineString(s.substring(i));
				identifierBuild = new StringBuilder("");
				break;
			}else if(checkNumeric(currentChar)) {
				defineIdentifier(identifierBuild.toString());
				identifierBuild = new StringBuilder(s.substring(i));
				defineNumeric(identifierBuild.toString());
				identifierBuild = new StringBuilder("");
				break;
			}else if(containsChar(identifierArray, currentChar)){
				identifierBuild.append(currentChar);
			}else{
				printString(identifierBuild.toString(), "Identifier");
				printString(Character.toString(currentChar), "**Error: Invalid Character**");
				identifierBuild = new StringBuilder("");
			}
		}
		if(containsString(keywordArray, identifierBuild.toString())){
			defineKeyword(identifierBuild.toString());
			identifierBuild = new StringBuilder("");
		}else if(identifierBuild.toString() != ""){
			printString(identifierBuild.toString(), "identifier");
		}
	}
	//Prints in desired format, Template changes happen here
	private void printChar(char output, String descriptor) {
		System.out.printf("%-30s %-10s\n", output, descriptor);
	}
	private void printString(String output, String descriptor) {
		if(!output.equals("")){
			System.out.printf("%-30s %-10s\n", output, descriptor);
		}
	}
	//Check Methods
	//Would rather put the logic here and not in the lex file
	private Boolean checkLexeme(char c){
		return containsChar(lexArray,c);
	}
	private Boolean checkComment(char c){
		return c == '{';
	}
	private Boolean checkString(char c){
		return c == '\"';
	}
	private Boolean checkNumeric(char c) {
		return containsChar(numericArray, c);
	}
	private boolean containsChar(char[] array, char c){
		boolean result = false;
        for(char index : array){
            if(index == c){
                result = true;
                break;
            }
        }
        return result;
    }
	private boolean containsString(String[] array, String c){
		boolean result = false;
        for(String index : array){
            if(index.equals(c)){
                result = true;
                break;
            }
        }
        return result;
    }
}
