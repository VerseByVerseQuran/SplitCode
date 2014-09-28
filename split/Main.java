/*
 * Copyright versebyversequran.com
 * Licensed under Apache 2.0
 * Main.java
 *
 * Created on 13 December 2006, 23:53
 *
 */

package split;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author john
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String timesFileName = null; /* Name of file to hold timings */
        String mp3FileName = null; /* Name of mp3 to be split */
        Main m = null;
        final String padString = "000";
        
        if(args.length == 0) {
            String seqName = null;
            m = new Main();
            
            for(int i=1; i<115; i++) {
                seqName = (new Integer(i)).toString();
                seqName = padString.substring(seqName.length()) + seqName;
                
                m.split(seqName + ".mp3", seqName + ".txt");
            }
            
            return;
            
        } else if(args.length != 2) {
            System.out.println("Usage: <Program> <mp3> <file>");
            System.out.println("<mp3>: Name of mp3 file, example: foo.mp3");
            System.out.println("<file>: Name of timing file, example timing.txt");
            return;
        }
        
        
        mp3FileName = args[0];
        timesFileName = args[1];
        
        m = new Main();
        
        m.split(mp3FileName, timesFileName);
        
    }
    
    public void split(String mp3FileName, String timesFileName) {
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(timesFileName)));
            int splitnumber = 0;
            String splitstring = null;
            final String padstring = "000";
            String chapterstring = null;
            String temp = null; /* String to store the read line */
            String starttime = null; /* temp string to hold start time */
            String endtime = null; /* temp string to hold end time */
            String fullsplitname = null; /* temp string to hold full mp3 name of verse */
            String fullcommand = null; /* temp string to hold full command to be executed */
            long millitime = 0; /* long to store parsed millisecond value */
            long prevmillitime = 0; /* dummy long to store previously read millisecond value, inorder to form range */
            int startminutecount = 0; /* temp integer to hold modified minutes */
            int endminutecount = 0; /* temp integer to hold modified minutes */
            Calendar refCal = null; /* a reference calendar to see if the hour changed */
            Calendar cal = null;
            Calendar prevcal = null;
            
            /* a reference calendar to compare if we moved in the hour section or not */
            refCal = Calendar.getInstance();
            refCal.setTimeInMillis(0);
            
            chapterstring = mp3FileName.substring(0, mp3FileName.length() - 4);
            
            while((temp = br.readLine()) != null) {
                
                temp = this.fixMe(temp);
                
                millitime = Long.parseLong(temp);
                
                cal = Calendar.getInstance();
                prevcal = Calendar.getInstance();
                
                
                cal.setTimeInMillis(millitime);
                prevcal.setTimeInMillis(prevmillitime);
                
                /* check if we moved an hour and change that into minute count */
                startminutecount = (prevcal.get(Calendar.HOUR_OF_DAY) - refCal.get(Calendar.HOUR_OF_DAY))*60;
                endminutecount = (cal.get(Calendar.HOUR_OF_DAY) - refCal.get(Calendar.HOUR_OF_DAY))*60;
                
                starttime = (startminutecount + prevcal.get(Calendar.MINUTE)) + "." + prevcal.get(Calendar.SECOND) + "." + this.roundMe(prevcal.get(Calendar.MILLISECOND));
                endtime = (endminutecount + cal.get(Calendar.MINUTE)) + "." + cal.get(Calendar.SECOND) + "." + this.roundMe(cal.get(Calendar.MILLISECOND));
                
                /* make up the name of the mp3 */
                splitstring = (new Integer(splitnumber)).toString();
                splitstring = padstring.substring(splitstring.length()) + splitstring;
                
                fullsplitname = chapterstring + splitstring + ".mp3";
                
                fullcommand = "mp3splt \"" + mp3FileName + "\" " + starttime + " " + endtime + " -o \"" + fullsplitname + "\" -d out";
                
                /* Execute the command in another process */
                System.out.println("Executing " + fullcommand);
                
                Process proc = Runtime.getRuntime().exec(fullcommand);
                
                InputStream inputstream = proc.getErrorStream();
                InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
                BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
                
                
                
                String line;
                while ((line = bufferedreader.readLine())
                != null) {
                    System.out.println(line);
                }
                
                              
                
                prevmillitime = millitime;
                splitnumber++;
                
            }
        } catch(FileNotFoundException fnfe) {
            System.out.println("File not found");
            fnfe.printStackTrace();
        } catch(IOException ioe) {
            System.out.println("IO Exception");
            ioe.printStackTrace();
        }
        
    }
    
    /*
     * A method to round a 3 digit number to the nearst ten. It turns the numbers such as 345 to 35 for example.
     * Params:
     * - toRound: The number to round
     * Return: The rounded number
     *
     */
    public int roundMe(int toRound) {
        if(toRound < 100) {
            return toRound;
        }
        
        if(toRound%10 < 5) {
            toRound = (int)toRound/10;
        } else {
            toRound = (int)toRound/10;
            
            if(toRound < 99)
            {
                toRound++;
            }
            
        }
        
        return toRound;
    }
    
    
    /*
     * A method to fix a weird way of handling numbers. Where millisecond values as 097 are interpreted as 970 milliseconds.
     * This is probably because the zero on the lhs isn't significant, but while reading the numbers as string, it actually is.
     * This turns numbers such as 51097 into 51000 since 97 milliseconds cann't even be distinguished when we're dealing with
     * sound files.
     * Params:
     * - milli: The number string of milliseconds
     * Return: The fixed string
     *
     */
    public String fixMe(String milli) {
        String trueMilli = milli.substring(milli.length() - 3);
        String prev = milli.substring(0, milli.length() - 3);
        
        int firstdigit = Integer.parseInt(trueMilli.substring(0, 1));
        int seconddigit = Integer.parseInt(trueMilli.substring(1, 2));
        int thirddigit  = Integer.parseInt(trueMilli.substring(2, 3));
        
        /* do the special rounding */
        if( firstdigit == 0) {
            if(seconddigit < 5) {
                seconddigit = 0;
                thirddigit = 0;
            } else {
                firstdigit = 1;
                seconddigit = 0;
                thirddigit = 0;
            }
            
            /* and re-construct the string */
            trueMilli = firstdigit + "" + seconddigit + "" + thirddigit;
        }
        
        return prev + trueMilli;
        
    }
    
}
