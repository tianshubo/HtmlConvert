import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class Wbaht {
	
	private static String rootDirectory;
	private static String htmlTemplate;
	private static int occupyingkey;
	static BufferedWriter log = null;
	
	protected static String INDEX_HEADER = 
			   "<!DOCTYPE html>\n"
	           + "<html lang=\"en\">\n"
	           + "    <head>\n"
	           + "		<meta charset=\"GBK\">\n"
	           + "	</head>\n"
	           + "	<body>\n";
	protected static String INDEX_TAIL = "	 </body>" + "</html>";

	
	public static void convertHtml(String infileName) throws Exception{
		HtmlConvert htmlConvert = new HtmlConvert(infileName, htmlTemplate, occupyingkey);
		String s = "\n" + infileName + "The file is being converted....\n";
		if(htmlConvert.wbaht())
			s += infileName + "Successful File Conversion\n";
		else
			s += infileName + "File conversion failed\n";
		log.write(s);
	}
	
	
	
	public static void listDir(String pathName) throws Exception{
		
		BufferedWriter fp = null;
		File file = new File(pathName);
		String contentStr = INDEX_HEADER;
		File indexFile = new File(pathName + "/index.html");
		fp = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(indexFile), "gbk"));
		contentStr += "        <h1>Index of /"+ file.getName() + "</h1><hr>\n";
		if (pathName.compareTo(rootDirectory) != 0)
	        contentStr += "        <a href=\"../index.html\">../</a><br>\n";
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                return;
            } 
            else {
            	List<File> fileList = new ArrayList<File>();
            	for (File file2 : files) 
            		fileList.add(file2);
            	Collections.sort(fileList, new Comparator<File>(){
            	
					@Override
					public int compare(File file1, File file2) {
						// TODO Auto-generated method stub
						if (file1.isDirectory() && file2.isFile())
            	            return -1;
            	        if (file1.isFile() && file2.isDirectory())
            	            return 1;
            	        return file1.getName().compareTo(file2.getName());
					}
            	});
                for (File file2 : fileList) {
                	
                    if (file2.isDirectory()) {
                    	contentStr += "        <a href=\""+ file2.getName() +"/index.html\">"
                                + file2.getName() +"</a><br>\n";
                        listDir(file2.getAbsolutePath());
                    }
                    else {
						String str = file2.getName().split("\\.")[file2.getName().split("\\.").length - 1].toString();
						if(!str.equals("html") && !str.equals("log")){
							contentStr += "        <a href=\""+ file2.getName().split("\\.")[0].toString() + ".html\">" 
									+ file2.getName() +"</a><br>\n";
							convertHtml(file2.getAbsolutePath());
						}
                    }
                       
                }
            }
        } 
        else 
            System.out.println("Documents do not exist!");
        contentStr += INDEX_TAIL;
        fp.write(contentStr);
        fp.close();
	}
	public static void useage(){
		System.out.println("\nYou must give the program three parameters,");
		System.out.println("      -t ");
		System.out.println("        show how many spaces before indentation");
		System.out.println("      -p");
		System.out.println("        where the template file is (with html suffix)");
		System.out.println("      -d");
		System.out.println("        which directory to be converted\n");
		System.out.println(" eaxple:");
		System.out.println("        java Wbaht -t 4 -p token.html -d C:\\Users\\Administrator\\Desktop\\tian");
	}
	public static void main(String[] args) throws Exception {
		long startTime =  System.currentTimeMillis();
		
		if(args.length != 6){
			System.out.println("The parameters must be six");
			useage();
			return;
		}
		else if(!args[0].equals("-t") || !args[2].equals("-p") || !args[4].equals("-d")){
			System.out.println("Error in parameter input");
			useage();
			return;
		}	
		occupyingkey = Integer.parseInt(args[1]);
		htmlTemplate = args[3];
		rootDirectory = args[5];
		File journalFile = new File(rootDirectory + "/journal.log");
		
		log = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(journalFile), "gbk"));
		listDir(rootDirectory);
		log.close();
		long endTime =  System.currentTimeMillis();
		long usedTime = (endTime-startTime)/1000;
		System.out.println("execution time: " + usedTime + "s");
	}
}
