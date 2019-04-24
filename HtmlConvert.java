import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;



public class HtmlConvert{
	
	protected enum Language_Types{
		C, CPP, JAVA, PYTHON, OTHER
	};

	protected enum Word_Types{
		MAROC, RESERVEDWORD, NUMBER, STRING, OPERATOR, NOTES, OTHER
	};
	
	static Language_Types language_Type;
	static int p = 0;
	static int lineNo = 0;
	protected static String[] OP_WORDS = {
			"!",     "%",     "&",     "(",     ")",
			"*",     "+",     ",",     "-",     ".",
			"/",     ":",     ";",     "<",     "=",
			">",     "?",     "[",     "]",     "^",
			"{",     "|",     "}",     "~"
	};
	
	protected static String[] C_KEY_WORDS = {
		    "auto",      "break",    "case",     "char",     "const",    "continue",   
		    "default",   "do",       "double",   "else",     "enum",     "extern",  
		    "float",     "for",      "goto",     "if",       "int",      "long",     
		    "register",  "return",   "short",    "signed",   "sizeof",   "static",
		    "struct",    "switch",   "typedef",  "union",    "unsigned", "void",        
		    "volatile",  "while"
   };
	
   protected static String[] CPP_KEY_WORDS = {
		   "asm",          "auto",        "bool",       "break",           "case",
		   "catch",        "char",        "class",      "const",           "const_cast",
		   "continue",     "default",     "delete",     "do",              "double",
		   "dynamic_cast", "else",        "enum",       "explicit",        "export",
		   "extern",       "false",       "float",      "for",             "friend",
		   "goto",         "if",          "inline",     "int",             "long",
		   "mutable",      "namespace",   "new",        "operator",        "private",
		   "protected",    "public",      "register",   "reinterpret_cast","return",
		   "short",        "signed",      "sizeof",     "static",          "static_cast",
		   "struct",       "switch",      "template",   "this",            "throw",
		   "true",         "try",         "typedef",    "typeid",          "typename",
		   "union",        "unsigned",    "using",      "virtual",         "void",
		   "volatile",     "wchar_t",     "while"   
   };
   
   protected static String[] JAVA_KEY_WORDS = {
		    "abstract",     "assert",       "boolean",    "break",       "byte", 
		    "case",         "catch",        "char",       "class",       "const", 
		    "continue",	    "default",	    "do",	      "double",	    "else",
		    "enum",	        "extends",	    "final",      "finally",	    "float",
		    "for",	        "goto",	        "if",	      "implements",  "import",
		    "instanceof",	"int",	        "interface",  "long",	    "native",
		    "new",	        "package",	    "private",	  "protected",   "public",
		    "return",	    "short",        "static",     "strictfp",    "super",
		    "switch",	    "synchronized",	"this",	      "throw",	    "throws",
		    "transient",	"try",	        "void",	      "volatile",	"while"
		};
   
   protected static String[] PYTHON_KEY_WORDS = {
		    "False",      "None",      "True",      "and",       "as",
		   
		    "assert",     "break",     "class",     "continue",  "def", 
		   
		    "del",        "elif",      "else",      "except",    "finally",
		   
		    "for",        "from",      "global",    "if",        "import",
		   
		    "in",         "is",        "lambda",    "nonlocal",  "not", 
		   
		    "or",         "pass",      "raise",     "return",    "try",
		   
		    "while",      "with",      "yield"

   };
   
   protected static String JS_CODE = "   		<script type=\"text/javascript\">\n"
           + "    var number = %d;\n"
           + "    window.onload = function() {\n"
           + "    numberBox = document.getElementById(\"numberBox\");\n"
           + "        for (i = 1; i <= number; i++) {\n"
           + "            node = document.createElement(\"div\");\n"
           + "            node.innerHTML = i;\n"
           + "            node.className = \"noBox\"; \n"
		   + "	          node.id=i;\n"
           + "            numberBox.appendChild(node);\n"
           + "        }\n"
           + "    }\n"
           + "</script>\n\n";
   
	private static String inFileName;
	private static String htmlTemplate;
	private static int occupyingkey;
   
   public HtmlConvert(String inFileName, String htmlTemplate, int occupyingkey){
	   this.inFileName = inFileName;
	   this.htmlTemplate = htmlTemplate;
	   this.occupyingkey = occupyingkey;
   }

   public static String ReplaceStr(String str0, String str1, int t){
		
		return str0.replaceAll(str1, t+"");
	}
   
   public static Language_Types Judg_Language_Types(String fileName){
	   
	   String fileExtension = fileName.split("\\.")[fileName.split("\\.").length - 1].toString();
	   fileExtension = fileExtension.toLowerCase();
	   Language_Types language_Types = Language_Types.OTHER;
	   switch (fileExtension) {
			case "c":
				language_Types = Language_Types.C;
				break;
				
			case "cpp":
				language_Types = Language_Types.CPP;
				break;
				
			case "java":
				language_Types = Language_Types.JAVA;
				break;
				
			case "py":
				language_Types = Language_Types.PYTHON;
				break;
		
			default:
				break;
		}
	   return language_Types;
   }
   
   public static boolean KeyWordJudgment(String[] KEY_WORDS, String word){
	   
	   int low, high, mid;
	   low = 0;
	   high = KEY_WORDS.length - 1 ;  
	   
	   while(low <= high)
	   {
	        mid = (low + high) / 2;    
	        if(word.compareTo(KEY_WORDS[mid]) < 0)
	            high = mid-1;
	        else if(word.compareTo(KEY_WORDS[mid]) > 0)
	            low = mid+1;
	        else
	            return true;
	    }
	   
	    return false;
   }
   
   public static void initHtmlHeader(BufferedReader fin, BufferedWriter fout) throws IOException{
	   
	   String str;
	   while ((str = fin.readLine()) != null) {
		   if(str.contains("$"))
			   return;
		   fout.write(str + "\n");
	   }
	   
   }
   
   public static void initHtmlTail(BufferedReader fin, BufferedWriter fout) throws IOException{
	   
	   String str;
	   while ((str = fin.readLine()) != null) {
		   fout.write(str + "\n");
	   }
	   
   }
   
   public static String getSourceCode(String fileName) throws IOException{
	   
	   String sourceCode = "";
	   String str;
	   File file = new File(fileName);
	   BufferedReader fp = null;
	   fp = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"));
	   while ((str = fp.readLine()) != null) {
		   sourceCode += str + "\n";
	   }
	   fp.close();
	   return sourceCode;
   }
   
   public static boolean isNumber(char s){
	   if((s <= 90 && s >= 65) || (s <= 122 && s >= 97) || (s >= 48 && s <= 57) || s == '.')
		   return true;
	   else
		   return false;
   }
   
   public static void StringBuilder(String temp, Word_Types wordType, BufferedWriter fout, int isLineEnd) throws IOException{
	   
	   String str = ""; 
	   
	   switch (wordType) {
			case MAROC:
				str += "<span class=\"maroc\">" + temp + "</span>";
				break;
			case RESERVEDWORD:
				str += "<span class=\"key\">" + temp + "</span>";
				break;
			case NUMBER:
				str += "<span class=\"number\">" + temp + "</span>";
				break;
			case STRING:
				str += "<span class=\"string\">" + temp + "</span>";
				break;
			case OPERATOR:
				str += "<span class=\"op\">" + temp + "</span>";
				break;
			case NOTES:
				str += "<span class=\"comment\">" + temp + "</span>";
				break;
			default:
				str += temp;
				break;
		}
	   if(isLineEnd == 1){
		   str += "\n" + "			</div>\n";
	       str += "			<div class=\"online\">";
	   }
	   fout.write(str);
	   
   }
   public static boolean isSpecialCharacter(char s){
	   
	   if(s == ' ' || s == '\t' || s == '<' || s == '>')
		   return true;
	   else
		   return false;
   }
   
   public static String specialCharacterConversion(char s){
	   
	    String str = "";
	    if(s == ' ')
		    str += "&nbsp;";
	    else if(s == '\t'){
		    for(int i = 0; i < occupyingkey - p%occupyingkey; i++)
			    str += "&nbsp;";
			p += occupyingkey - 1 - p%occupyingkey;
	    }
	    else if(s == '<')
			str += "&lt";
		else if(s == '>')
			str += "&gt";
	   p++;
	   return str;
   }
   public static void lexicalAnalysis(String sourceCode, String[] KEY_WORDS, BufferedWriter fout) throws IOException{
	   
	   char[] code = sourceCode.toCharArray();
	   fout.write("			<div class=\"online\">"); 
	   int codeP = 0;
	   while(codeP < sourceCode.length()){  
		   String temp = "";
		   Word_Types word_Types = Word_Types.OTHER;
		   int isLineEnd = 0;
		   if(code[codeP] == ' ' || code[codeP] == '\t')
		   {
			   temp += specialCharacterConversion(code[codeP++]); 
			   while(code[codeP] == ' ' || code[codeP] == '\t')
				   temp += specialCharacterConversion(code[codeP++]); 
		   }
			     
		   if(code[codeP] == '#'){
			   word_Types = Word_Types.MAROC;
			   temp += code[codeP++];
			   ++p;
			   boolean bl = false;
			   while((code[codeP] != '\n' || bl) && (code[codeP] != '/' && (code[codeP + 1] != '/' || code[codeP + 1] != '*'))){
				   if(code[codeP] == '\\')
						   bl = true;
				   if(code[codeP] == '\n'){
					   StringBuilder(temp, word_Types, fout, 1);
					   temp = "";
					   p = 0;
					   bl = false;
					   codeP++;
					   lineNo++;
				   }
				   if(isSpecialCharacter(code[codeP]))
					   temp += specialCharacterConversion(code[codeP++]);
				   else{
				       temp += code[codeP++];
				       ++p;
				   }
			   }
		   }
		   else if(Character.isDigit(code[codeP])){
			   word_Types = Word_Types.NUMBER;
			   temp += code[codeP++];
			   ++p;
			   while(isNumber(code[codeP])){
				   if(isSpecialCharacter(code[codeP]))
					   temp += specialCharacterConversion(code[codeP++]);
				   else{ 
					   temp += code[codeP++];
					   ++p;
				   }
			   }   
		   }
		   
		   else if(code[codeP] == '/' &&  (code[codeP + 1] == '/' || code[codeP + 1] == '*')){
			   word_Types = Word_Types.NOTES;
			   temp += code[codeP++];
			   ++p;
			   if(code[codeP] == '/'){
				   while(code[codeP] != '\n'){  
					   if(isSpecialCharacter(code[codeP]))
						   temp += specialCharacterConversion(code[codeP++]);
					   else{
						   temp += code[codeP++];
						   ++p;
					   }
				   }
					   
			   }
			   else{
				   			   
				   while(code[codeP] != '*' || code[codeP + 1] != '/'){
					   if(code[codeP] == '\n'){
						   StringBuilder(temp, word_Types, fout, 1);
						   temp = "";
						   p = 0;
						   codeP++;
						  
						   lineNo++;
						   continue;
					   }
					   if(isSpecialCharacter(code[codeP]))
						   temp += specialCharacterConversion(code[codeP++]);
					   else{
						   temp += code[codeP++];
						   ++p;
					   }
				   }
				   temp += "*/";
				   codeP += 2; 
				   p += 2;
			   }
		   }
		   
		   else if(code[codeP] == '"' || code[codeP] == '\''){
			   int n = 0;
			   word_Types = Word_Types.STRING;
			   if(code[codeP] == '"'){
				   temp += code[codeP++];
				   ++p;
				   while(code[codeP] != '"' || code[codeP - 1] == '\\'){
					   if(code[codeP] == '\n')
					   {
						   p = 0;
						   lineNo++;
						   if(code[codeP - 1] != '\\')
						       break;
						   else {
							   StringBuilder(temp, word_Types, fout, 1);
							   temp = "";
							   codeP++;
						   }
					   }
						   
			
					   if(isSpecialCharacter(code[codeP]))
						   temp += specialCharacterConversion(code[codeP++]);
					   else{
						   temp += code[codeP++];
						   ++p;
					   }
				   }
				   if(code[codeP] == '"'){
					   temp += code[codeP++];
					   ++p;
				   }
			   }
			   else{
				   temp += code[codeP++];
				   ++p;
				   while(code[codeP] != '\'' || code[codeP - 1] == '\\'){
					   if(code[codeP] == '\n'){
						   p = 0;
						   if(code[codeP - 1] != '\\')
							       break;
						   else {
							   StringBuilder(temp, word_Types, fout, 1);
							   temp = "";
							   codeP++;
							   lineNo++;
						   }
					   }
					   if(isSpecialCharacter(code[codeP]))
						   temp += specialCharacterConversion(code[codeP++]);
					   else{
						   temp += code[codeP++];
						   ++p;
					   }
				   }
				   if(code[codeP] == '\''){
					   temp += code[codeP++];
					   ++p;
				   }
			   }
			   
		   }
		   
		   else if(KeyWordJudgment(OP_WORDS, code[codeP] + "")){
			    word_Types = Word_Types.OPERATOR;
			    if(code[codeP] == '<')
				    temp += "&lt";
			    else if(code[codeP] == '>')
				    temp += "&gt";
			    else
				    temp += code[codeP];
			    codeP++;
			    ++p;
		   }
		   
		   else if(Character.isLetter(code[codeP]) || code[codeP] == '_'){   
			   String temp1 = "";
			   temp1 += code[codeP++];
			   ++p;
			   while(Character.isLetter(code[codeP]) || code[codeP] == '_' || Character.isDigit(code[codeP])){
				   temp1 += code[codeP++];
				   ++p;
			   }
			   
			   if(KeyWordJudgment(KEY_WORDS, temp1)){
				   word_Types = Word_Types.RESERVEDWORD;
			   }
			   temp += temp1;
		   }
		   else if(code[codeP] == '\n'){
			   
			   p = 0;
			   isLineEnd = 1;
			   codeP++;
			   lineNo++;
		   }
		   else{
			   word_Types = Word_Types.OTHER;
			   while(code[codeP] != '\n'){ 
				   if(isSpecialCharacter(code[codeP]))
					   temp += specialCharacterConversion(code[codeP++]);
				   else{
					   temp += code[codeP++];
					   ++p;
				   }
			   }
		   }
		StringBuilder(temp, word_Types, fout, isLineEnd);
	   }
	   fout.write("			</div>\n");
   }

   public boolean wbaht(){
	   
	   try{
		   String outFileName = inFileName.split("\\.")[0].toString() + ".html";
		   File intemplateFile = new File(htmlTemplate);
		   File outHtmlFile = new File(outFileName);
		   BufferedReader fin = null;
	       BufferedWriter fout = null;
	       String[] KEY_WORDS;
	       fin = new BufferedReader(new InputStreamReader(new FileInputStream(intemplateFile), "gbk"));
		   fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outHtmlFile), "gbk"));
		   String sourceCode = getSourceCode(inFileName);
		   language_Type = Judg_Language_Types(inFileName);
	
		   if(language_Type == Language_Types.JAVA)
			   KEY_WORDS = JAVA_KEY_WORDS;
		   else if(language_Type == Language_Types.CPP)
			   KEY_WORDS = CPP_KEY_WORDS;
		   else if(language_Type == Language_Types.PYTHON)
			   KEY_WORDS = PYTHON_KEY_WORDS;
		   else 
			   KEY_WORDS = C_KEY_WORDS;
		   initHtmlHeader(fin, fout);
		   lineNo = 0;
		   lexicalAnalysis(sourceCode, KEY_WORDS, fout);
		   String JS= ReplaceStr(JS_CODE, "%d", lineNo);;
		   fout.write(JS);
		   initHtmlTail(fin, fout); 
		   fin.close();
		   fout.close();
	   }catch(Exception e){
		   return false;
	   }
	   return true;
   }
   public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	  
	}
   
}