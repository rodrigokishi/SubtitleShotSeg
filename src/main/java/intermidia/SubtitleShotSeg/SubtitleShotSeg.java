package intermidia.SubtitleShotSeg;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.fredy.jsrt.api.SRT;
import org.fredy.jsrt.api.SRTInfo;
import org.fredy.jsrt.api.SRTReader;

import TVSSUnits.Shot;
import TVSSUnits.ShotList;
import TVSSUtils.ShotReader;

public class SubtitleShotSeg 
{
    public static void main( String[] args ) throws Exception
    {
        SRTInfo subtitleList = SRTReader.read(new File(args[0]));                
        ShotList shotList = ShotReader.readFromCSV(args[1]);
        float FPS = Integer.parseInt(args[2]);
        
        /*Iterator<SRT> subtitleIterator = subtitleList.iterator();*/
        Iterator<Shot> shotIterator = shotList.getList().iterator();
        int shotIndex = 1;
        int writtenShotIndex = 0;
        ArrayList<String> shotSubtitles = new ArrayList<String>();
        
        Shot actualShot;     
        actualShot = shotIterator.next();
        
        for(SRT actualSubtitle: subtitleList)
        {
        	float actualSubtitleFrameIndex = convertToFrame(actualSubtitle.startTime, FPS); 

        	while(shotIterator.hasNext() && actualSubtitleFrameIndex >= actualShot.getEndBoundary())
        	{
        		actualShot = shotIterator.next();
        		if(writtenShotIndex < shotIndex)
        		{
        			shotSubtitles.add(shotIndex + " \"\"");
        		}
        		shotIndex++;
        	}
        	if(actualSubtitleFrameIndex < actualShot.getEndBoundary())
        	{           	
        		shotSubtitles.add(shotIndex + " \"" + clearHtml(actualSubtitle) + "\"");
        		//shotSubtitles.add(shotIndex + " " + actualSubtitle.toString());
        		if(writtenShotIndex < shotIndex)
        		{
        			writtenShotIndex = shotIndex;
        		}
        	}
        	
        }
        
        //Cast away last shot with empty text if its index already appeared on output
        if(shotIterator.hasNext() && writtenShotIndex == shotIndex)
        {
        	shotIndex++;
        	shotIterator.next();
        }
        
        //To exhaust final shots without subtitles
        while(shotIterator.hasNext())
        {
       		shotSubtitles.add(shotIndex++ + " \"\"");
        	shotIterator.next();
        }
        shotSubtitles.add(shotIndex + " \"\"");
    	
      
        FileWriter subtitleWriter = new FileWriter(args[3]);                
        for(String shotSubtitle : shotSubtitles)
        {        	
        	subtitleWriter.write(shotSubtitle + "\n");
        }
        subtitleWriter.close();
    }
        
	private static float convertToFrame(Date time, float FPS)    
	{  	
		//Must subtract getTimezoneOffset because it adds 3 hours of brazilian timezone
		@SuppressWarnings("deprecation")
		float elapsedSecs = ((float)time.getTime() / (float)1000) - (time.getTimezoneOffset() * 60); 
    	return elapsedSecs * FPS;     	    	
    }
	
	private static String clearHtml(SRT subtitle)
	{
		String fullLine = "";
    	for(String text : subtitle.text)
    	{        		
    		if(fullLine.compareTo("") != 0)
    		{
    			fullLine += " " + text;
    		}
    		else
    		{
    			fullLine = text;
    		}
    	}
    	return fullLine.replaceAll("\\<.*?>",""); 
	}
}
